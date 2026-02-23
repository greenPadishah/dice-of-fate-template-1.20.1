package net.bozkurt.dof.entity;

import net.bozkurt.dof.DiceOfFate;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<ThrownDiceEntity> THROWN_DICE = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier(DiceOfFate.MOD_ID, "thrown_dice"),
        FabricEntityTypeBuilder.<ThrownDiceEntity>create(SpawnGroup.MISC, ThrownDiceEntity::new)
            .dimensions(EntityDimensions.fixed(0.25f, 0.25f))
            .trackRangeBlocks(4)
            .trackedUpdateRate(10)
            .build()
    );

    public static void register() {
        DiceOfFate.LOGGER.info("Registering entities for " + DiceOfFate.MOD_ID);
    }
}
