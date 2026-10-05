# Armor (`net.satisfy.foundation.armor`)

## Items

```java
new TexturedArmorItem(material, ArmorItem.Type.HELMET, properties, MyMod.identifier("textures/models/armor/chef_hat.png"));
new DyeableArmorItem(material, ArmorItem.Type.CHESTPLATE, 0xFF8080, properties, MyMod.identifier("textures/models/armor/dress.png"));
```

`TexturedArmorItem` keeps the texture for your armor renderer (`getTexture()`), the slot comes from the type.
`DyeableArmorItem` takes a default color and an optional overlay texture, `getColor(stack)` reads dyed colors.
Tint the item icons on the client with `ArmorColors.register(DRESS.get(), SUIT.get())`.

## Sets

```java
ArmorSet.builder("tooltip.mymod.set.chef")
        .piece(ObjectRegistry.CHEF_HAT)
        .piece(ObjectRegistry.SHIRT, ObjectRegistry.FORMAL_SHIRT)
        .bonus("tooltip.mymod.set.chef.bonus")
        .tooltipVisible(MyConfig::showSetTooltips)
        .register();
```

Every piece can have alternatives. All set items get the classic set tooltip: the set name with the worn pieces, each piece green when worn and grey when missing, and the bonus lighting up once the set is complete.
Without `bonus` only the pieces are listed. `bonusActive` replaces the "all pieces worn" check, e.g. when two sets share one bonus.

`Wearing.isWearing(entity, item)` checks armor slots and every registered slot provider. Accessory mods hook in with `Wearing.registerSlotProvider((entity, item) -> ...)`, so pieces in a ring or hat slot count too.

## Custom 3D models

For armor with its own model (hats, dresses, boots …). The model is a `HumanoidModel` with your own layer; Foundation copies the wearer's pose onto it, so no `copyHead`/`copyBody` helpers are needed.

```java
// client
EntityModelLayerRegistry.register(ChefHatModel.LAYER_LOCATION, ChefHatModel::createBodyLayer);
ArmorModels.register(ChefHatModel.LAYER_LOCATION, ChefHatModel::new, ObjectRegistry.CHEF_HAT.get(), ObjectRegistry.COOKING_HAT.get());

// Fabric client
ArmorRenderer.register(FoundationArmorRenderer.INSTANCE, ObjectRegistry.CHEF_HAT.get(), ObjectRegistry.COOKING_HAT.get());

// NeoForge, RegisterClientExtensionsEvent
event.registerItem(FoundationArmorExtensions.INSTANCE, ObjectRegistry.CHEF_HAT.get(), ObjectRegistry.COOKING_HAT.get());
```

Works for `TexturedArmorItem` (texture from `getTexture()`) and `DyeableArmorItem` (dye color + overlay).
Setting `Visible: false` in the item's custom data hides the piece.
On NeoForge the texture of a registered `TexturedArmorItem` also comes from the item, not from the armor material.
