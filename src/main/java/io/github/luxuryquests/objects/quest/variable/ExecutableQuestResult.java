package io.github.luxuryquests.objects.quest.variable;

import com.google.common.collect.Maps;
import me.hyfe.simplespigot.tuple.ImmutablePair;
import me.hyfe.simplespigot.version.MultiMaterial;
import me.hyfe.simplespigot.version.ServerVersion;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ExecutableQuestResult implements QuestResult {
    private String root;
    private Map<String, String> subRoots = Maps.newHashMap();

    // Item / Block
    private String material;
    private byte data;

    @Override
    public ExecutableQuestResult root(String root) {
        if (root == null) {
            return this;
        }
        this.root = root;
        return this;
    }

    @Override
    public ExecutableQuestResult root(Block rootBlock) {
        if (rootBlock == null) {
            return this;
        }
        this.material = rootBlock.getType().toString();
        this.data = rootBlock.getData();
        return this;
    }

    @Override
    public ExecutableQuestResult root(ItemStack rootItem) {
        if (rootItem == null) {
            return this;
        }
        this.material = rootItem.getType().toString();
        this.data = rootItem.getData().getData();
        return this;
    }

    @Override
    public ExecutableQuestResult subRoot(String subRoot, String value) {
        this.subRoots.put(subRoot, value);
        return this;
    }

    @Override
    public ExecutableQuestResult none() {
        this.root = "none";
        return this;
    }

    @Override
    public boolean isEligible(Player player, Variable variable) {
        for (String root : variable.getRoots()) {
            boolean noVariableRoot = root.equalsIgnoreCase("none");
            if (this.areSubRootsValid(player, variable)) {
                if (this.material == null && (noVariableRoot || this.root.equalsIgnoreCase(root))) {
                    return true;
                }
                if (this.material != null) {
                    for (ImmutablePair<String, Byte> pair : variable.getMaterialRoots()) {
                        if ((noVariableRoot || this.material.equalsIgnoreCase(pair.getKey())) && (pair.getValue() < 0 || this.data == pair.getValue())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean areSubRootsValid(Player player, Variable variable) {
        Map<String, List<String>> subRoots = variable.getSubRoots();
        if (subRoots.containsKey("name") && (!this.subRoots.containsKey("name") || !subRoots.get("name").contains(this.subRoots.get("name")))) {
            return false;
        }
        if (subRoots.containsKey("holding.item")) {
            ItemStack holding = ServerVersion.getVersion().getVersionId() > 183 ? player.getInventory().getItemInMainHand() : player.getItemInHand();
            if (holding.getType().equals(Material.AIR) || !holding.getType().toString().toLowerCase().concat(":").concat(Byte.toString(holding.getData().getData()))
                    .equalsIgnoreCase(subRoots.get("holding.item").get(0))) {
                return false;
            }
            ItemMeta itemMeta = holding.getItemMeta();
            if (subRoots.containsKey("holding.name") && Objects.nonNull(itemMeta) && !subRoots.get("holding.name").get(0).equals(itemMeta.getDisplayName())) {
                return false;
            }
            return !subRoots.containsKey("holding.amount") || Integer.parseInt(subRoots.get("holding.amount").get(0)) == holding.getAmount();
        }
        return true;
    }
}
