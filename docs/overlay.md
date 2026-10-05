# HUD panels (`net.satisfy.foundation.overlay`)

Look at a block → a small panel tells the player what is inside and what to do next.
Foundation draws the panel (position, scaling, background). You only describe the content.

## 1. Describe your block

```java
public class ShelfInfoProvider implements BlockInfoProvider {
    @Override
    public List<InfoSection> describe(Level level, BlockPos pos, BlockState state, @Nullable BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof ShelfBlockEntity shelf)) {
            return List.of();                       // not my block → next provider
        }
        return List.of(
                InfoSection.icons(Component.translatable("hud.mymod.on_shelf"), shelf.getItems(), InfoSection.ROW_COLUMNS),
                InfoSection.title(Component.translatable("hud.mymod.take_hint").withStyle(ChatFormatting.GRAY))
        );
    }
}
```

## 2. Register it (client init)

```java
BlockInfoOverlay.registerProvider(new ShelfInfoProvider());
```

The first provider that returns a non-empty list wins.

## Building blocks: `InfoSection`

| Factory | Shows |
|---|---|
| `InfoSection.title(text)` | one line of text |
| `InfoSection.icons(title, items, columns)` | title + item grid (`ROW_COLUMNS` = 6, `GRID_COLUMNS` = 3) |
| `InfoSection.lines(title, lines)` | title + several text lines |
| `InfoSection.rows(title, rows)` | title + icon/text rows (`Row.item(...)`, `Row.sprite(...)`) |
| `InfoSection.image(title, texture, size)` | title + a picture (e.g. a "toss!" icon) |

Modifiers: `withLines(...)`, `withRows(...)`, `withPlaceholder(texture)`, `withDecorations()`.

## Extras

- **Panels for blocks you don't look at** – e.g. "your wok needs a toss":
  ```java
  BlockInfoOverlay.registerTracker(() -> myTrackedPositions);
  ```
  Your provider then gets `hit == null` for those positions.
- **Coloured border** – override `beforeBackground(...)` and call `TooltipBorder.markHot()` / `markSoul()` / `mark(top, bottom)`.
- **Short notice** – `BlockInfoOverlay.showNotice(pos, message)`.
- **Server data the client doesn't have** – register a `BlockInfoSync.Handler` on the server, read it on the client:
  ```java
  BlockInfoSync.registerHandler((level, pos, player, tag) -> tag.putInt("stock", myStock(level, pos)));
  CompoundTag data = BlockInfoSync.poll(level, pos); // in describe(...)
  ```

## Config switch

Foundation has no config. Return `List.of()` from `describe` when your mod's "block info" option is off.

## Notices from the server

`BlockInfoOverlay.showNotice(pos, text)` shows a short golden notice at a block, client side. From the server use `BlockNotice.send(serverPlayer, pos, text)`, or `send(serverPlayer, pos, title, lines)` for a title with more lines below.
