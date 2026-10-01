# Storage shelves (`net.satisfy.foundation.storage`)

Blocks that hold a few items and show them: tool racks, window sills, cake stands, shelves …
The player right-clicks a spot on the front face; that spot decides the slot.

## 1. The block

```java
public class SpiceRackBlock extends StorageBlock {
    public SpiceRackBlock(Properties properties) {
        super(properties);
    }

    @Override public BlockEntityType<?> blockEntityType() { return MyBlockEntities.STORAGE.get(); }
    @Override public int size() { return 3; }
    @Override public ResourceLocation type() { return MyMod.identifier("spice_rack"); }
    @Override public Direction[] unAllowedDirections() { return new Direction[]{Direction.UP, Direction.DOWN}; }
    @Override public boolean canInsertStack(ItemStack stack) { return stack.is(MyTags.SPICES); }

    @Override
    public int getSection(Float x, Float y) {   // x, y = 0..1 on the front face
        return x < 0.33F ? 0 : x < 0.66F ? 1 : 2;  // Integer.MIN_VALUE = "no slot here"
    }
}
```

## 2. One block entity type for all your storage blocks

```java
public static final RegistrySupplier<BlockEntityType<StorageBlockEntity>> STORAGE = BLOCK_ENTITIES.register("storage",
        () -> BlockEntityType.Builder.of((pos, state) -> new StorageBlockEntity(MyBlockEntities.STORAGE.get(), pos, state),
                SPICE_RACK.get(), CAKE_STAND.get()).build(null));
```

## 3. How it looks (client)

```java
BlockEntityRendererRegistry.register(MyBlockEntities.STORAGE.get(), context -> new StorageBlockEntityRenderer());
StorageBlockEntityRenderer.registerStorageType(MyMod.identifier("spice_rack"), (entity, poses, buffers, items) -> {
    // draw `items` – use DisplayItemRenderer.renderUpright / renderFlat
});
```

The id passed to `registerStorageType` must match `type()` of the block.
