package us.jcedeno.anmelden.bukkit.scenarios.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.bukkit.Material;

/**
 * Scenario annotation to be used in the scenario classes to describe and auto register the scenario.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Scenario {
    String name();
    String description();
    Material ui();
}
