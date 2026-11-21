package net.satisfy.foundation.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class TableBlock extends LineConnectingBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape TOP_SHAPE = box(0, 13, 0, 16, 16, 16);
    private static final VoxelShape[] LEG_SHAPES = {
            box(1, 0, 1, 4, 13, 4),
            box(12, 0, 1, 15, 13, 4),
            box(12, 0, 12, 15, 13, 15),
            box(1, 0, 12, 4, 13, 15)
    };

    public TableBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
    }

    protected VoxelShape getTableShape(LineConnectingType type, Direction facing) {
        if (type == LineConnectingType.MIDDLE) {
            return TOP_SHAPE;
        }
        boolean leftEnd = facing == Direction.NORTH && type == LineConnectingType.LEFT || facing == Direction.SOUTH && type == LineConnectingType.RIGHT;
        boolean rightEnd = facing == Direction.NORTH && type == LineConnectingType.RIGHT || facing == Direction.SOUTH && type == LineConnectingType.LEFT;
        boolean eastLeft = facing == Direction.EAST && type == LineConnectingType.LEFT || facing == Direction.WEST && type == LineConnectingType.RIGHT;
        boolean eastRight = facing == Direction.EAST && type == LineConnectingType.RIGHT || facing == Direction.WEST && type == LineConnectingType.LEFT;
        if (leftEnd) return Shapes.or(TOP_SHAPE, LEG_SHAPES[0], LEG_SHAPES[3]);
        if (rightEnd) return Shapes.or(TOP_SHAPE, LEG_SHAPES[1], LEG_SHAPES[2]);
        if (eastLeft) return Shapes.or(TOP_SHAPE, LEG_SHAPES[0], LEG_SHAPES[1]);
        if (eastRight) return Shapes.or(TOP_SHAPE, LEG_SHAPES[2], LEG_SHAPES[3]);
        return Shapes.or(TOP_SHAPE, LEG_SHAPES);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getTableShape(state.getValue(TYPE), state.getValue(FACING));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        return state == null ? null : state.setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
}
