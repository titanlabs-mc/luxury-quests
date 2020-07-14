package io.github.luxuryquests.registry;

import com.google.common.collect.Maps;
import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.quests.QuestExecutor;
import io.github.luxuryquests.quests.quests.external.*;
import io.github.luxuryquests.quests.quests.external.executor.ExternalQuestExecutor;
import io.github.luxuryquests.quests.quests.internal.*;
import me.hyfe.simplespigot.registry.Registry;
import me.hyfe.simplespigot.tuple.ImmutablePair;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.logging.Level;

public class QuestRegistry implements Registry {
    private final QuestsPlugin plugin;
    private final Map<String, ImmutablePair<AtomicInteger, Integer>> attempts = Maps.newHashMap();

    public QuestRegistry(QuestsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void register() {
        this.registerQuests(
                BlockBreakQuest::new,
                BlockPlaceQuest::new,
                ChatQuest::new,
                ClickQuest::new,
                ConsumeQuest::new,
                CraftQuest::new,
                DamageQuest::new,
                EnchantQuest::new,
                ExecuteCommandQuest::new,
                FishingQuest::new,
                GainExpQuest::new,
                MovementQuests::new,
                ItemBreakQuest::new,
                KillMobQuest::new,
                KillPlayerQuest::new,
                LoginQuest::new,
                MilkQuest::new,
                PlayTimeQuest::new,
                RegenerateQuest::new,
                RideMobQuest::new,
                ShearSheepQuest::new,
                SmeltQuest::new,
                TameQuest::new
        );
        this.registerHook("AdvancedEnchantments", AdvancedEnchantmentsQuests::new);
        this.registerHook("ASkyblock", ASkyblockQuests::new);
        this.registerHook("AuctionHouse", AuctionHouseKludgeQuests::new, "klugemonkey");
        this.registerHook("BedWars1058", BedWars1058Quests::new);
        this.registerHook("BuildBattle", BuildBattleTigerQuests::new, "Tigerpanzer");
        this.registerHook("ChatReaction", ChatReactionQuests::new);
        //this.registerHook("ChestShop", ChestShopQuests::new, "https://github.com/ChestShop-authors/ChestShop-3/contributors", version -> !(version > 3.92));
        this.registerHook("Citizens", CitizensQuests::new);
        this.registerHook("Clans", ClansQuests::new);
        this.registerHook("ClueScrolls", ClueScrollsQuests::new);
        this.registerHook("CrateReloaded", CrateReloadedQuests::new);
        this.registerHook("CratesPlus", CratesPlusQuests::new);
        this.registerHook("CrazyCrates", CrazyCratesQuests::new);
        this.registerHook("DiscordMinecraft", DiscordMinecraftQuests::new);
        // this.registerHook("Factions", FactionsUuidQuests::new, "com.massivecraft.factions.event.FactionPlayerEvent");
        this.registerHook("Jobs", JobsQuests::new);
        this.registerHook("Lands", LandsQuests::new);
        this.registerHook("KoTH", KothQuests::new);
        this.registerHook("MoneyHunters", MoneyHuntersQuests::new);
        this.registerHook("MythicMobs", MythicMobsQuests::new);
        this.registerHook("Votifier", VotifierQuests::new);
        this.registerHook("PlaceholderApi", PlaceholderApiQuests::new);
        this.registerHook("PlotSquared", PlotSquaredQuests::new);
        this.registerHook("ProCosmetics", ProCosmeticsQuests::new);
        this.registerHook("CrazyEnvoy", CrazyEnvoyQuests::new);
        this.registerHook("Shopkeepers", ShopkeeperQuests::new, "nisovin");
        this.registerHook("SkillAPI", SkillApiQuests::new);
        this.registerHook("StrikePractice", StrikePracticeQuests::new);
        this.registerHook("SuperiorSkyblock2", SuperiorSkyblockQuests::new);
        this.registerHook("TheLab", TheLabQuests::new);
        this.registerHook("TokenEnchant", TokenEnchantQuests::new);
        this.registerHook("uSkyBlock", USkyBlockQuests::new);
    }

    @SafeVarargs
    public final void registerQuests(Function<QuestsPlugin, QuestExecutor>... functions) {
        for (Function<QuestsPlugin, QuestExecutor> function : functions) {
            Bukkit.getPluginManager().registerEvents(function.apply(this.plugin), this.plugin);
        }
    }

    public boolean registerHook(String plugin, Function<QuestsPlugin, ExternalQuestExecutor> function) {
        if (Bukkit.getPluginManager().isPluginEnabled(plugin)) {
            Bukkit.getPluginManager().registerEvents(function.apply(this.plugin), this.plugin);
            Bukkit.getLogger().log(Level.INFO, "Hooked into ".concat(plugin));
            return true;
        }
        this.runRepeatingCheck(plugin, () -> {
            if (this.registerHook(plugin, function)) {
                Bukkit.getScheduler().cancelTask(this.attempts.get(plugin).getValue());
            }
        });
        return false;
    }

    public boolean registerHook(String plugin, Function<QuestsPlugin, ExternalQuestExecutor> function, String author) {
        if (Bukkit.getPluginManager().isPluginEnabled(plugin)) {
            if (Bukkit.getPluginManager().getPlugin(plugin).getDescription().getAuthors().contains(author)) {
                Bukkit.getPluginManager().registerEvents(function.apply(this.plugin), this.plugin);
                Bukkit.getLogger().log(Level.INFO, "Hooked into ".concat(plugin));
                return true;
            }
        }
        this.runRepeatingCheck(plugin, () -> {
            if (this.registerHook(plugin, function, author)) {
                Bukkit.getScheduler().cancelTask(this.attempts.get(plugin).getValue());
            }
        });
        return false;
    }

    public boolean registerHook(String plugin, Function<QuestsPlugin, ExternalQuestExecutor> function, Function<Double, Boolean> versionCheck) {
        PluginManager pluginManager = Bukkit.getPluginManager();
        if (pluginManager.isPluginEnabled(plugin)) {
            double version = this.getFormattedVersion(plugin);
            if (versionCheck.apply(version)) {
                pluginManager.registerEvents(function.apply(this.plugin), this.plugin);
                Bukkit.getLogger().log(Level.INFO, "Hooked into ".concat(plugin));
            } else {
                Bukkit.getLogger().log(Level.INFO, plugin.concat(" was present but its version is not supported."));
            }
            return true;
        }
        this.runRepeatingCheck(plugin, () -> {
            if (this.registerHook(plugin, function, versionCheck)) {
                Bukkit.getScheduler().cancelTask(this.attempts.get(plugin).getValue());
            }
        });
        return false;
    }

    public boolean registerHook(String plugin, Function<QuestsPlugin, ExternalQuestExecutor> function, String author, Function<Double, Boolean> versionCheck) {
        PluginManager pluginManager = Bukkit.getPluginManager();
        if (pluginManager.isPluginEnabled(plugin)) {
            double version = this.getFormattedVersion(plugin);
            if (pluginManager.getPlugin(plugin).getDescription().getAuthors().contains(author) && versionCheck.apply(version)) {
                pluginManager.registerEvents(function.apply(this.plugin), this.plugin);
                Bukkit.getLogger().log(Level.INFO, "Hooked into ".concat(plugin));
            } else {
                Bukkit.getLogger().log(Level.INFO, plugin.concat(" was present but its version is not supported."));
            }
            return true;
        }
        this.runRepeatingCheck(plugin, () -> {
            if (this.registerHook(plugin, function, author, versionCheck)) {
                Bukkit.getScheduler().cancelTask(this.attempts.get(plugin).getValue());
            }
        });
        return false;
    }

    private void runRepeatingCheck(String plugin, Runnable runnable) {
        if (this.attempts.containsKey(plugin)) {
            return;
        }
        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> {
            AtomicInteger value = this.attempts.get(plugin).getKey();
            if (value.intValue() > 18) {
                Bukkit.getScheduler().cancelTask(this.attempts.get(plugin).getValue());
            }
            runnable.run();
        }, 200, 200);

        this.attempts.put(plugin, ImmutablePair.of(new AtomicInteger(), taskId));
    }

    private double getFormattedVersion(String plugin) {
        String pluginVersion = Bukkit.getPluginManager().getPlugin(plugin).getDescription().getVersion();
        if (pluginVersion.contains(".")) {
            String[] split = pluginVersion.split(".");
            StringBuilder builder = new StringBuilder();
            boolean first = true;
            for (String part : split) {
                builder.append(part.replace("\\D", ""));
                if (first && split.length > 1) {
                    builder.append(".");
                    first = false;
                }
            }
            return Double.parseDouble(builder.toString());
        }
        return Double.parseDouble(pluginVersion);
    }
}
