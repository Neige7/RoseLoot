package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.util.nms.StructureUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.StructureType;

public class FeatureCondition extends BaseLootCondition {

    private List<StructureType> features;

    public FeatureCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        Optional<Location> origin = context.get(LootContextParams.ORIGIN);
        if (origin.isEmpty())
            return false;

        for (StructureType structureType : this.features)
            if (StructureUtils.isWithinStructure(origin.get(), structureType))
                return true;

        return false;
    }

    @Override
    public boolean parseValues(String[] values) {
        this.features = new ArrayList<>();

        for (String value : values) {
            try {
                StructureType structureType = StructureType.getStructureTypes().get(value.toLowerCase());
                if (structureType != null)
                    this.features.add(structureType);
            } catch (Exception ignored) { }
        }

        return !this.features.isEmpty();
    }

}
