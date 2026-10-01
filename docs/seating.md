# Seats (`net.satisfy.foundation.seat`, `block.ChairBlock`, `block.BenchBlock`)

## Ready-made

- `ChairBlock` – a sittable chair with shape and placement.
- `BenchBlock` – a sittable bench that connects left/middle/right (see `LineConnectingBlock`).

```java
public static final RegistrySupplier<Block> STOOL = registerWithItem("stool", () -> new ChairBlock(Properties.ofFullCopy(Blocks.OAK_PLANKS)));
```

## Make any block sittable

```java
@Override
protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
    return SeatUtil.onUse(level, player, InteractionHand.MAIN_HAND, hit, 0.1).result(); // 0.1 = extra sitting height
}

@Override
protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
    SeatUtil.onStateReplaced(level, pos);   // stands the player up when the block goes away
    super.onRemove(state, level, pos, newState, moved);
}
```

Helpers: `SeatUtil.isOccupied(level, pos)`, `SeatUtil.isPlayerSitting(player)`.
The seat entity (`foundation:chair`) and its renderer are registered by Foundation.
