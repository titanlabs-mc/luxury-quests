package io.github.luxuryquests.objects.quest.variable;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface QuestResult {

    QuestResult root(String root);

    QuestResult root(Block rootBlock);

    QuestResult root(ItemStack rootItem);

    QuestResult subRoot(String subRoot, String value);

    QuestResult none();

    boolean isEligible(Player player, Variable variable);
}
