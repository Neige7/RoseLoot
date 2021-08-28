package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.item.meta.ItemLootMeta;
import dev.rosewood.roseloot.util.LootUtils;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemLootItem implements LootItem<List<ItemStack>> {

    private final Material item;
    private final int min;
    private final int max;
    private final ItemLootMeta itemLootMeta;
    private final EnchantmentBonus enchantmentBonus;

    public ItemLootItem(Material item, int min, int max, ItemLootMeta itemLootMeta, EnchantmentBonus enchantmentBonus) {
        this.item = item;
        this.min = min;
        this.max = max;
        this.itemLootMeta = itemLootMeta;
        this.enchantmentBonus = enchantmentBonus;
    }

    @Override
    public List<ItemStack> create(LootContext context) {
        List<ItemStack> generatedItems = new ArrayList<>();

        int max = this.max;
        int amount = 0;
        ItemStack itemUsed = context.getItemUsed();
        if (this.enchantmentBonus != null && itemUsed != null) {
            ItemMeta itemMeta = itemUsed.getItemMeta();
            if (itemMeta != null) {
                int level = itemMeta.getEnchantLevel(this.enchantmentBonus.getEnchantment());
                if (this.enchantmentBonus.addToMax()) {
                    max += level * this.enchantmentBonus.getBonusPerLevel();
                } else {
                    amount += level * this.enchantmentBonus.getBonusPerLevel();
                }
            }
        }

        amount += LootUtils.randomInRange(this.min, max);
        if (amount > 0) {
            int maxStackSize = this.item.getMaxStackSize();
            ItemStack itemStack = this.itemLootMeta.apply(new ItemStack(this.item), context);
            while (amount > maxStackSize) {
                amount -= maxStackSize;
                ItemStack clone = itemStack.clone();
                clone.setAmount(maxStackSize);
                generatedItems.add(clone);
            }

            if (amount > 0) {
                ItemStack clone = itemStack.clone();
                clone.setAmount(amount);
                generatedItems.add(clone);
            }
        }

        return generatedItems;
    }

    public static class EnchantmentBonus {

        private final Enchantment enchantment;
        private final int bonusPerLevel;
        private final boolean addToMax;

        public EnchantmentBonus(Enchantment enchantment, int bonusPerLevel, boolean addToMax) {
            this.enchantment = enchantment;
            this.bonusPerLevel = bonusPerLevel;
            this.addToMax = addToMax;
        }

        public Enchantment getEnchantment() {
            return this.enchantment;
        }

        public int getBonusPerLevel() {
            return this.bonusPerLevel;
        }

        public boolean addToMax() {
            return this.addToMax;
        }

    }

}
