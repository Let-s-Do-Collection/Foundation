package net.satisfy.foundation.recipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Recipe;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/** Persists unlocked recipes per player via {@link RecipeUnlockSavedData}. */
public class RecipeUnlockManager {

    /** Heads up: currently stores the recipe TYPE key, not the recipe id. */
    public static void unlockRecipes(ServerPlayer player, List<Recipe<?>> recipes) {
        UUID uuid = player.getUUID();
        Set<ResourceLocation> unlocked = loadUnlockedRecipes(player);
        for (Recipe<?> recipe : recipes) {
            unlocked.add(BuiltInRegistries.RECIPE_TYPE.getKey(recipe.getType()));
        }
        saveUnlockedRecipes(player, unlocked);
    }

    /** @return true if the player did not unlock this recipe yet */
    public static boolean isRecipeLocked(ServerPlayer player, ResourceLocation recipeId) {
        return !loadUnlockedRecipes(player).contains(recipeId);
    }

    public static void saveUnlockedRecipes(ServerPlayer player, Set<ResourceLocation> recipes) {
        ServerLevel level = (ServerLevel) player.getCommandSenderWorld();
        RecipeUnlockSavedData data = level.getDataStorage()
                        .computeIfAbsent(RecipeUnlockSavedData.factory(), RecipeUnlockSavedData.DATA_NAME);
        data.setPlayerRecipes(player.getUUID(), recipes);
    }

    public static Set<ResourceLocation> loadUnlockedRecipes(ServerPlayer player) {
        ServerLevel level = (ServerLevel) player.getCommandSenderWorld();
        RecipeUnlockSavedData data = level.getDataStorage()
                .computeIfAbsent(RecipeUnlockSavedData.factory(), RecipeUnlockSavedData.DATA_NAME);
        return data.getPlayerRecipes(player.getUUID());
    }
}