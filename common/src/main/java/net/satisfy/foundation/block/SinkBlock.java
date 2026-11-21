package net.satisfy.foundation.block;

import net.satisfy.foundation.util.ShapeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.satisfy.foundation.registry.FoundationParticles;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.InteractionResult;
import net.minecraft.tags.ItemTags;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Two block sink with a tap. Fill it with a bucket or bottle (or just click with
 * empty hand), take water back out again. Drips a bit on the client.
 */
@SuppressWarnings({"unused", "deprecation"})
public class SinkBlock extends Block implements EntityBlock {
    public static final BooleanProperty FILLED = BooleanProperty.create("filled");
    public static final EnumProperty<DoubleBlockHalf> HALF = EnumProperty.create("half", DoubleBlockHalf.class);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final Map<Direction, VoxelShape> TOP_SHAPES = new HashMap<>();
    public static final Map<Direction, VoxelShape> BOTTOM_SHAPES = new HashMap<>();

    static {
        Supplier<VoxelShape> topShapeSupplier = SinkBlock::makeTopShape;
        Supplier<VoxelShape> bottomShapeSupplier = SinkBlock::makeBottomShape;

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            TOP_SHAPES.put(direction, ShapeUtil.rotateShape(Direction.NORTH, direction, topShapeSupplier.get()));
            BOTTOM_SHAPES.put(direction, ShapeUtil.rotateShape(Direction.NORTH, direction, bottomShapeSupplier.get()));
        }
    }

    private final Layout layout;

    public SinkBlock(Properties settings) {
        this(settings, Layout.DEFAULT);
    }

    public SinkBlock(Properties settings, Layout layout) {
        super(settings);
        this.layout = layout;
        this.registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER).setValue(FILLED, false).setValue(FACING, Direction.NORTH));
    }

    public record Layout(float basinX0, float basinX1, float basinZ0, float basinZ1, float surface, float levelStep, float spoutX, float spoutY, float spoutZ) {
        public static final Layout DEFAULT = new Layout(3, 13, 3, 12, 12, 1, 8, 3.52F, 9);

        public float surfaceHeight(float waterLevel) {
            return (surface + waterLevel * levelStep) / 16.0F;
        }
    }

    public Layout layout() {
        return layout;
    }

    public static Layout layoutOf(BlockState state) {
        return state.getBlock() instanceof SinkBlock sink ? sink.layout() : Layout.DEFAULT;
    }

    private static VoxelShape makeTopShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.375, 0, 0.75, 0.625, 0.0625, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.125, 0.75, 0.375, 0.3125, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.3125, 0.5, 0.5625, 0.5, 0.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.25, 0.4375, 0.625, 0.3125, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.0625, 0.8125, 0.5625, 0.375, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.375, 0.625, 0.5625, 0.5, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.1875, 0.8125, 0.4375, 0.25, 0.875), BooleanOp.OR);
        return shape;
    }

    private static VoxelShape makeBottomShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0, 0.125, 1, 0.75, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.75, 0.1875, 0.1875, 1, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.75, 0.75, 1, 1, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.75, 0, 1, 1, 0.1875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.8125, 0.75, 0.1875, 1, 1, 0.75), BooleanOp.OR);
        return shape;
    }

    protected VoxelShape getSinkShape(DoubleBlockHalf half, Direction facing) {
        return half == DoubleBlockHalf.UPPER ? TOP_SHAPES.get(facing) : BOTTOM_SHAPES.get(facing);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return getSinkShape(state.getValue(HALF), state.getValue(FACING));
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack itemStack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (itemStack.isEmpty()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        SinkBlockEntity sink = sinkAt(world, pos, state);
        if (sink == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        int water = sink.getWaterLevel();
        ItemStack result = ItemStack.EMPTY;
        int change = 0;
        if (itemStack.is(Items.WATER_BUCKET) && water < SinkBlockEntity.MAX_LEVEL) {
            result = new ItemStack(Items.BUCKET);
            change = SinkBlockEntity.MAX_LEVEL - water;
        } else if (itemStack.is(Items.BUCKET) && water == SinkBlockEntity.MAX_LEVEL) {
            result = new ItemStack(Items.WATER_BUCKET);
            change = -water;
        } else if (itemStack.is(Items.GLASS_BOTTLE) && water > 0) {
            result = PotionContents.createItemStack(Items.POTION, Potions.WATER);
            change = -1;
        } else if (isWaterBottle(itemStack) && water < SinkBlockEntity.MAX_LEVEL) {
            result = new ItemStack(Items.GLASS_BOTTLE);
            change = 1;
        } else if (water > 0) {
            result = washed(itemStack);
            change = result.isEmpty() ? 0 : -1;
        }
        if (change == 0) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!world.isClientSide) {
            sink.setWaterLevel(water + change);
            world.playSound(null, pos, change > 0 ? SoundEvents.BUCKET_EMPTY : SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 0.8F, 1.0F);
            if (change > 0 && world instanceof ServerLevel server) {
                BlockPos base = sink.getBlockPos();
                double surface = base.getY() + layout.surfaceHeight(water + change);
                for (int i = 0; i < 12; i++) {
                    double x = base.getX() + 0.3 + world.random.nextDouble() * 0.4;
                    double z = base.getZ() + 0.3 + world.random.nextDouble() * 0.4;
                    server.sendParticles(ParticleTypes.SPLASH, x, surface, z, 1, 0, 0, 0, 0.01);
                }
            }
            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);
                if (!player.getInventory().add(result)) {
                    player.drop(result, false);
                }
            }
        }
        return ItemInteractionResult.sidedSuccess(world.isClientSide);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        SinkBlockEntity sink = sinkAt(world, pos, state);
        if (sink == null) {
            return InteractionResult.PASS;
        }
        if (!world.isClientSide) {
            if (player.isOnFire() && sink.getWaterLevel() > 0) {
                player.clearFire();
                sink.setWaterLevel(sink.getWaterLevel() - 1);
                world.playSound(null, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 0.8F, 1.2F);
            } else {
                sink.toggle();
            }
        }
        return InteractionResult.sidedSuccess(world.isClientSide);
    }

    private static boolean isWaterBottle(ItemStack stack) {
        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        return stack.is(Items.POTION) && contents != null && contents.is(Potions.WATER);
    }

    private static ItemStack washed(ItemStack stack) {
        if (stack.is(ItemTags.DYEABLE) && stack.has(DataComponents.DYED_COLOR)) {
            ItemStack clean = stack.copyWithCount(1);
            clean.remove(DataComponents.DYED_COLOR);
            return clean;
        }
        BannerPatternLayers layers = stack.get(DataComponents.BANNER_PATTERNS);
        if (stack.getItem() instanceof BannerItem && layers != null && !layers.layers().isEmpty()) {
            ItemStack clean = stack.copyWithCount(1);
            clean.set(DataComponents.BANNER_PATTERNS, layers.removeLast());
            return clean;
        }
        if (Block.byItem(stack.getItem()) instanceof ShulkerBoxBlock box && box.getColor() != null) {
            return stack.transmuteCopy(Items.SHULKER_BOX, 1);
        }
        return ItemStack.EMPTY;
    }

    public static @Nullable SinkBlockEntity sinkAt(Level world, BlockPos pos, BlockState state) {
        BlockPos base = state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
        return world.getBlockEntity(base) instanceof SinkBlockEntity sink ? sink : null;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? new SinkBlockEntity(pos, state) : null;
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        if (state.getValue(HALF) != DoubleBlockHalf.LOWER) {
            return null;
        }
        if (world.isClientSide) {
            return (level, pos, blockState, blockEntity) -> {
                if (blockEntity instanceof SinkBlockEntity sink) {
                    SinkBlockEntity.clientTick(sink);
                }
            };
        }
        return (level, pos, blockState, blockEntity) -> {
            if (blockEntity instanceof SinkBlockEntity sink) {
                SinkBlockEntity.serverTick(level, pos, blockState, sink);
            }
        };
    }

    @Override
    protected @NotNull RenderShape getRenderShape(BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.UPPER ? RenderShape.INVISIBLE : RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF, FILLED, FACING);
    }


    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockPos = context.getClickedPos();
        return context.getLevel().getBlockState(blockPos.above()).canBeReplaced(context) ? this.defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER).setValue(FACING, context.getHorizontalDirection().getOpposite()) : null;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        world.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockPos blockPos = pos.below();
            BlockState blockState = world.getBlockState(blockPos);
            return blockState.is(this) && blockState.getValue(HALF) == DoubleBlockHalf.LOWER;
        } else {
            return super.canSurvive(state, world, pos);
        }
    }

    /** Places both halves at once, handy for worldgen/structures. */
    public static void placeAt(LevelAccessor levelAccessor, BlockState blockState, BlockPos blockPos, int i) {
        BlockPos blockPos2 = blockPos.above();
        levelAccessor.setBlock(blockPos, copyWaterloggedFrom(levelAccessor, blockPos, blockState.setValue(HALF, DoubleBlockHalf.LOWER)), i);
        levelAccessor.setBlock(blockPos2, copyWaterloggedFrom(levelAccessor, blockPos2, blockState.setValue(HALF, DoubleBlockHalf.UPPER)), i);
    }


    private static BlockState copyWaterloggedFrom(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
        return blockState.hasProperty(BlockStateProperties.WATERLOGGED) ? blockState.setValue(BlockStateProperties.WATERLOGGED, levelReader.isWaterAt(blockPos)) : blockState;
    }

    protected static void preventCreativeDropFromBottomPart(Level level, BlockPos blockPos, BlockState blockState, Player player) {
        DoubleBlockHalf doubleBlockHalf = blockState.getValue(HALF);
        if (doubleBlockHalf == DoubleBlockHalf.UPPER) {
            BlockPos blockPos2 = blockPos.below();
            BlockState blockState2 = level.getBlockState(blockPos2);
            if (blockState2.is(blockState.getBlock()) && blockState2.getValue(HALF) == DoubleBlockHalf.LOWER) {
                BlockState blockState3 = blockState2.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                level.setBlock(blockPos2, blockState3, 35);
                level.levelEvent(player, 2001, blockPos2, Block.getId(blockState2));
            }
        }

    }

    @Override
    public @NotNull BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        DoubleBlockHalf half = state.getValue(HALF);
        BlockPos blockPos = half == DoubleBlockHalf.LOWER ? pos.above() : pos.below();
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.getBlock() == this && blockState.getValue(HALF) != half) {
            world.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 35);
            world.levelEvent(player, 2001, blockPos, Block.getId(blockState));
            if (!world.isClientSide && !player.isCreative()) {
                dropResources(state, world, pos, null, player, player.getMainHandItem());
                dropResources(blockState, world, blockPos, null, player, player.getMainHandItem());
            }
        }
        return super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack) {
        super.playerDestroy(level, player, blockPos, Blocks.AIR.defaultBlockState(), blockEntity, itemStack);
        if (blockState.getValue(HALF) == DoubleBlockHalf.LOWER) {
            BlockPos blockPos2 = blockPos.above();
            BlockState blockState2 = level.getBlockState(blockPos2);

            if (blockState2.is(this) && blockState2.getValue(HALF) == DoubleBlockHalf.UPPER) {
                level.destroyBlock(blockPos2, true);
            }
        } else {
            BlockPos blockPos1 = blockPos.below();
            BlockState blockState1 = level.getBlockState(blockPos1);

            if (blockState1.is(this) && blockState1.getValue(HALF) == DoubleBlockHalf.LOWER) {
                level.destroyBlock(blockPos1, true);
            }
        }
    }

    @Override
    public long getSeed(BlockState blockState, BlockPos blockPos) {
        return Mth.getSeed(blockPos.getX(), blockPos.below(blockState.getValue(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), blockPos.getZ());
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource randomSource) {
        if (state.getValue(HALF) != DoubleBlockHalf.UPPER || !(world.getBlockEntity(pos.below()) instanceof SinkBlockEntity sink)) {
            return;
        }
        Vec3 spout = spout(pos, state.getValue(FACING));
        if (sink.isOpen()) {
            world.addParticle(ParticleTypes.FALLING_WATER, spout.x, spout.y, spout.z, 0.0, 0.0, 0.0);
            world.addParticle(FoundationParticles.WATER_DRIP.get(), spout.x, spout.y, spout.z, 0.0, 0.0, 0.0);
            if (randomSource.nextInt(3) == 0) {
                double surface = pos.getY() - 1 + layout.surfaceHeight(Math.max(1, sink.getWaterLevel()));
                world.addParticle(FoundationParticles.WATER_SPLASH.get(), spout.x, surface, spout.z, 0.0, 0.0, 0.0);
            }
            if (randomSource.nextInt(20) == 0) {
                world.playLocalSound(spout.x, spout.y, spout.z, SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, 0.3F, 1.4F, false);
            }
        } else if (randomSource.nextFloat() < 0.05F) {
            world.addParticle(FoundationParticles.WATER_DRIP.get(), spout.x, spout.y, spout.z, 0.0, 0.0, 0.0);
        }
    }

    public Vec3 spout(BlockPos upper, Direction facing) {
        double x = layout.spoutX() / 16.0, z = layout.spoutZ() / 16.0;
        for (int turn = 0; turn < (int) (facing.toYRot() / 90.0F + 2) % 4; turn++) {
            double rotated = 1.0 - z;
            z = x;
            x = rotated;
        }
        return new Vec3(upper.getX() + x, upper.getY() + layout.spoutY() / 16.0, upper.getZ() + z);
    }
}