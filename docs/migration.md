# Migrating from Farm & Charm helpers

Most of Foundation used to live in Farm & Charm. Replace imports like this:

| Old (Farm & Charm) | New (Foundation) |
|---|---|
| `farm_and_charm.lib.<area>.X` | `foundation.<area>.X` |
| `core.block.FacingBlock`, `LineConnectingBlock`, `ChairBlock`, `BenchBlock`, `StackableBlock`, `StackableEatableBlock`, `SliceableCakeBlock`, `BerryBushBlock`, `SinkBlock`, `EatableBoxBlock`, `Bonemealable…` | `foundation.block.*` |
| `core.block.StorageBlock`, `core.block.entity.StorageBlockEntity` | `foundation.storage.*` |
| `core.entity.ChairEntity` | `foundation.seat.ChairEntity` |
| `core.world.ImplementedInventory` | `foundation.util.ImplementedInventory` |
| `client.util.ClientUtil` | `foundation.render.ClientUtil` |
| `core.item.food.EffectItem` | `foundation.food.EffectFoodItem` |
| `core.item.food.EffectJugItem` | `foundation.food.EffectDrinkItem` |
| `core.item.food.EffectBlockItem` | `foundation.food.PlaceableEffectFoodItem` |
| `core.item.food.EffectFoodItem` | `foundation.food.IngredientEffectFoodItem` |
| `core.item.food.EffectFoodBlockItem` | `foundation.food.PlaceableIngredientEffectFoodItem` |
| `core.item.food.EffectFood` | `foundation.food.IngredientEffectCarrier` |
| `core.item.food.EffectFoodHelper` | `foundation.food.IngredientEffects` |
| `core.block.EffectFoodBlock` (+ entity) | `foundation.food.IngredientEffectFoodBlock` (+ entity) |
| `core.registry.ParticleTypeRegistry.X` | `foundation.registry.FoundationParticles.X` |

## `GeneralUtil`

| Old | New |
|---|---|
| `registerWithItem`, `registerWithoutItem`, `registerItem` | `RegistryUtil.*` |
| `rotateShape`, `isFaceFull`, `isSolid`, `isFullAndSolid`, `getRelativeHitCoordinatesForBlockFace` | `ShapeUtil.*` |
| `onUse`, `isOccupied`, `isPlayerSitting`, `onStateReplaced`, `add/get/removeChairEntity` | `SeatUtil.*` |
| `tracking`, buckets, `popResourceFromFace`, block-pos NBT, damage checks | `LibUtil.*` |
| `LineConnectingType`, `LINE_CONNECTING_TYPE` | `LineConnectingType`, `LineConnectingBlock.TYPE` |
| `spawnSlice(...)` | removed → vanilla `Block.popResourceFromFace(level, pos, direction, stack)` |

## Behaviour changes

- `StorageBlock` needs `blockEntityType()`; `StorageBlockEntity` takes the type in its constructor.
- `IngredientEffectFoodBlock` is abstract – implement `newBlockEntity` and `codec()`.
- `FireflyAmbience.init(BooleanSupplier)` takes your config switch; the tag is `foundation:attracts_fireflies`.
- Cake cutters tag: `foundation:cake_cutters`.
- `BannerSettings` takes the banner effect (and optional radius, default 8).
- Mod copies of `StorageBlock`, `StorageBlockEntity`, `StorageBlockEntityRenderer` and `StorageTypeRenderer` are replaced by `foundation.storage.*`; override `getAddSound` / `getRemoveSound` for custom sounds.
- Generic lang keys: `tooltip.foundation.canbeplaced`, `tooltip.foundation.hold_shift`.
