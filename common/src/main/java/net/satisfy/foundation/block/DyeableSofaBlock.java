package net.satisfy.foundation.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.foundation.util.DyeHelper;
import org.jetbrains.annotations.NotNull;

public class DyeableSofaBlock extends SofaBlock {
    public static final EnumProperty<ClothColor> COLOR = EnumProperty.create("color", ClothColor.class);

    public DyeableSofaBlock(Properties properties) {
        this(properties, 0.2);
    }

    public DyeableSofaBlock(Properties properties, double seatHeight) {
        super(properties, seatHeight);
        registerDefaultState(defaultBlockState().setValue(COLOR, ClothColor.NONE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(COLOR);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.getItem() instanceof DyeItem dye && state.getValue(COLOR).color() != dye.getDyeColor()) {
            return DyeHelper.apply(stack, dye.getDyeColor(), state.setValue(COLOR, ClothColor.of(dye.getDyeColor())), level, pos, player);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }
}
