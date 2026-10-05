package net.satisfy.foundation.fabric.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.satisfy.foundation.client.armor.ArmorModels;

public final class FoundationArmorRenderer implements ArmorRenderer {
    public static final FoundationArmorRenderer INSTANCE = new FoundationArmorRenderer();

    private FoundationArmorRenderer() {
    }

    @Override
    public void render(PoseStack matrices, MultiBufferSource vertexConsumers, ItemStack stack, LivingEntity entity, EquipmentSlot slot, int light, HumanoidModel<LivingEntity> contextModel) {
        ArmorModels.render(matrices, vertexConsumers, stack, slot, light, contextModel);
    }
}
