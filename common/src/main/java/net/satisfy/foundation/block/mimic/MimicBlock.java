package net.satisfy.foundation.block.mimic;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class MimicBlock {
    public static final BooleanProperty APPLIED = BooleanProperty.create("applied");
    private static final int DESTROY_EVENT = 2001;

    private MimicBlock() {
    }

    public static boolean canAccept(BlockGetter level, BlockPos pos, BlockState candidate) {
        if (candidate == null || candidate.isAir()) return false;
        if (candidate.getBlock() instanceof EntityBlock) return false;
        if (candidate.getRenderShape() != RenderShape.MODEL) return false;
        return Block.isShapeFullBlock(candidate.getShape(level, pos));
    }

    public static ItemInteractionResult use(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, Predicate<BlockState> accepts) {
        if (stack.getItem() instanceof PickaxeItem) {
            if (!state.getValue(APPLIED)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            if (!level.isClientSide) {
                remove(level, pos, state, true);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        if (state.getValue(APPLIED) || !(stack.getItem() instanceof BlockItem blockItem)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        BlockState mimic = blockItem.getBlock().defaultBlockState();
        if (!canAccept(level, pos, mimic) || !accepts.test(mimic)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!level.isClientSide && apply(level, pos, state, mimic) && !player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    public static boolean apply(Level level, BlockPos pos, BlockState state, BlockState mimic) {
        if (!(level.getBlockEntity(pos) instanceof MimicBlockEntity entity)) return false;
        entity.setMimicState(mimic);
        if (state.hasProperty(APPLIED)) {
            level.setBlock(pos, state.setValue(APPLIED, true), Block.UPDATE_ALL);
        }
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.levelEvent(DESTROY_EVENT, pos, Block.getId(mimic));
        }
        return true;
    }

    public static void remove(Level level, BlockPos pos, BlockState state, boolean drop) {
        if (!(level.getBlockEntity(pos) instanceof MimicBlockEntity entity)) return;
        BlockState mimic = entity.getMimicState();
        if (mimic == null) return;
        if (drop) {
            Block.popResource(level, pos, new ItemStack(mimic.getBlock()));
        }
        entity.setMimicState(null);
        if (state.hasProperty(APPLIED)) {
            level.setBlock(pos, state.setValue(APPLIED, false), Block.UPDATE_ALL);
        }
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.levelEvent(DESTROY_EVENT, pos, Block.getId(mimic));
        }
    }

    public static List<ItemStack> addDrops(List<ItemStack> drops, LootParams.Builder builder) {
        if (!(builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof MimicBlockEntity entity) || !entity.hasMimic()) {
            return drops;
        }
        List<ItemStack> result = new ArrayList<>(drops);
        result.add(new ItemStack(entity.getMimicState().getBlock()));
        return result;
    }
}
