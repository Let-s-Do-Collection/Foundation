package net.satisfy.foundation.neoforge.core.mixin;

import net.neoforged.neoforge.client.model.data.ModelData;
import net.satisfy.foundation.block.mimic.MimicBlockEntity;
import net.satisfy.foundation.neoforge.client.mimic.NeoForgeMimicModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MimicBlockEntity.class)
public abstract class MimicBlockEntityMixin {
    public ModelData getModelData() {
        MimicBlockEntity entity = (MimicBlockEntity) (Object) this;
        return entity.hasMimic() ? ModelData.builder().with(NeoForgeMimicModel.MIMIC, entity.getMimicState()).build() : ModelData.EMPTY;
    }

    @Inject(method = "refreshClient", at = @At("HEAD"))
    private void foundation$requestModelDataUpdate(CallbackInfo ci) {
        MimicBlockEntity entity = (MimicBlockEntity) (Object) this;
        if (entity.getLevel() != null && entity.getLevel().isClientSide) {
            entity.requestModelDataUpdate();
        }
    }
}
