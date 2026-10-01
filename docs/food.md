# Food (`net.satisfy.foundation.food`)

Two families – pick by one question: **are the effects fixed, or do they come from the ingredients?**

## Fixed effects – `Effect…`

The effects live in the item's `FoodProperties`; Foundation shows them in the tooltip.

| Class | Use for |
|---|---|
| `EffectFoodItem(props, duration, returnBowl)` | a meal; `returnBowl = true` gives the bowl back |
| `EffectDrinkItem(props, duration, returnBottle)` | a drink (drinking animation), gives the bottle back |
| `PlaceableEffectFoodItem(block, props)` | a dish item that can also be placed as a block |
| `TeaJugItem(block, props)` | a placeable jug/teapot with effect tooltip |
| `FoodBlock(props, maxBites, food)` | a placed dish that is eaten bite by bite |

```java
public static final RegistrySupplier<Item> MISO_SOUP = registerItem("miso_soup",
        () -> new EffectFoodItem(settings().food(MyFoods.MISO_SOUP), 900, true));
```

## Ingredient effects – `IngredientEffect…`

The dish takes over the effects of what went into it (cooking pot, stove, roaster …).

| Class | Use for |
|---|---|
| `IngredientEffectFoodItem(props, foodStages)` | a meal carrying ingredient effects |
| `PlaceableIngredientEffectFoodItem(block, props, foodStages)` | same, placeable |
| `IngredientEffectFoodBlock` (abstract) | the placed version, eaten in bites – you supply the block entity |
| `IngredientEffectFoodBlockEntity` | stores the effects of a placed dish |
| `IngredientEffectCarrier` | marker interface: "copy ingredient effects onto me" |
| `IngredientEffects` | read / write / apply the stored effects on an `ItemStack` |

When your station finishes a recipe:

```java
if (output.getItem() instanceof IngredientEffectCarrier) {
    for (ItemStack ingredient : usedIngredients) {
        IngredientEffects.getEffects(ingredient).forEach(effect -> IngredientEffects.addEffect(output, effect));
    }
}
```

A placed ingredient dish needs your own block entity type:

```java
public class LasagneBlock extends IngredientEffectFoodBlock {
    // constructor + codec() …
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new IngredientEffectFoodBlockEntity(MyBlockEntities.EFFECT_FOOD.get(), pos, state);
    }
}
```
