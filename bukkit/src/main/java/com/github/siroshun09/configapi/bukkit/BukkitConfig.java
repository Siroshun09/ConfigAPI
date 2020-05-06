package com.github.siroshun09.configapi.bukkit;

import com.github.siroshun09.configapi.common.FileUtils;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;

/**
 * This class extends {@link BukkitYaml} to use plugin resources.
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
        this.plugin = plugin;
        this.fileName = fileName;
        this.resource = resource;
        load();
    }

    /**
     * Loads a yaml file.
     * <p>
     * If a yaml file is not found, this method will create one.
     * At that time, copy from plugin jar if resource is true.
     *
     * @return True if the load is successful or false if it is failure.
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
