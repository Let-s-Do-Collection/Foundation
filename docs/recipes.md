# Recipes (`net.satisfy.foundation.recipe`, `net.satisfy.foundation.compat`)

## Station recipe book

The green recipe book from the crafting table, for any modded station. Same look (vanilla textures), search, "only craftable" filter, pages. Clicking a recipe fills the inputs from the inventory (shift = as many as fit); if items are missing, the ingredients are shown as ghosts in the slots.

```java
// menu
public class StoveMenu extends AbstractContainerMenu implements StationRecipeBookMenu {
    @Override public RecipeType<?> recipeBookType() { return MyRecipes.STOVE.get(); }
    @Override public int[] recipeBookInputSlots() { return new int[]{0, 1, 2}; } // ingredient i -> menu slot i
    @Override public boolean recipeBookRequiresUnlock() { return true; }         // optional, hides locked recipes
}

// screen: extend StationRecipeBookScreen instead of AbstractContainerScreen, nothing else to call
public class StoveScreen extends StationRecipeBookScreen<StoveMenu> { ... }
```

Stations with several recipe types (e.g. apple press: mashing + fermenting) get one vanilla tab per type:

```java
@Override public List<RecipeType<?>> recipeBookTypes() { return List.of(MASHING.get(), FERMENTING.get()); }
@Override public int[] recipeBookInputSlots(RecipeType<?> type) { return type == MASHING.get() ? new int[]{0} : new int[]{1}; }
```

The tooltip of a recipe lists its ingredients. Optional hooks on the menu (all client side):

| Hook | Use |
|---|---|
| `recipeBookTabName(type)` | tooltip of the tab |
| `recipeBookExtrasMet(recipe)` | things outside the input slots (fluid, bottle...); false shows the recipe as not craftable, ingredients can still be placed |
| `recipeBookResultSlot(type)` | menu slot of the result, the ghost shows the result there |
| `recipeBookExtraInputs(recipe)` | slot -> ingredient outside the recipe (bottle, bowl...), placed and counted like an ingredient |
| `recipeBookExtraGhosts(recipe)` | slot -> ingredient shown as ghost only (juice, bottle...), never placed |
| `appendRecipeBookTooltip(recipe, lines)` | extra tooltip lines, e.g. needed fluid and current fill level |

`StationRecipeBook#getGhost()` returns the clicked recipe, e.g. to extend own tooltips.

Override `recipeBookButtonX()` / `recipeBookButtonY()` to move the button. Screens that can't extend the base class can forward the calls to a `StationRecipeBook` themselves (see `StationRecipeBookScreen`).

With `recipeBookRequiresUnlock()` a recipe shows up once its id **or** its recipe type is unlocked. Unlocks are synced to the client on join and on every change.

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

The book item saves the recipe ids, `RecipeUnlockManager.unlockRecipes` saves whole recipe types. Unlocks are stored per world (saved data `farm_and_charm_recipe_unlock_data`, kept for old worlds).

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
