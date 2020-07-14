package io.github.luxuryquests.loaders;

import com.google.common.collect.Maps;
import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.menu.service.extensions.ConfigMenu;
import lombok.SneakyThrows;
import me.hyfe.simplespigot.config.Config;
import me.hyfe.simplespigot.menu.Menu;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MenuLoader {
    private final QuestsPlugin plugin;
    private final Path dataFolder;
    private final Map<String, Config> menus = Maps.newHashMap();

    public MenuLoader(QuestsPlugin plugin) {
        this.plugin = plugin;
        this.dataFolder = plugin.getDataFolder().toPath();
    }

    @SneakyThrows
    public void load() {
        this.createDefaultFiles();
        Set<File> menuFiles = Files.walk(this.dataFolder.resolve("menus/custom"))
                .map(Path::toFile)
                .collect(Collectors.toSet());
        for (File menuFile : menuFiles) {
            String name = menuFile.getName().replace(".yml", "");
            Config config = new Config(this.plugin, menuFile, true);
            this.menus.put(name, config);
        }
    }

    public Menu createMenu(Player player, String menu) {
        if (this.menus.containsKey(menu)) {
            Config config = this.menus.get(menu);
            return new ConfigMenu(this.plugin, config, player) {

                @Override
                public void redraw() {
                    super.redraw();
                }
            };
        }
        return null;
    }

    private void createDefaultFiles() {
        Path path = this.dataFolder.resolve("menus/custom");
        if (path.toFile().exists()) {
            return;
        }
        this.plugin.saveResource("menus/custom/custom.yml", false);
    }

    public Set<String> getMenuNames() {
        return this.menus.keySet();
    }
}
