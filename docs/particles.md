# Particles (`net.satisfy.foundation.particle`, `registry.FoundationParticles`)

Registered and rendered by Foundation – just spawn them.

| Constant | Id | Type | Looks like |
|---|---|---|---|
| `SOUP_BUBBLE` | `foundation:soup_bubble` | simple | bubble popping on a surface |
| `SOUP_COOKING_BUBBLE` | `foundation:soup_cooking_bubble` | simple | bubble rising in a pot |
| `SOUP_STEAM` | `foundation:soup_steam` | simple | slow rising steam |
| `WATER_DRIP` | `foundation:water_drip` | simple | drop falling from a tap |
| `WATER_SPLASH` | `foundation:water_splash` | simple | small splash |
| `DYE_SPLASH` | `foundation:dye_splash` | colour | coloured splash (dyeing, oil …) |
| `FEATHER` | `foundation:feather` | colour | feather drifting down |
| `FIREFLY` | `foundation:firefly` | simple | glowing, pulsing firefly |

```java
// simple
level.addParticle(FoundationParticles.SOUP_STEAM.get(), x, y, z, 0.0, 0.07, 0.0);

// coloured
ColorParticleOption red = ColorParticleOption.create(FoundationParticles.DYE_SPLASH.get(), FastColor.ARGB32.opaque(0xB02E26));
serverLevel.sendParticles(red, x, y, z, 12, 0.2, 0.1, 0.2, 0.05);
```

Want a new generic particle? Add it to `FoundationParticles`, register its provider in `FoundationClient`
**and** in `FoundationClientNeoForge`, and put the JSON + textures under `assets/foundation/`.
