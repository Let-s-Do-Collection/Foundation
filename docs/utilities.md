# Utilities

## Registration – `util.RegistryUtil`

```java
RegistryUtil.registerWithItem(BLOCKS, BLOCK_REGISTRAR, ITEMS, ITEM_REGISTRAR, MyMod.identifier("oak_table"), () -> new TableBlock(props));
RegistryUtil.registerWithoutItem(BLOCKS, BLOCK_REGISTRAR, MyMod.identifier("wall_banner"), () -> new CompletionistWallBannerBlock(props, BANNER));
RegistryUtil.registerItem(ITEMS, ITEM_REGISTRAR, MyMod.identifier("knife"), () -> new SwordItem(...));
```

## Shapes – `util.ShapeUtil`

```java
VoxelShape east = ShapeUtil.rotateShape(Direction.NORTH, Direction.EAST, northShape);
ShapeUtil.isFaceFull(level, pos);   ShapeUtil.isSolid(level, pos);   ShapeUtil.isFullAndSolid(level, pos);
ShapeUtil.getRelativeHitCoordinatesForBlockFace(hit, facing, blockedFaces); // x/y 0..1 on the clicked face
```

## Everything else – `util.LibUtil`

`tracking(level, pos)` (players that see a chunk), `fillBucket` / `emptyBucket`, `popResourceFromFace`,
`putBlockPos` / `putBlockPoses` / `readBlockPoses` (NBT), `isFire(damageSource)`, `isDamageType(...)`,
`convertStackAfterFinishUsing(...)` (bowls/bottles after eating), `getInPercent`, `isIndexInRange`.

## Dyeing – `util.DyeHelper`

```java
// in useItemOn, with a DyeItem in hand – recolours the block, consumes the dye, plays sound + dye splash
return DyeHelper.dye(stack, dye.getDyeColor(), state, COLOR, level, pos, player);
```

## Items on blocks – `render.DisplayItemRenderer`

```java
DisplayItemRenderer.renderFlat(stack, poseStack, buffers, light, overlay, level, x, y, z, yaw, scale, seed);    // lying down
DisplayItemRenderer.renderUpright(stack, poseStack, buffers, light, overlay, level, x, y, z, yaw, scale, seed); // standing
```

## Also here

| Class | Use |
|---|---|
| `util.ImplementedInventory` | `Container` from a `NonNullList` in two lines |
| `util.StreamCodecUtil` | stream codec helpers |
| `render.ClientUtil` | light level at a position, GUI item rendering |
| `text.TextEditableBlockEntity` + `SetTextPacket` | blocks with editable text (signs, bowls): implement the interface, send `SetTextPacket.sendToServer(...)` |
| `advancement.SimplePlayerTrigger` | "player did X" advancement trigger: register one per event, call `trigger(player)` |
