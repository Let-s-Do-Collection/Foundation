package net.satisfy.foundation.recipe.book;

import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

/**
 * Implement on a station menu to get the recipe book.
 * Ingredient {@code i} of a recipe goes into menu slot {@code recipeBookInputSlots(type)[i]}.
 * <p>
 * One recipe type: override {@link #recipeBookType()} and {@link #recipeBookInputSlots()}.
 * Several types (one tab each): override {@link #recipeBookTypes()} and {@link #recipeBookInputSlots(RecipeType)}.
 */
public interface StationRecipeBookMenu {

    /** Recipes of this type are listed in the book. */
    default RecipeType<?> recipeBookType() {
        throw new UnsupportedOperationException("Override recipeBookType() or recipeBookTypes()");
    }

    /** Menu slot indices (not container indices) for the ingredients, in ingredient order. */
    default int[] recipeBookInputSlots() {
        throw new UnsupportedOperationException("Override recipeBookInputSlots() or recipeBookInputSlots(RecipeType)");
    }

    /** All listed types, the book shows a tab per type when there is more than one. */
    default List<RecipeType<?>> recipeBookTypes() {
        return List.of(recipeBookType());
    }

    default int[] recipeBookInputSlots(RecipeType<?> type) {
        return recipeBookInputSlots();
    }

    /** If true, only recipes unlocked through {@link net.satisfy.foundation.recipe.RecipeUnlockManager} show up. */
    default boolean recipeBookRequiresUnlock() {
        return false;
    }
}
