package dev.rosewood.roseloot.listener;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.listener.helper.LazyLootTableListener;
import dev.rosewood.roseloot.loot.LootResult;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.loot.table.LootTableTypes;
import dev.rosewood.roseloot.manager.ConfigurationManager;
import dev.rosewood.roseloot.util.LootUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

public class AdvancementListener extends LazyLootTableListener {

    public AdvancementListener(RosePlugin rosePlugin) {
        super(rosePlugin, LootTableTypes.ADVANCEMENT);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAdvancementDone(PlayerAdvancementDoneEvent event) {
        Player player = event.getPlayer();
        if (ConfigurationManager.Setting.DISABLED_WORLDS.getStringList().stream().anyMatch(x -> x.equalsIgnoreCase(player.getWorld().getName())))
            return;

        LootContext lootContext = LootContext.builder(LootUtils.getEntityLuck(player))
                .put(LootContextParams.ORIGIN, player.getLocation())
                .put(LootContextParams.LOOTER, player)
                .put(LootContextParams.ADVANCEMENT_KEY, event.getAdvancement().getKey())
                .build();
        LootResult lootResult = LOOT_TABLE_MANAGER.getLoot(LootTableTypes.ADVANCEMENT, lootContext);
        if (lootResult.isEmpty())
            return;

        lootResult.getLootContents().dropForPlayer(player);
    }

}
