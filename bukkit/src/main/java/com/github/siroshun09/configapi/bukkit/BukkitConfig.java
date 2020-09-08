package com.github.siroshun09.configapi.bukkit;

import com.github.siroshun09.configapi.common.FileUtils;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.util.Objects;

/**
 * This class extends {@link BukkitYaml} to use plugin resources.
 * <p>
 * Using null for @NotNull argument will cause a {@link NullPointerException}.
 */
public class BukkitConfig extends BukkitYaml {

    private final Plugin plugin;
    private final String fileName;
    private final boolean resource;

    /**
     * Creates an {@link BukkitConfig} with given yaml file.
     * <p>
     * This constructor loads the file automatically.
     * The file path to load or create is {@code /plugins/PLUGIN_NAME/FILE_NAME}
     *
     * @param plugin   A plugin instance.
     * @param fileName Loads or creates file name.
     * @param resource If the file does not exist, should it be copied from the plugin jar.
     */
    public BukkitConfig(@NotNull Plugin plugin, @NotNull String fileName, boolean resource) {
        super(plugin.getDataFolder().toPath().resolve(fileName), false);

        Objects.requireNonNull(plugin, "plugin must not be null.");
        Objects.requireNonNull(fileName, "fileName must not be null.");

        this.plugin = plugin;
        this.fileName = fileName;
        this.resource = resource;
        load();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean load() {
        if (resource && !Files.exists(filePath) && plugin.getResource(fileName) != null) {
            if (FileUtils.checkParentDirectory(filePath)) {
                plugin.saveResource(fileName, false);
            }
        }
        return super.load();
    }
}
