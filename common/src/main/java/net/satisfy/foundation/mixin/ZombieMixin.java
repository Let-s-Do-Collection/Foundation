package net.satisfy.foundation.mixin;

import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.ServerLevelAccessor;
import net.satisfy.foundation.entity.equipment.ZombieEquipment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Zombie.class)
public abstract class ZombieMixin {
    @Inject(method = "finalizeSpawn", at = @At("RETURN"))
    private void foundation$equip(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, SpawnGroupData groupData, CallbackInfoReturnable<SpawnGroupData> cir) {
        ZombieEquipment.equip((Zombie) (Object) this, level.getRandom());
    }
}
