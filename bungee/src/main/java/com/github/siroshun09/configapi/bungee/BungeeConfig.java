package com.github.siroshun09.configapi.bungee;

import com.github.siroshun09.configapi.common.FileUtils;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * This class extends {@link BungeeYaml} to use plugin resources.
 */
public class BungeeConfig extends BungeeYaml {

    private final Plugin plugin;
    private final String fileName;
    private final boolean resource;

    /**
     * Creates an {@link BungeeConfig} with given yaml file.
     * <p>
     * This constructor loads the file automatically.
     * The file path to load or create is {@code /plugins/PLUGIN_NAME/FILE_NAME}
     *
     * @param plugin   A plugin instance.
     * @param fileName Loads or creates file name.
     * @param resource If the file does not exist, should it be copied from the plugin jar.
     */
    public BungeeConfig(@NotNull Plugin plugin, @NotNull String fileName, boolean resource) {
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
        if (resource && !Files.exists(filePath)) {
            InputStream in = plugin.getResourceAsStream(fileName);
            if (in != null && FileUtils.checkParentDirectory(filePath)) {
                try {
                    Files.copy(in, filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return super.load();
    }
}
