package io.github.luxuryquests;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import io.github.luxuryquests.api.Api;
import io.github.luxuryquests.cache.QuestCache;
import io.github.luxuryquests.cache.RewardCache;
import io.github.luxuryquests.cache.StaticItemsCache;
import io.github.luxuryquests.cache.UserCache;
import io.github.luxuryquests.cache.listener.ConnectionListener;
import io.github.luxuryquests.commands.AliasesListener;
import io.github.luxuryquests.commands.lq.LqCommand;
import io.github.luxuryquests.commands.lqa.LqaCommand;
import io.github.luxuryquests.config.Lang;
import io.github.luxuryquests.controller.QuestController;
import io.github.luxuryquests.loaders.MenuLoader;
import io.github.luxuryquests.loaders.TypeLoader;
import io.github.luxuryquests.menu.MenuFactory;
import io.github.luxuryquests.menu.service.MenuIllustrator;
import io.github.luxuryquests.menu.service.action.Action;
import io.github.luxuryquests.objects.user.User;
import io.github.luxuryquests.placeholders.PlaceholderApiPlaceholders;
import io.github.luxuryquests.quests.workers.QuestReset;
import io.github.luxuryquests.quests.workers.pipeline.QuestPipeline;
import io.github.luxuryquests.registry.ArgumentRegistry;
import io.github.luxuryquests.registry.QuestRegistry;
import io.github.luxuryquests.storage.ResetStorage;
import io.github.luxuryquests.storage.UserStorage;
import io.github.luxuryquests.updaters.UserUpdater;
import me.hyfe.simplespigot.config.Config;
import me.hyfe.simplespigot.menu.listener.MenuListener;
import me.hyfe.simplespigot.plugin.SpigotPlugin;
import me.hyfe.simplespigot.storage.StorageSettings;
import me.hyfe.simplespigot.storage.storage.Storage;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class QuestsPlugin extends SpigotPlugin {
    private static Api api;
    private TypeLoader typeLoader;
    private MenuLoader menuLoader;
    private StaticItemsCache staticItemsCache;
    private UserCache userCache;
    private UserUpdater userUpdater;
    private QuestCache questCache;
    private RewardCache rewardCache;
    private QuestPipeline questPipeline;
    private QuestRegistry questRegistry;
    private QuestController questController;
    private MenuFactory menuFactory;
    private MenuIllustrator menuIllustrator;
    private Storage<User> storage;
    private Storage<QuestReset> resetStorage;
    private Cache<String, Map<Integer, Set<Action>>> actionCache;
    private AtomicInteger placeholderRuns = new AtomicInteger();
    private Lang lang;
    private Api localApi;

    @Override
    public void onEnable() {
        this.configRelations();
        this.load();
    }

    @Override
    public void onDisable() {
        this.unload();
    }

    public static Api getApi() {
        return api;
    }

    public Api getLocalApi() {
        return this.localApi;
    }

    public UserUpdater getUserUpdater() {
        return this.userUpdater;
    }

    public TypeLoader getTypeLoader() {
        return this.typeLoader;
    }

    public MenuLoader getMenuLoader() {
        return this.menuLoader;
    }

    public StaticItemsCache getStaticItemsCache() {
        return this.staticItemsCache;
    }

    public UserCache getUserCache() {
        return this.userCache;
    }

    public QuestCache getQuestCache() {
        return this.questCache;
    }

    public RewardCache getRewardCache() {
        return this.rewardCache;
    }

    public QuestPipeline getQuestPipeline() {
        return this.questPipeline;
    }

    public QuestRegistry getQuestRegistry() {
        return this.questRegistry;
    }

    public QuestController getQuestController() {
        return this.questController;
    }

    public MenuFactory getMenuFactory() {
        return this.menuFactory;
    }

    public MenuIllustrator getMenuIllustrator() {
        return this.menuIllustrator;
    }

    public Storage<User> getStorage() {
        return this.storage;
    }

    public Storage<QuestReset> getResetStorage() {
        return this.resetStorage;
    }

    public Lang getLang() {
        return this.lang;
    }

    public Config getConfig(String string) {
        return this.getConfigStore().getConfig(string);
    }

    public Cache<String, Map<Integer, Set<Action>>> getActionCache() {
        return this.actionCache;
    }

    private void load() {
        this.setStorageSettings();

        this.userUpdater = new UserUpdater(this);
        this.storage = new UserStorage(this);
        this.resetStorage = new ResetStorage(this);
        this.typeLoader = new TypeLoader(this);
        this.menuLoader = new MenuLoader(this);
        this.staticItemsCache = new StaticItemsCache(this);
        this.questCache = new QuestCache(this);
        this.questController = new QuestController(this);
        this.userCache = new UserCache(this, this.storage);
        this.rewardCache = new RewardCache(this);
        this.questRegistry = new QuestRegistry(this);

        this.typeLoader.load();
        this.menuLoader.load();
        this.staticItemsCache.cache();
        this.rewardCache.cache();
        this.questCache.cache();
        this.userCache.loadOnline();
        this.typeLoader.loadQuestResets();

        this.questPipeline = new QuestPipeline(this);
        this.menuFactory = new MenuFactory(this);
        this.menuIllustrator = new MenuIllustrator();

        this.actionCache = CacheBuilder.newBuilder().expireAfterAccess(20, TimeUnit.SECONDS).build();

        this.localApi = new Api(this);
        api = this.localApi;

        this.getSavingController().addSavable(this.userCache, this.getConfig("settings").integer("storage-options.auto-save-interval") * 20);

        this.registerRegistries(new ArgumentRegistry(this), this.questRegistry);
        this.registerListeners(new MenuListener(), new ConnectionListener(this), new AliasesListener(this));
        this.runSync(() -> {
            this.getCommandBase().getCommands().clear();
            this.registerCommands(new LqaCommand(this), new LqCommand(this));
            this.placeholders();
        });
    }

    private void unload() {
        HandlerList.unregisterAll(this);
        this.userCache.save();
        this.userCache.getSubCache().invalidateAll();
        this.typeLoader.saveQuestResets();
        this.storage.closeBack();
        this.resetStorage.closeBack();
    }

    public void reload() {
        this.getConfigStore().reloadReloadableConfigs();
        this.unload();
        this.load();
        System.gc();
    }

    private void placeholders() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderApiPlaceholders(this).register();
        }
        if (this.placeholderRuns.intValue() < 10) {
            this.placeholderRuns.getAndIncrement();
            Bukkit.getScheduler().runTaskLater(this, this::placeholders, 100);
        }
    }

    private void configRelations() {
        this.getConfigStore()
                .config("settings", Path::resolve, true)
                .config("rewards", Path::resolve, true)
                .config("lang", Path::resolve, true)
                .config("static-items", Path::resolve, true)
                .config("user-options", Path::resolve, true)
                .config("portal-menu", (path, name) -> path.resolve("menus").resolve("portal"), true)
                .config("user-menu", (path, name) -> path.resolve("menus").resolve("user-options"), true)
                .config("rewards-menu", (path, name) -> path.resolve("menus").resolve("rewards"), true)
                .common("storageType", "settings", config -> config.string("storage-options.storage-method"));
        this.lang = new Lang(this);
    }

    private void setStorageSettings() {
        Config config = this.getConfig("settings");
        StorageSettings storageSettings = this.getStorageSettings();
        storageSettings.setAddress(config.string("storage-options.address"));
        storageSettings.setDatabase(config.string("storage-options.database"));
        storageSettings.setPrefix(config.string("storage-options.prefix"));
        storageSettings.setUsername(config.string("storage-options.username"));
        storageSettings.setPassword(config.string("storage-options.password"));
        storageSettings.setConnectionTimeout(config.integer("storage-options.pool-settings.connection-timeout"));
        storageSettings.setMaximumLifetime(config.integer("storage-options.pool-settings.maximum-lifetime"));
        storageSettings.setMaximumPoolSize(config.integer("storage-options.pool-settings.maximum-pool-size"));
        storageSettings.setMinimumIdle(config.integer("storage-options.pool-settings.minimum-idle"));
        storageSettings.setProperties(Maps.newHashMap());
    }
}
