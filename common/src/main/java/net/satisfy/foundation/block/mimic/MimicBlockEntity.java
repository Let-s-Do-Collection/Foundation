package net.satisfy.foundation.block.mimic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MimicBlockEntity extends BlockEntity {
    private static final String TAG = "Mimic";
    private static final String LEGACY_TAG = "Held";

    private @Nullable BlockState mimicState;

    public MimicBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public @Nullable BlockState getMimicState() {
        return mimicState;
    }

    public boolean hasMimic() {
        return mimicState != null && !mimicState.isAir();
    }

    public void setMimicState(@Nullable BlockState state) {
        BlockState next = state == null || state.isAir() ? null : state;
        if (next == mimicState) {
            return;
        }
        mimicState = next;
        setChanged();
        if (level != null) {
            if (level.isClientSide) {
                refreshClient();
            } else {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    protected void refreshClient() {
        if (level != null && level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_IMMEDIATE);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        if (mimicState != null) {
            tag.put(TAG, NbtUtils.writeBlockState(mimicState));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        String key = tag.contains(TAG) ? TAG : tag.contains(LEGACY_TAG) ? LEGACY_TAG : null;
        BlockState state = key == null ? null : NbtUtils.readBlockState(provider.lookupOrThrow(Registries.BLOCK), tag.getCompound(key));
        mimicState = state == null || state.isAir() ? null : state;
        refreshClient();
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveWithoutMetadata(provider);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
