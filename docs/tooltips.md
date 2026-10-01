# Tooltips (`net.satisfy.foundation.tooltip`)

## Info tooltips

One line per item – no `appendHoverText` overrides needed.

```java
InfoTooltip.of(MY_SHELF.get())
        .placeable()                              // "Can be placed"
        .line("tooltip.mymod.fits_on_wall")       // any extra gray line
        .details(2)                               // "[SHIFT] for more" → 2 lines
        .when(MyConfig::itemTooltips)             // optional switch from your config
        .register();
```

`details(n)` reads the keys `tooltip.<modid>.<item_path>.info_0` … `info_{n-1}` from **your** lang file:

```json
"tooltip.mymod.shelf.info_0": "Right-click with an item to put it on the shelf.",
"tooltip.mymod.shelf.info_1": "Right-click with an empty hand to take it back."
```

The generic texts (`tooltip.foundation.canbeplaced`, `tooltip.foundation.hold_shift`) come from Foundation.

## Coloured tooltip borders

```java
TooltipBorder.markSoul();                 // cyan, e.g. soul-fire food
TooltipBorder.markHot();                  // orange-red, e.g. something hot
TooltipBorder.mark(0xF0FFD700, 0xF0886600); // any gradient (top, bottom), ARGB
```

Call it while the tooltip is being built (e.g. inside a `ClientTooltipEvent` listener or `appendHoverText`).
The mark applies to the next tooltip frame only, so nothing needs to be reset.
