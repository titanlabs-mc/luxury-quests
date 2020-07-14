package io.github.luxuryquests.checker;

import io.github.luxuryquests.objects.quest.SubQuest;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.logging.Level;

public class QuestChecker {

    public boolean validateSubQuest(String category, String parentId, String subId, String name, String type, int requiredProgress) {
        if (name == null || type == null || requiredProgress < 1 ) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not load sub quest ".concat(subId).concat(" for quest with ID ").concat(parentId)
                    .concat(" in category ").concat(category).concat(". ").concat("Name: ").concat(String.valueOf(name)).concat(" Type: ")
                    .concat(String.valueOf(type)).concat(" Required Progress: ").concat(String.valueOf(requiredProgress)).concat(" (errors if less than 1)"));
            return false;
        }
        return true;
    }

    public boolean validateQuest(String category, String id, ItemStack questItem, String name, Set<SubQuest> subQuests) {
        if (name == null || questItem == null || subQuests.isEmpty()) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not load quest with ID ".concat(id).concat(" in category ").concat(category)
                    .concat(". Quest name: ").concat(String.valueOf(name)).concat(" Quest item: ").concat(String.valueOf(questItem))
                    .concat("Are objective sub quests empty? ").concat(subQuests.isEmpty() ? "YES (issue present)" : "No (fine)"));
            return false;
        }
        return true;
    }
}
