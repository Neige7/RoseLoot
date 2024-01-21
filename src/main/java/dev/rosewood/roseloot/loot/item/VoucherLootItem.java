package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.condition.LootConditionParser;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.meta.ItemLootMeta;
import dev.rosewood.roseloot.provider.NumberProvider;
import dev.rosewood.roseloot.provider.StringProvider;
import dev.rosewood.roseloot.util.VoucherUtils;
import dev.rosewood.roseloot.util.nms.EnchantingUtils;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class VoucherLootItem extends ItemLootItem {

    private final String lootTable;

    public VoucherLootItem(String lootTable, Material item, NumberProvider amount, NumberProvider maxAmount, List<AmountModifier> amountModifiers, ItemLootMeta itemLootMeta, EnchantmentBonus enchantmentBonus, boolean smeltIfBurning, StringProvider nbt) {
        super(item, amount, maxAmount, amountModifiers, itemLootMeta, enchantmentBonus, smeltIfBurning, nbt);
        this.lootTable = lootTable;
    }

    @Override
    public List<ItemStack> generate(LootContext context) {
        List<ItemStack> items = super.generate(context);
        items.forEach(x -> VoucherUtils.setVoucherData(x, this.lootTable));
        return items;
    }

    public static VoucherLootItem fromSection(ConfigurationSection section) {
        String lootTable = section.getString("loottable");
        if (lootTable == null)
            return null;

        String itemString = section.getString("item");
        if (itemString == null)
            return null;

        Material item = Material.matchMaterial(itemString);
        if (item == null)
            return null;

        NumberProvider amount = NumberProvider.fromSection(section, "amount", 1);
        NumberProvider maxAmount = NumberProvider.fromSection(section, "max-amount", Integer.MAX_VALUE);

        List<AmountModifier> amountModifiers = new ArrayList<>();
        ConfigurationSection amountModifiersSection = section.getConfigurationSection("amount-modifiers");
        if (amountModifiersSection != null) {
            for (String key : amountModifiersSection.getKeys(false)) {
                ConfigurationSection entrySection = amountModifiersSection.getConfigurationSection(key);
                if (entrySection != null) {
                    List<LootCondition> conditions = new ArrayList<>();
                    for (String conditionString : entrySection.getStringList("conditions")) {
                        LootCondition condition = LootConditionParser.parse(conditionString);
                        if (condition != null)
                            conditions.add(condition);
                    }

                    NumberProvider value = NumberProvider.fromSection(entrySection, "value", 1);
                    boolean add = entrySection.getBoolean("add", false);
                    amountModifiers.add(new AmountModifier(conditions, value, add));
                }
            }
        }

        ConfigurationSection enchantmentBonusSection = section.getConfigurationSection("enchantment-bonus");
        ItemLootItem.EnchantmentBonus enchantmentBonus = null;
        if (enchantmentBonusSection != null) {
            BonusFormula formula = BonusFormula.fromString(enchantmentBonusSection.getString("formula", BonusFormula.UNIFORM.name()));
            String enchantmentString = enchantmentBonusSection.getString("enchantment");
            if (enchantmentString != null) {
                Enchantment enchantment = EnchantingUtils.getEnchantmentByName(enchantmentString);
                NumberProvider bonusPerLevel = NumberProvider.fromSection(enchantmentBonusSection, "bonus-per-level", 0);
                NumberProvider probability = NumberProvider.fromSection(enchantmentBonusSection, "probability", 0);
                if (enchantment != null)
                    enchantmentBonus = new ItemLootItem.EnchantmentBonus(formula, enchantment, bonusPerLevel, probability);
            }
        }

        boolean smeltIfBurning = section.getBoolean("smelt-if-burning", false);
        StringProvider nbt = StringProvider.fromSection(section, "nbt", null);
        ItemLootMeta itemLootMeta = ItemLootMeta.fromSection(item, section);
        return new VoucherLootItem(lootTable, item, amount, maxAmount, amountModifiers, itemLootMeta, enchantmentBonus, smeltIfBurning, nbt);
    }

}
