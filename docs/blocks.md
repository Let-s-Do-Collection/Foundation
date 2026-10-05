# Blocks (`net.satisfy.foundation.block`)

| Class | What it does |
|---|---|
| `FacingBlock` | horizontal `FACING`, rotates/mirrors correctly |
| `LineConnectingBlock` | connects to equal neighbours: `TYPE` = `NONE` / `LEFT` / `MIDDLE` / `RIGHT` (`LineConnectingType`) – tables, counters, troughs |
| `ChairBlock`, `BenchBlock` | sittable, see [seating.md](seating.md) |
| `StackableBlock(props, maxStack)` | placing the same item again stacks it (plates, crates) |
| `StackableEatableBlock` | stackable and eatable |
| `EatableBoxBlock` | box eaten in 6 bites (1 hunger each), gives a chest back – chocolate boxes, bread baskets |
| `SliceableCakeBlock(props, slice)` | cut slices with anything in `#foundation:cake_cutters` |
| `BerryBushBlock(props, berry[, blossom])` | bush that grows and drops berries, bonemealable |
| `BonemealableFlowerBlock`, `BonemealableTallFlowerBlock` | flowers that spread when bonemealed |
| `SinkBlock` | tap + basin with water drip particles |

## Cake cutters

Add your knives to `data/foundation/tags/item/cake_cutters.json`:

```json
{
  "replace": false,
  "values": [{ "id": "#mymod:knives", "required": false }]
}
```

## Cabinet

```java
public static final RegistrySupplier<Block> OAK_CABINET = registerWithItem("oak_cabinet",
        () -> new CabinetBlock(Properties.ofFullCopy(Blocks.OAK_PLANKS), MyBlockEntities.CABINET, MySounds.CABINET_OPEN.get(), MySounds.CABINET_CLOSE.get()));

BlockEntityType.Builder.of((pos, state) -> new CabinetBlockEntity(MyBlockEntities.CABINET.get(), pos, state), OAK_CABINET.get()).build(null);
```

Keep your own block entity type so cabinets in existing worlds keep their items. Sounds are optional.

## Wall decoration

```java
public static final RegistrySupplier<Block> HEART = registerWithItem("heart",
        () -> new WallDecorationBlock(Properties.ofFullCopy(Blocks.FLOWER_POT).noCollission(), MyBlockEntities.WALL_DECORATION, MyMod.identifier("textures/item/heart.png"), 12));

BlockEntityType.Builder.of((pos, state) -> new WallDecorationBlockEntity(MyBlockEntities.WALL_DECORATION.get(), pos, state), HEART.get()).build(null);
BlockEntityRendererRegistry.register(MyBlockEntities.WALL_DECORATION.get(), WallDecorationRenderer::new);
```

The texture is shown in the edit screen, the number is the max text length. Glow ink makes the text glow.

## Lamp

```java
public static final RegistrySupplier<Block> LAMP = registerWithItem("lamp",
        () -> new LampBlock(Properties.ofFullCopy(Blocks.LANTERN).lightLevel(LampBlock.lightWhenOn(15)), STANDING_SHAPE, HANGING_SHAPE));
```

Right-click turns it on and off (`luminance` block state).

## Table

```java
public static final RegistrySupplier<Block> OAK_TABLE = registerWithItem("oak_table",
        () -> new TableBlock(Properties.ofFullCopy(Blocks.OAK_PLANKS)));
```

Connects with tables next to it and can be waterlogged. The default hitbox is a table top with legs at the open ends; override `getTableShape(LineConnectingType type, Direction facing)` for your own model. `SofaBlock` works the same way with `getSofaShape`.

## Big table

```java
public static final RegistrySupplier<Block> OAK_BIG_TABLE = registerWithItem("oak_big_table",
        () -> new BigTableBlock(Properties.ofFullCopy(Blocks.OAK_PLANKS)));
```

Two blocks long, placed like a bed (`part` = head and foot). Use `foundation:block/template_big_table` and `template_big_table_adv` as models.

### Tablecloth

Every `TableBlock` takes a tablecloth: right-click with a carpet, dye to recolor, sneak and right-click with an empty hand to take it off. The color is the `tablecloth` block state, Foundation tints it.
Tables that always have a cloth (like the Brewery table): `new TableBlock(properties, ClothColor.WHITE)`. The cloth can then only be dyed, not taken off.
Add the cloth to your table blockstate as multipart with `foundation:block/template_tablecloth_single`, `_end` and `_middle` (use the end model with `y` + 180 for the right end).

## Sofa colors

`SofaBlock` has a `color` block state (default `none`, the original texture), right-click with a dye to recolor. Give the fabric faces of your sofa models `"tintindex": 0`, Foundation tints them.

## Wardrobe

```java
public static final RegistrySupplier<Block> WARDROBE = registerWithItem("wardrobe",
        () -> new WardrobeBlock(Properties.ofFullCopy(Blocks.OAK_PLANKS).noOcclusion(), MyBlockEntities.WARDROBE));

BlockEntityType.Builder.of((pos, state) -> new WardrobeBlockEntity(MyBlockEntities.WARDROBE.get(), pos, state), WARDROBE.get()).build(null);
BlockEntityRendererRegistry.register(MyBlockEntities.WARDROBE.get(), WardrobeRenderer::new);
```

Two blocks tall. Right-click with armor hangs it up (one piece per slot), sneak + right-click takes a piece back, right-click opens the doors.
Breaking either half drops the stored armor.

## Dresser

```java
public static final RegistrySupplier<Block> OAK_DRESSER = registerWithItem("oak_dresser",
        () -> new DresserBlock(Properties.ofFullCopy(Blocks.OAK_PLANKS), MyBlockEntities.CABINET, MySounds.DRAWER_OPEN, MySounds.DRAWER_CLOSE));
```

36 slots (`CabinetBlockEntity`), connects left/right like a `LineConnectingBlock`, waterloggable.
Override `connectsTo(BlockState)` to join other furniture (desks, tables), `getShape` for your own hitbox.
Need an `open` state for the model? Subclass, add `BlockStateProperties.OPEN` in `createBlockStateDefinition` – the cabinet block entity sets it while someone looks inside.

`CabinetWallBlock` is a `CabinetBlock` hanging on the wall (12 px deep).

## Window

```java
public static final RegistrySupplier<Block> OAK_WINDOW = registerWithItem("oak_window",
        () -> new WindowBlock(Properties.ofFullCopy(Blocks.GLASS_PANE)));
```

Pane that connects sideways like glass panes and knows its place in a stack: `part` = 0 single, 1 bottom, 2 middle, 3 top.

## Shutter

```java
public static final RegistrySupplier<Block> OAK_SHUTTER = registerWithItem("oak_shutter",
        () -> new ShutterBlock(Properties.ofFullCopy(Blocks.OAK_TRAPDOOR)));
```

Hinge left or right depending on where you click. Opens by hand or redstone; stacked shutters (`type` = `top`/`middle`/`bottom`) open together, sneak to open just one.
Override `getSound(boolean open)` for other sounds.
