package us.jcedeno.anmelden.bukkit.skins.utils;

import static com.comphenix.protocol.PacketType.Play.Server.PLAYER_INFO;
import static com.comphenix.protocol.PacketType.Play.Server.PLAYER_INFO_REMOVE;
import static com.comphenix.protocol.PacketType.Play.Server.POSITION;
import static com.comphenix.protocol.PacketType.Play.Server.RESPAWN;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.FuzzyReflection;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.utility.MinecraftVersion;
import com.comphenix.protocol.wrappers.BukkitConverters;
import com.comphenix.protocol.wrappers.Converters;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.Difficulty;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.common.hash.Hashing;

import lombok.extern.log4j.Log4j2;
import us.jcedeno.anmelden.bukkit.MonadUHC;
import us.jcedeno.anmelden.bukkit.skins.model.PlayerSkin;

/**
 * 
 * A utility class that handles applying a skin to a player with all the
 * newances of newer game versions. Intended to be used in tandem with a system
 * that manages the state of the skins generated and currently active, as well
 * as keeping a way to restore back to original skin.
 * 
 * @author jcedeno
 * @author games647 - took some code from him -
 *         https://github.com/games647/ChangeSkin/blob/main/bukkit/src/main/java/com/github/games647/changeskin/bukkit/task/SkinApplier.java
 */
@Log4j2
public class SkinLib {
    /*
     * All of this is black magic we don't need to understand. It's a bunch of
     * reflection to figure out what methods to properly call from bukkit
     * dependening on the current game version.
     */
    private static final boolean NEW_HIDE_METHOD_AVAILABLE;
    private static final Method DEBUG_WORLD_METHOD;

    private static final Method PLAYER_HANDLE_METHOD;
    private static final Field INTERACTION_MANAGER;
    private static final Field GAMEMODE_FIELD;

    private static final boolean DISABLED_PACKETS;

    static {
        boolean methodAvailable;
        try {
            Player.class.getDeclaredMethod("hidePlayer", Plugin.class, Player.class);
            methodAvailable = true;
        } catch (NoSuchMethodException noSuchMethodEx) {
            methodAvailable = false;
        }

        boolean localDisable = false;
        Method localDebugWorld = null;

        Method localHandleMethod = null;
        Field localInteractionField = null;
        Field localGamemode = null;

        // use standard reflection if possible, MethodHandles are only clearly faster
        // with invokeExact
        // we can use for a nested call of debug world:
        // getDebugField(getNMSWorldFromBukkit) in a single call
        // But for the resourceKey the return type is not known at compile time - it's
        // an NMS class
        if (isAtOrAbove("1.16")) {
            try {
                Class<?> nmsWorldClass = MinecraftReflection.getNmsWorldClass();

                // in comparison to the field values is this not obfuscated in 1.16 and 1.17
                if (isAtOrAbove("1.18")) {
                    localDebugWorld = nmsWorldClass.getDeclaredMethod("ad");
                } else {
                    localDebugWorld = nmsWorldClass.getDeclaredMethod("isDebugWorld");
                }

                localHandleMethod = MinecraftReflection.getCraftPlayerClass().getDeclaredMethod("getHandle");

                String INTERACTION_CLASS = "PlayerInteractManager";
                Class<?> interactionManager = MinecraftReflection.getMinecraftClass(
                        "server.level." + INTERACTION_CLASS, INTERACTION_CLASS);

                Class<?> entityPlayerClass = MinecraftReflection.getEntityPlayerClass();
                localInteractionField = FuzzyReflection.fromClass(entityPlayerClass)
                        .getFieldByType("playerInteractManager", interactionManager);
                localInteractionField.setAccessible(true);

                localGamemode = getPreviousGamemodeField(interactionManager);
                localGamemode.setAccessible(true);
            } catch (NoSuchFieldException | NoSuchMethodException reflectiveEx) {
                log.warn("Cannot find packet fields", reflectiveEx);
                localDisable = true;
            }
        }

        NEW_HIDE_METHOD_AVAILABLE = methodAvailable;

        DEBUG_WORLD_METHOD = localDebugWorld;
        PLAYER_HANDLE_METHOD = localHandleMethod;
        INTERACTION_MANAGER = localInteractionField;
        GAMEMODE_FIELD = localGamemode;
        DISABLED_PACKETS = localDisable;
    }

    // jcedeno's version of the skinlib starts here.

    /**
     * A helper function that returns a WrappedGameProfile for a player with the
     * provided Skin properties.
     * 
     * @param player the player to get the WrappedGameProfile for.
     * @param skin   the skin properties to apply to the WrappedGameProfile.
     * @return the WrappedGameProfile with the provided skin properties.
     */
    public static WrappedGameProfile getUpdatedGameProfile(final Player player, final PlayerSkin skin) {
        var gameProfile = WrappedGameProfile.fromPlayer(player);

        gameProfile.getProperties().put("textures",
                WrappedSignedProperty.fromValues("textures", skin.getValue(), skin.getSignature()));

        return gameProfile;
    }

    /**
     * Helper function to parse a ProtocolLib WrappedGameProfile to a Paper based
     * PlayerProfile.
     * 
     * @param player             the player to get the PlayerProfile for.
     * @param wrappedGameProfile the ProtocolLib WrappedGameProfile.
     * @return the Paper based PlayerProfile.
     */
    public static PlayerProfile fromWrappedToPlayerProfile(final Player player,
            final WrappedGameProfile wrappedGameProfile) {
        PlayerProfile playerProfile = player.getPlayerProfile();

        // Clear properties so that we can override them.
        playerProfile.clearProperties();

        for (WrappedSignedProperty property : wrappedGameProfile.getProperties().values()) {
            playerProfile
                    .setProperty(new ProfileProperty(property.getName(), property.getValue(), property.getSignature()));
        }
        return playerProfile;
    }

    /**
     * Method that sends all the packets necessary to update the skin of a player in
     * > 1.19.3.
     * 
     * @param updatePlayer the player to update
     * @param skin         the new skin
     */
    public static void updatePlayerSkin(final Player updatePlayer, final PlayerSkin skin) {
        // Force player out of vehicle to perform update
        // TODO: Comeback to this and make it so that the player can remounted.
        Optional.ofNullable(updatePlayer.getVehicle()).ifPresent(Entity::eject);

        final var wrappedGameProfile = getUpdatedGameProfile(updatePlayer, skin);
        final var gameProfile = fromWrappedToPlayerProfile(updatePlayer, wrappedGameProfile);

        sendPacketsSelf(updatePlayer, wrappedGameProfile);

        // trigger update exp
        updatePlayer.setExp(updatePlayer.getExp());

        // triggers updateAbilities
        updatePlayer.setWalkSpeed(updatePlayer.getWalkSpeed());

        // send the current inventory - otherwise player would have an empty inventory
        updatePlayer.updateInventory();

        PlayerInventory inventory = updatePlayer.getInventory();
        inventory.setHeldItemSlot(inventory.getHeldItemSlot());

        // trigger update attributes like health modifier for generic.maxHealth
        try {
            updatePlayer.getClass().getDeclaredMethod("updateScaledHealth").invoke(updatePlayer);
        } catch (ReflectiveOperationException reflectiveEx) {
            log.error("Failed to invoke updateScaledHealth for attributes", reflectiveEx);
        }
        // Hide and show player to others??
        updatePlayer.setPlayerProfile(gameProfile);
        // TODO: Not sure how this would communicate the new skin to other players.
        Bukkit.getOnlinePlayers().stream()
                .filter(onlinePlayer -> !onlinePlayer.equals(updatePlayer))
                .filter(onlinePlayer -> onlinePlayer.canSee(updatePlayer))
                .forEach(p -> hideAndShow(p, updatePlayer));
    }

    // jcedeno's version of the skinlib ends here. everything after this is helper
    // functions to enable it.

    /**
     * Helper function that creates and sends all the necessary packets to the
     * player whose skin is being updated.
     * 
     * @param receiver    the player whose skin is being updated.
     * @param gameProfile the game profile of the player whose skin is being
     *                    updated.
     */
    private static void sendPacketsSelf(Player receiver, WrappedGameProfile gameProfile) {
        PacketContainer removeInfo;
        PacketContainer addInfo;
        PacketContainer respawn;
        PacketContainer teleport;

        try {
            NativeGameMode gamemode = NativeGameMode.fromBukkit(receiver.getGameMode());
            WrappedChatComponent displayName = WrappedChatComponent.fromText(receiver.getPlayerListName());

            PlayerInfoData playerInfoData = new PlayerInfoData(gameProfile, 0, gamemode, displayName, null);

            // remove the old skin - client updates it only on a complete remove and add
            removeInfo = createRemovePacket(receiver, playerInfoData);

            // add info containing the skin data
            addInfo = createAddPacket(receiver, playerInfoData);

            // Respawn packet - notify the client that it should update the own skin
            respawn = createRespawnPacket(receiver, gamemode);

            // prevent the moved too quickly message
            teleport = createTeleportPacket(receiver.getLocation().clone());
        } catch (ReflectiveOperationException reflectiveEx) {
            log.error("Error occurred preparing packets. Cancelling self update", reflectiveEx);
            return;
        }

        sendPackets(receiver, removeInfo, addInfo, respawn, teleport);
    }

    /**
     * Helper function that creates the ADD to PlayerInfo packet (tab).
     * 
     * @param receiver       the player whose skin is being updated.
     * @param playerInfoData the player info data to add to the packet.
     * @return the ADD to PlayerInfo packet.
     */
    private static PacketContainer createAddPacket(Player receiver, PlayerInfoData playerInfoData) {
        PacketContainer addInfo = new PacketContainer(PLAYER_INFO);
        if (new MinecraftVersion(1, 19, 0).atOrAbove()) {
            addInfo.getPlayerInfoDataLists().write(1, Collections.singletonList(playerInfoData));
        } else {
            addInfo.getPlayerInfoDataLists().write(0, Arrays.asList(playerInfoData));
        }

        if (new MinecraftVersion(1, 19, 3).atOrAbove()) {
            addInfo.getPlayerInfoActions().write(0, EnumSet.of(PlayerInfoAction.ADD_PLAYER));
        } else {
            addInfo.getPlayerInfoAction().write(0, PlayerInfoAction.ADD_PLAYER);
        }

        return addInfo;
    }

    /**
     * Helper function that creates the REMOVE from PlayerInfo packet (tab).
     * 
     * @param receiver       the player whose skin is being updated.
     * @param playerInfoData the player info data to remove from the packet.
     * @return the REMOVE from PlayerInfo packet.
     */
    private static PacketContainer createRemovePacket(Player receiver, PlayerInfoData playerInfoData) {
        PacketContainer removeInfo;
        if (new MinecraftVersion(1, 19, 3).atOrAbove()) {
            removeInfo = new PacketContainer(PLAYER_INFO_REMOVE);

            List<UUID> removedPlayers = Collections.singletonList(receiver.getUniqueId());
            removeInfo.getLists(Converters.passthrough(UUID.class)).write(0, removedPlayers);
        } else {
            removeInfo = new PacketContainer(PLAYER_INFO);
            removeInfo.getPlayerInfoAction().write(0, PlayerInfoAction.REMOVE_PLAYER);
            removeInfo.getPlayerInfoDataLists().write(0, Arrays.asList(playerInfoData));
        }

        return removeInfo;
    }

    /**
     * Helper function that creates the respawn packet.
     * 
     * @param receiver the player whose skin is being updated.
     * @param gamemode the gamemode of the player whose skin is being updated.
     * @return the respawn packet.
     * @throws ReflectiveOperationException if the packet could not be created.
     */
    private static PacketContainer createRespawnPacket(Player receiver, NativeGameMode gamemode)
            throws ReflectiveOperationException {
        PacketContainer respawn = new PacketContainer(RESPAWN);

        World world = receiver.getWorld();
        Difficulty difficulty = EnumWrappers.getDifficultyConverter().getSpecific(world.getDifficulty());

        // <= 1.13.1
        int dimensionId = world.getEnvironment().getId();
        respawn.getIntegers().writeSafely(0, dimensionId);

        // > 1.13.1
        if (MinecraftVersion.getCurrentVersion().compareTo(MinecraftVersion.AQUATIC_UPDATE) > 0) {
            try {
                respawn.getDimensionTypes().writeSafely(0, world);
                if (isAtOrAbove("1.18.2")) {
                    Object dimensionTypeHolder = getDimensionType(world);
                    respawn.getModifier().write(0, dimensionTypeHolder);
                }
            } catch (NoSuchMethodError noSuchMethodError) {
                throw new ReflectiveOperationException("Unable to find dimension setter. " +
                        "Your ProtocolLib version is incompatible with this plugin version in combination with " +
                        "Minecraft 1.13.1. " +
                        "Try to download an update of ProtocolLib.", noSuchMethodError);
            }
        }

        // 1.14 dropped difficulty and 1.15 added hashed seed
        respawn.getDifficulties().writeSafely(0, difficulty);
        if (isAtOrAbove("1.15")) {
            long seed = world.getSeed();
            respawn.getLongs().write(0, Hashing.sha256().hashLong(seed).asLong());
        }

        if (isAtOrAbove("1.16")) {
            // a = dimension (as resource key) -> dim type, b = world (resource key) ->
            // world name, c = "hashed" seed
            // dimension and seed covered above - we have to start with 1 because dimensions
            // already uses the first idx

            // 1.16.2 dropped the first resourcekey usage
            respawn.getWorldKeys().write(0, world);

            // d = gamemode, e = gamemode (previous)
            respawn.getGameModes().write(0, gamemode);

            NativeGameMode previousGamemode = getPreviousGamemode(receiver);
            if (previousGamemode != null) {
                respawn.getGameModes().write(1, previousGamemode);
            }

            // f = debug world, g = flat world, h = flag (copy metadata)
            // get the NMS world
            try {
                Object nmsWorld = BukkitConverters.getWorldConverter().getGeneric(world);
                respawn.getBooleans().write(0, (boolean) DEBUG_WORLD_METHOD.invoke(nmsWorld));
            } catch (Exception ex) {
                log.error("Cannot fetch debug state of world {}. Assuming false", world);
                respawn.getBooleans().write(0, false);
            } catch (Throwable throwable) {
                throw (Error) throwable;
            }

            respawn.getBooleans().write(1, world.getWorldType() == WorldType.FLAT);
            // flag: true = teleport like, false = player actually died - uses respawn
            // anchor in nether
            respawn.getBooleans().writeSafely(2, true);
        } else {
            // world type field replaced with a boolean
            respawn.getWorldTypeModifier().write(0, world.getWorldType());
            respawn.getGameModes().write(0, gamemode);
        }

        if (isAtOrAbove("1.19")) {
            // set last death location
            respawn.getOptionals(Converters.passthrough(Object.class)).write(0, Optional.empty());
        }

        return respawn;
    }

    /**
     * Creates a teleport packet that will be sent to the player.
     * 
     * @param location the location to teleport to.
     * @return the teleport packet.
     */
    private static PacketContainer createTeleportPacket(Location location) {
        PacketContainer teleport = new PacketContainer(POSITION);
        teleport.getModifier().writeDefaults();

        teleport.getDoubles().write(0, location.getX())
                .write(1, location.getY())
                .write(2, location.getZ());

        teleport.getFloat().write(0, location.getYaw())
                .write(1, location.getPitch());

        // send an invalid teleport id in order to let Bukkit ignore the incoming
        // confirm packet
        teleport.getIntegers().writeSafely(0, -1337);
        return teleport;
    }

    /**
     * Sends the given packets to the given player.
     * 
     * @param receiver the player to send the packets to.
     * @param packets  the packets to send.
     */
    private static void sendPackets(Player receiver, PacketContainer... packets) {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        for (PacketContainer packet : packets) {
            protocolManager.sendServerPacket(receiver, packet);
        }
    }

    /**
     * Gets the previous, packet safe gamemode of the given player.
     * 
     * @param receiver the player to get the gamemode of.
     * @return the previous gamemode.
     */
    private static NativeGameMode getPreviousGamemode(Player receiver) {
        try {
            Object nmsPlayer = PLAYER_HANDLE_METHOD.invoke(receiver);
            Object interactionManager = INTERACTION_MANAGER.get(nmsPlayer);
            Enum<?> gamemode = (Enum<?>) GAMEMODE_FIELD.get(interactionManager);
            if (gamemode == null) {
                return null;
            }

            return NativeGameMode.valueOf(gamemode.name());
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Failed to fetch previous gamemode of player {}", receiver, e);
        }

        return NativeGameMode.fromBukkit(receiver.getGameMode());
    }

    /**
     * Gets the dimension type of the given world.
     * 
     * @param world the world to get the dimension type of.
     * @return the dimension type.
     */
    private static Object getDimensionType(World world) {
        try {
            Class<?> holderClass = MinecraftReflection.getMinecraftClass("core.Holder");
            Class<?> nmsWorldClass = MinecraftReflection.getNmsWorldClass();

            // get method by return type, but without any arguments
            // explicitly use new Class[]{} in order to get the correct method without
            // varargs method arguments
            Method dimensionTypeGetter = FuzzyReflection.fromClass(nmsWorldClass)
                    .getMethodByReturnTypeAndParameters("dimensionTypeRegistration", holderClass, new Class[] {});

            Object nmsWorld = BukkitConverters.getWorldConverter().getGeneric(world);

            Object holder = dimensionTypeGetter.invoke(nmsWorld);
            Class<?> resourceKey = MinecraftReflection.getResourceKey();
            Field field = FuzzyReflection.fromClass(holder.getClass(), true).getFieldByType("key", resourceKey);
            field.setAccessible(true);
            return field.get(holder);
        } catch (ReflectiveOperationException reflectiveEx) {
            log.error("Failed to get dimension type for skin refresh", reflectiveEx);
        }

        return null;
    }

    /**
     * Gets the previous gamemode field of the given interaction manager.
     * 
     * @param interactionManager the interaction manager to get the field from.
     * @return the previous gamemode field.
     * @throws NoSuchFieldException if the field cannot be found.
     */
    private static Field getPreviousGamemodeField(Class<?> interactionManager) throws NoSuchFieldException {
        List<Field> gamemodes = FuzzyReflection.fromClass(interactionManager, true)
                .getFieldListByType(EnumWrappers.getGameModeClass());
        if (gamemodes.size() < 2) {
            throw new NoSuchFieldException("Cannot find previous gamemode field");
        }

        // skip the first field that is the current field
        return gamemodes.get(1);
    }

    private static boolean isAtOrAbove(String s) {
        return MinecraftVersion.getCurrentVersion().compareTo(new MinecraftVersion(s)) >= 0;
    }

    protected static void runAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(MonadUHC.instance(), runnable);
    }

    @SuppressWarnings("deprecation")
    private static void hideAndShow(Player other, Player receiver) {
        // removes the entity and display the new skin
        if (NEW_HIDE_METHOD_AVAILABLE) {
            other.hidePlayer(MonadUHC.instance(), receiver);
            other.showPlayer(MonadUHC.instance(), receiver);
        } else {
            other.hidePlayer(receiver);
            other.showPlayer(receiver);
        }
    }
}
