package net.tearpelato.deco_lib.client.core.core_tabs;

@FunctionalInterface
public interface FeatureFlag {
    FeatureFlag DEFAULT = () -> true;

    boolean isEnabled();
}
