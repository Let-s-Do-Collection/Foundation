package net.satisfy.foundation.seat;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

/**
 * Invisible entity the player rides while sitting. Gets discarded on dismount
 * and puts the player back where they came from.
 */
public class ChairEntity extends Entity {
    private BlockPos seatPos;
    private boolean seatPosInit;

    public ChairEntity(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    public void setSeatPos(BlockPos pos) {
        this.seatPos = pos.immutable();
        this.seatPosInit = true;
    }

    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        if (passenger instanceof Player p) {
            BlockPos pos = SeatUtil.getPreviousPlayerPosition(p, this);
            if (pos != null) {
                this.discard();
                return new Vec3((double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5);
            }
        }

        this.discard();
        return super.getDismountLocationForPassenger(passenger);
    }

    public void remove(RemovalReason reason) {
        super.remove(reason);
        SeatUtil.removeChairEntity(this.level(), this.seatPos != null ? this.seatPos : this.blockPosition());
    }

    protected void readAdditionalSaveData(CompoundTag nbt) {
        if (nbt.contains("spx")) {
            this.seatPos = new BlockPos(nbt.getInt("spx"), nbt.getInt("spy"), nbt.getInt("spz"));
            this.seatPosInit = true;
        }
    }

    protected void addAdditionalSaveData(CompoundTag nbt) {
        BlockPos p = this.seatPos != null ? this.seatPos : this.blockPosition();
        nbt.putInt("spx", p.getX());
        nbt.putInt("spy", p.getY());
        nbt.putInt("spz", p.getZ());
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction move) {
        move.accept(passenger, getX(), getY(), getZ());
    }

    @Override
    public boolean isControlledByLocalInstance() {
        return false;
    }

    private float computeYawFromBlock() {
        BlockPos p = this.seatPos != null ? this.seatPos : this.blockPosition();
        BlockState s = level().getBlockState(p);
        float yaw = getYRot();
        if (s.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            yaw = s.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot();
        } else if (s.hasProperty(BlockStateProperties.FACING)) {
            yaw = s.getValue(BlockStateProperties.FACING).toYRot();
        } else {
            for (Property<?> prop : s.getProperties()) {
                if (prop.getName().equals("facing") && prop instanceof DirectionProperty dir) {
                    yaw = s.getValue(dir).toYRot();
                    break;
                }
            }
        }
        if (s.hasProperty(BlockStateProperties.BED_PART) && s.getValue(BlockStateProperties.BED_PART).toString().equals("head")) {
            yaw += 180.0F;
        } else {
            for (Property<?> prop : s.getProperties()) {
                if (prop.getName().equals("part")) {
                    if (s.getValue(prop).toString().equals("head")) {
                        yaw += 180.0F;
                        break;
                    }
                }
            }
        }
        return yaw;
    }

    @Override
    public void onPassengerTurned(Entity passenger) {
        if (passenger instanceof LivingEntity l) {
            float yaw = getYRot();
            l.setYBodyRot(yaw);
            l.yBodyRotO = yaw;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!seatPosInit) {
            this.seatPos = this.blockPosition();
            this.seatPosInit = true;
        }
        float target = computeYawFromBlock();
        if (getYRot() != target) {
            setYRot(target);
            yRotO = target;
        }
        for (Entity e : getPassengers()) {
            if (e instanceof Player p) {
                if (!level().isClientSide) p.setDeltaMovement(Vec3.ZERO);
                float yaw = getYRot();
                p.setYBodyRot(yaw);
                p.yBodyRotO = yaw;
            }
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity server) {
        return new ClientboundAddEntityPacket(this, server);
    }
}