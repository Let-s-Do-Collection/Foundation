package net.satisfy.foundation.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.foundation.seat.SeatUtil;
import org.jetbrains.annotations.NotNull;

public class SofaBlock extends LineConnectingBlock {
    private final double seatHeight;

    public SofaBlock(Properties properties) {
        this(properties, 0.2);
    }

    public SofaBlock(Properties properties, double seatHeight) {
        super(properties);
        this.seatHeight = seatHeight;
    }

    protected VoxelShape getSofaShape(LineConnectingType type, Direction facing) {
        return Shapes.block();
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getSofaShape(state.getValue(TYPE), state.getValue(FACING));
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return SeatUtil.onUse(level, player, hand, hit, seatHeight);
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        super.fallOn(level, state, pos, entity, fallDistance * 0.5F);
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter level, Entity entity) {
        if (entity.isSuppressingBounce()) {
            super.updateEntityAfterFallOn(level, entity);
            return;
        }
        Vec3 motion = entity.getDeltaMovement();
        if (motion.y < 0.0) {
            double bounceFactor = entity instanceof LivingEntity ? 1.0 : 0.8;
            entity.setDeltaMovement(motion.x, -motion.y * 0.66 * bounceFactor, motion.z);
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        SeatUtil.onStateReplaced(level, pos);
        super.onRemove(state, level, pos, newState, moved);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }
}
