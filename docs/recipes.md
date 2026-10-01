# Recipes (`net.satisfy.foundation.recipe`, `net.satisfy.foundation.compat`)

## Recipe unlock book

Stations can lock recipe **types** until the player reads a book.

```java
// in your station, before crafting
if (RecipeUnlockManager.isRecipeLocked(serverPlayer, BuiltInRegistries.RECIPE_TYPE.getKey(recipe.getType()))) {
    return; // locked
}

// give a book that unlocks recipes
ItemStack book = GrandmothersRecipeBookItem.createUnlockerForRecipes(MyItems.RECIPE_BOOK.get(), "mymod:pie", "mymod:tart");
```

Unlocks are stored per world (saved data `farm_and_charm_recipe_unlock_data`, kept for old worlds).

## REI / JEI layout helpers

Shared sizes and arrows so every Let's Do recipe screen looks alike.

```java
RecipeViewerLayout.SLOT;                       // 18
RecipeViewerLayout.drawRightArrow(graphics, x, y);
RecipeViewerLayout.drawDownArrow(graphics, x, y);
RecipeViewerLayout.assembly(inputs, ordered, maxOrderedInputs); // slot/arrow positions for step-by-step recipes
```

REI widgets:

```java
ReiWidgets.inputSlot(widgets, x, y, entries);
ReiWidgets.outputSlot(widgets, x, y, entries);
ReiWidgets.rightArrow(widgets, x, y, Component.literal("20s"));
```
