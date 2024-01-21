package dev.rosewood.roseloot.listener;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.listener.helper.LazyLootTableListener;
import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.LootResult;
import dev.rosewood.roseloot.loot.OverwriteExisting;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.loot.table.LootTableTypes;
import dev.rosewood.roseloot.manager.ConfigurationManager.Setting;
import dev.rosewood.roseloot.util.EntitySpawnUtil;
import dev.rosewood.roseloot.util.LootUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.LootGenerateEvent;

public class LootGenerateListener extends LazyLootTableListener {

    public LootGenerateListener(RosePlugin rosePlugin) {
        super(rosePlugin, LootTableTypes.CONTAINER);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onLootGenerate(LootGenerateEvent event) {
        if (event.getInventoryHolder() instanceof Container container) {
            Block block = container.getBlock();
            if (Setting.DISABLED_WORLDS.getStringList().stream().anyMatch(x -> x.equalsIgnoreCase(block.getWorld().getName())))
                return;

            LivingEntity looter = null;
            if (event.getEntity() instanceof LivingEntity)
                looter = (LivingEntity) event.getEntity();

            LootContext lootContext = LootContext.builder(LootUtils.getEntityLuck(looter))
                    .put(LootContextParams.ORIGIN, block.getLocation())
                    .put(LootContextParams.LOOTER, looter)
                    .put(LootContextParams.LOOTED_BLOCK, block)
                    .put(LootContextParams.VANILLA_LOOT_TABLE_KEY, event.getLootTable().getKey())
                    .put(LootContextParams.HAS_EXISTING_ITEMS, !event.getLoot().isEmpty())
                    .build();
            LootResult lootResult = LOOT_TABLE_MANAGER.getLoot(LootTableTypes.CONTAINER, lootContext);
            if (lootResult.isEmpty())
                return;

            LootContents lootContents = lootResult.getLootContents();

            // Overwrite existing loot if applicable
            if (lootResult.doesOverwriteExisting(OverwriteExisting.ITEMS))
                event.getLoot().clear();

            // Set items and drop experience
            event.getLoot().addAll(lootResult.getLootContents().getItems());

            int experience = lootContents.getExperience();
            if (experience > 0) {
                Location location = looter == null ? block.getLocation() : looter.getLocation();
                EntitySpawnUtil.spawn(location, ExperienceOrb.class, x -> x.setExperience(experience));
            }

            lootContents.triggerExtras(block.getLocation());
        } else if (event.getInventoryHolder() instanceof Entity entity) {
            if (Setting.DISABLED_WORLDS.getStringList().stream().anyMatch(x -> x.equalsIgnoreCase(entity.getWorld().getName())))
                return;

            LivingEntity looter = null;
            if (event.getEntity() instanceof LivingEntity)
                looter = (LivingEntity) event.getEntity();

            LootContext lootContext = LootContext.builder(LootUtils.getEntityLuck(looter))
                    .put(LootContextParams.ORIGIN, entity.getLocation())
                    .put(LootContextParams.LOOTER, looter)
                    .put(LootContextParams.VANILLA_LOOT_TABLE_KEY, event.getLootTable().getKey())
                    .put(LootContextParams.HAS_EXISTING_ITEMS, !event.getLoot().isEmpty())
                    .build();
            LootResult lootResult = LOOT_TABLE_MANAGER.getLoot(LootTableTypes.CONTAINER, lootContext);
            if (lootResult.isEmpty())
                return;

            LootContents lootContents = lootResult.getLootContents();

            // Overwrite existing loot if applicable
            if (lootResult.doesOverwriteExisting(OverwriteExisting.ITEMS))
                event.getLoot().clear();

            // Set items and drop experience
            event.getLoot().addAll(lootResult.getLootContents().getItems());

            int experience = lootContents.getExperience();
            if (experience > 0) {
                Location location = looter == null ? entity.getLocation() : looter.getLocation();
                EntitySpawnUtil.spawn(location, ExperienceOrb.class, x -> x.setExperience(experience));
            }

            lootContents.triggerExtras(entity.getLocation());
        }
    }

}
