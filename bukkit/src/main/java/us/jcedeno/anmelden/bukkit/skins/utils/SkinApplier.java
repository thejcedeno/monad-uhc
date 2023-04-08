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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.FieldAccessException;
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
import com.google.common.hash.Hashing;

import lombok.extern.log4j.Log4j2;
import us.jcedeno.anmelden.bukkit.MonadUHC;

@Log4j2
public class SkinApplier {

    private static final boolean NEW_HIDE_METHOD_AVAILABLE;

    // static final methods are faster, because JVM can inline them and make them
    // accessible
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

    private static Field getPreviousGamemodeField(Class<?> interactionManager) throws NoSuchFieldException {
        List<Field> gamemodes = FuzzyReflection.fromClass(interactionManager, true)
                .getFieldListByType(EnumWrappers.getGameModeClass());
        if (gamemodes.size() < 2) {
            throw new NoSuchFieldException("Cannot find previous gamemode field");
        }

        // skip the first field that is the current field
        return gamemodes.get(1);
    }

    private final CommandSender invoker;
    private final Player receiver;
    private final boolean keepSkin;

    public SkinApplier(CommandSender invoker, Player receiver, boolean keepSkin) {

        this.invoker = invoker;
        this.receiver = receiver;
        // this.targetSkin = targetSkin;
        this.keepSkin = keepSkin;
    }

    public void run() {
        if (!isConnected()) {
            return;
        }

        // applySkin();
    }

    protected boolean isConnected() {
        return receiver != null && receiver.isOnline();
    }

    protected void applyInstantUpdate() {
        // TODO Come back to this and figure out what the mehtod is.
        // plugin.getApi().applySkin(receiver, targetSkin);

        if (!DISABLED_PACKETS) {
            sendUpdateSelf(WrappedGameProfile.fromPlayer(receiver));
        }

        sendUpdateOthers();

        if (receiver.equals(invoker)) {
            log.info("Skin changed for " + receiver.getName());
        } else {
            log.info("Skin updated for " + receiver.getName() + " by " + invoker.getName());
        }
    }

    protected void runAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(MonadUHC.instance(), runnable);
    }

    private void sendUpdateOthers() throws FieldAccessException {
        // triggers an update for others player to see the new skin
        Bukkit.getOnlinePlayers().stream()
                .filter(onlinePlayer -> !onlinePlayer.equals(receiver))
                .filter(onlinePlayer -> onlinePlayer.canSee(receiver))
                .forEach(this::hideAndShow);
    }

    private void sendUpdateSelf(WrappedGameProfile gameProfile) throws FieldAccessException {
        Optional.ofNullable(receiver.getVehicle()).ifPresent(Entity::eject);

        sendPacketsSelf(gameProfile);

        // trigger update exp
        receiver.setExp(receiver.getExp());

        // triggers updateAbilities
        receiver.setWalkSpeed(receiver.getWalkSpeed());

        // send the current inventory - otherwise player would have an empty inventory
        receiver.updateInventory();

        PlayerInventory inventory = receiver.getInventory();
        inventory.setHeldItemSlot(inventory.getHeldItemSlot());

        // trigger update attributes like health modifier for generic.maxHealth
        try {
            receiver.getClass().getDeclaredMethod("updateScaledHealth").invoke(receiver);
        } catch (ReflectiveOperationException reflectiveEx) {
            log.error("Failed to invoke updateScaledHealth for attributes", reflectiveEx);
        }
    }

    private void sendPacketsSelf(WrappedGameProfile gameProfile) {
        PacketContainer removeInfo;
        PacketContainer addInfo;
        PacketContainer respawn;
        PacketContainer teleport;

        try {
            NativeGameMode gamemode = NativeGameMode.fromBukkit(receiver.getGameMode());
            WrappedChatComponent displayName = WrappedChatComponent.fromText(receiver.getPlayerListName());

            PlayerInfoData playerInfoData = new PlayerInfoData(gameProfile, 0, gamemode, displayName, null);

            // remove the old skin - client updates it only on a complete remove and add
            removeInfo = createRemovePacket(playerInfoData);

            // add info containing the skin data
            addInfo = createAddPacket(playerInfoData);

            // Respawn packet - notify the client that it should update the own skin
            respawn = createRespawnPacket(gamemode);

            // prevent the moved too quickly message
            teleport = createTeleportPacket(receiver.getLocation().clone());
        } catch (ReflectiveOperationException reflectiveEx) {
            log.error("Error occurred preparing packets. Cancelling self update", reflectiveEx);
            return;
        }

        sendPackets(removeInfo, addInfo, respawn, teleport);
    }

    private PacketContainer createAddPacket(PlayerInfoData playerInfoData) {
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

    private PacketContainer createRemovePacket(PlayerInfoData playerInfoData) {
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

    @SuppressWarnings("deprecation")
    private void hideAndShow(Player other) {
        // removes the entity and display the new skin
        if (NEW_HIDE_METHOD_AVAILABLE) {
            other.hidePlayer(MonadUHC.instance(), receiver);
            other.showPlayer(MonadUHC.instance(), receiver);
        } else {
            other.hidePlayer(receiver);
            other.showPlayer(receiver);
        }
    }

    private void sendPackets(PacketContainer... packets) {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        for (PacketContainer packet : packets) {
            protocolManager.sendServerPacket(receiver, packet);
        }
    }

    private PacketContainer createRespawnPacket(NativeGameMode gamemode) throws ReflectiveOperationException {
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

    private static boolean isAtOrAbove(String s) {
        return MinecraftVersion.getCurrentVersion().compareTo(new MinecraftVersion(s)) >= 0;
    }

    private PacketContainer createTeleportPacket(Location location) {
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

    private NativeGameMode getPreviousGamemode(Player receiver) {
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

    private Object getDimensionType(World world) {
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
}
