package com.github.siroshun09.configapi.common;

/**
 * An interface extending {@link FileConfiguration} that uses yaml files
 * <p>
 * Using null for @NotNull argument will cause a {@link NullPointerException}.
 */
public interface Yaml extends FileConfiguration {
}
