# Effect Translation System

This document explains how to properly add translations for dice effects.

## Adding a New Effect

When you create a new effect in `DiceEffects.java`, follow these steps:

### 1. Define the Effect ID
Create an effect with a clear ID following the pattern: `{dice_color}_{effect_type}`

Effect ID naming guidelines:
- **dice_color**: `white`, `black`, or `red`
- **effect_type**: Descriptive name of the effect (snake_case)

Examples:
- `white_full_health` - White dice, good health effect
- `white_downgrade_gear` - White dice, bad gear effect
- `black_fill_stacks` - Black dice, good inventory effect
- `black_stash_and_launch` - Black dice, bad displacement effect
- `red_custom_effect` - Red dice, admin/custom effect

### 2. Add Translation Entry
Add a translation key to `src/main/resources/assets/dice-of-fate/lang/en_us.json` in the appropriate category section:

```json
// WHITE DICE - GOOD EFFECTS
"effect.dice-of-fate.white_new_good_effect": "Display Name"

// WHITE DICE - BAD EFFECTS
"effect.dice-of-fate.white_new_bad_effect": "Display Name"

// BLACK DICE - BAD EFFECTS
"effect.dice-of-fate.black_new_bad_effect": "Display Name"

// BLACK DICE - GOOD EFFECTS
"effect.dice-of-fate.black_new_good_effect": "Display Name"
```

The categories are organized as:
1. White Dice - Good Effects
2. White Dice - Bad Effects
3. Black Dice - Bad Effects
4. Black Dice - Good Effects

### 3. Translation Key Format
The auto-generated key is: `effect.dice-of-fate.` + effect.id()

So if your effect ID is `my_new_effect`, the translation key will be:
`effect.dice-of-fate.my_new_effect`

Make sure this key exists in the lang file!

## Current Effects

### White Dice - Good Effects
- `white_full_health` → "Full Health Recovery"
- `white_full_hunger` → "Feast"
- `white_upgrade_gear` → "Gear Upgrade"

### White Dice - Bad Effects
- `white_half_heart` → "Half Heart Curse"
- `white_empty_hunger` → "Starvation"
- `white_downgrade_gear` → "Gear Downgrade"

### Black Dice - Bad Effects
- `black_remove_diamonds` → "Diamond Curse"
- `black_stash_and_launch` → "Launched into the Void"

### Black Dice - Good Effects
- `black_fill_stacks` → "Full Stacks"
- `black_good_loot_chest` → "Treasure Chest"

## Organization

In the `en_us.json` file, effects are organized by:
1. **Dice Type**: White Dice, Black Dice
2. **Alignment**: Good Effects, Bad Effects

When adding new effects, place the translation in the appropriate category section with a category comment above it.

Example for a new white dice good effect:
```json
"_comment": "WHITE DICE - GOOD EFFECTS",
"effect.dice-of-fate.white_full_health": "Full Health Recovery",
"effect.dice-of-fate.white_new_effect": "My New Effect"
```

## Code Reference

When registering effects in `DiceEffects.registerDefaults()`:

```java
DiceEffectRegistry.register(DiceType.WHITE, EffectAlignment.GOOD,
    new SimpleDiceEffect("my_new_effect", (player, random) -> {
        // Effect implementation
    })
);
```

The effect ID (`"my_new_effect"`) must have a translation key in the lang file!
