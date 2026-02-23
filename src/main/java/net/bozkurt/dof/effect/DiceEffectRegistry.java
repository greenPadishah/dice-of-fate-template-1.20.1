package net.bozkurt.dof.effect;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.util.math.random.Random;
import java.util.ArrayList;

public final class DiceEffectRegistry {
    private static final Map<DiceType, Map<EffectAlignment, List<DiceEffect>>> EFFECTS = new EnumMap<>(DiceType.class);

    static {
        for (DiceType type : DiceType.values()) {
            Map<EffectAlignment, List<DiceEffect>> byAlignment = new EnumMap<>(EffectAlignment.class);
            for (EffectAlignment alignment : EffectAlignment.values()) {
                byAlignment.put(alignment, new ArrayList<>());
            }
            EFFECTS.put(type, byAlignment);
        }
    }

    private DiceEffectRegistry() {
    }

    public static void register(DiceType type, EffectAlignment alignment, DiceEffect effect) {
        EFFECTS.get(type).get(alignment).add(effect);
    }

    public static DiceEffect getRandom(DiceType type, EffectAlignment alignment, Random random) {
        List<DiceEffect> list = EFFECTS.get(type).get(alignment);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(random.nextInt(list.size()));
    }

    public static List<DiceEffect> getAllEffects() {
        List<DiceEffect> allEffects = new ArrayList<>();
        for (DiceType type : DiceType.values()) {
            for (EffectAlignment alignment : EffectAlignment.values()) {
                allEffects.addAll(EFFECTS.get(type).get(alignment));
            }
        }
        return allEffects;
    }
}
