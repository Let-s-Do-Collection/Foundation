# Ambience (`net.satisfy.foundation.ambient`)

## Fireflies around lanterns

Every block in the tag `foundation:attracts_fireflies` gets fireflies at night – outdoors, no rain, and only while lit
(if the block has a `lit` property).

**Turn it on** (client init). Pass your config switch – Foundation has none:

```java
FireflyAmbience.init(MyConfig::fireflies);
```

Several mods may call `init`; fireflies show when **all** passed switches are on.

**Add your blocks** – `data/foundation/tags/block/attracts_fireflies.json` in *your* mod:

```json
{
  "replace": false,
  "values": ["mymod:paper_lantern"]
}
```

Vanilla lanterns and `#c:lanterns` are already in.

## Fireflies from your own block

```java
@Override
public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
    if (random.nextInt(12) == 0 && MyConfig.fireflies() && Fireflies.isActive(level, pos)) {
        Fireflies.spawn(level, pos, random, 0.5, 0.6); // spread, height above the block
    }
}
```
