package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.util.BlockInfo;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.bukkit.block.data.BlockData;

public class BlockDataCondition extends BaseLootCondition {

    private List<String> blockData;

    public BlockDataCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        Optional<BlockInfo> blockInfo = context.getLootedBlockInfo();
        if (blockInfo.isEmpty())
            return false;

        BlockData blockData = blockInfo.get().getData();
        String blockDataString = blockData.getAsString(false);
        if (!blockDataString.contains("[") || !blockDataString.contains("]"))
            return false;

        blockDataString = blockDataString.substring(blockDataString.indexOf('[') + 1, blockDataString.lastIndexOf(']')).replaceAll(" ", "").toLowerCase(); // Remove [] and all spaces
        List<String> dataValues = List.of(blockDataString.split(","));
        return this.blockData.stream().anyMatch(dataValues::contains);
    }

    @Override
    public boolean parseValues(String[] values) {
        this.blockData = Arrays.stream(values).map(String::toLowerCase).toList();
        return !this.blockData.isEmpty();
    }

}
