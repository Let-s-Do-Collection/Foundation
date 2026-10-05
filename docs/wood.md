# Custom woods (`net.satisfy.foundation.wood`, `client.wood`)

Signs, hanging signs, boats and chest boats for your own wood types – no copied vanilla renderers needed.

## 1. Wood type

```java
public static final WoodType PALM = WoodType.register(new WoodType(MyMod.identifier("palm").toString(), BlockSetType.OAK));
```

Textures: `assets/<ns>/textures/entity/signs/palm.png` and `entity/signs/hanging/palm.png`.

## 2. Signs

```java
// blocks
PALM_SIGN = registerWithoutItem("palm_sign", () -> new WoodStandingSignBlock(PALM, Properties.ofFullCopy(Blocks.OAK_SIGN), MyBlockEntities.SIGN));
PALM_WALL_SIGN = registerWithoutItem("palm_wall_sign", () -> new WoodWallSignBlock(PALM, Properties.ofFullCopy(Blocks.OAK_WALL_SIGN), MyBlockEntities.SIGN));
PALM_HANGING_SIGN = registerWithoutItem("palm_hanging_sign", () -> new WoodCeilingHangingSignBlock(PALM, Properties.ofFullCopy(Blocks.OAK_HANGING_SIGN), MyBlockEntities.HANGING_SIGN));
PALM_WALL_HANGING_SIGN = registerWithoutItem("palm_wall_hanging_sign", () -> new WoodWallHangingSignBlock(PALM, Properties.ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN), MyBlockEntities.HANGING_SIGN));
// items: vanilla SignItem / HangingSignItem

// block entity types – keep your existing ids so placed signs survive
SIGN = BLOCK_ENTITIES.register("sign", () -> BlockEntityType.Builder.of(
        (pos, state) -> new WoodSignBlockEntity(SIGN.get(), pos, state), PALM_SIGN.get(), PALM_WALL_SIGN.get()).build(null));
HANGING_SIGN = BLOCK_ENTITIES.register("hanging_sign", () -> BlockEntityType.Builder.of(
        (pos, state) -> new WoodHangingSignBlockEntity(HANGING_SIGN.get(), pos, state), PALM_HANGING_SIGN.get(), PALM_WALL_HANGING_SIGN.get()).build(null));
```

Client:

```java
WoodClient.registerSignMaterials(MyWoodTypes.PALM);
WoodClient.registerSignRenderers(MyBlockEntities.SIGN.get(), MyBlockEntities.HANGING_SIGN.get());
```

`WoodHangingSignBlockEntity` is a real vanilla hanging sign, so players get the hanging sign edit screen and text layout.

## 3. Boats

One entity type per mod (plus one for chest boats) carries all your woods:

```java
BOAT = ENTITY_TYPES.register("boat", () -> EntityType.Builder.<WoodBoat>of(WoodBoat::new, MobCategory.MISC).sized(1.375F, 0.5625F).clientTrackingRange(10).build("boat"));
CHEST_BOAT = ENTITY_TYPES.register("chest_boat", () -> EntityType.Builder.<WoodBoat>of(WoodChestBoat::new, MobCategory.MISC).sized(1.375F, 0.5625F).clientTrackingRange(10).build("chest_boat"));

PALM_BOAT_ITEM = registerItem("palm_boat", () -> new WoodBoatItem(MyEntities.BOAT, MyMod.identifier("palm"), new Item.Properties().stacksTo(1)));
PALM_CHEST_BOAT_ITEM = registerItem("palm_chest_boat", () -> new WoodBoatItem(MyEntities.CHEST_BOAT, MyMod.identifier("palm"), new Item.Properties().stacksTo(1)));

public static final BoatWood PALM_BOAT = BoatWood.register(MyMod.identifier("palm"), PALM_BOAT_ITEM, PALM_CHEST_BOAT_ITEM);
```

Textures: `textures/entity/boat/palm.png` and `textures/entity/chest_boat/palm.png`.

Client:

```java
WoodClient.registerBoatLayers(MyWoods.PALM_BOAT);               // early, with the other model layers
EntityRendererRegistry.register(MyEntities.BOAT, context -> new WoodBoatRenderer(context, false));
EntityRendererRegistry.register(MyEntities.CHEST_BOAT, context -> new WoodBoatRenderer(context, true));
```

Boats store their wood as `Type` (full id). Old saves with just the path (`"palm"`) are read with the namespace of the entity type.
