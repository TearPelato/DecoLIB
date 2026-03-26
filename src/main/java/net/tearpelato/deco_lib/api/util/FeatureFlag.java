package net.tearpelato.deco_lib.api.util;

/**
 * @author ItsBlackGear
 **/
@FunctionalInterface
public interface FeatureFlag {
    FeatureFlag DEFAULT = () -> true;

    boolean isEnabled();
}