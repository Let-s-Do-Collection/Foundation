# Particles (`net.satisfy.foundation.particle`, `registry.FoundationParticles`)

Registered and rendered by Foundation – just spawn them.

| Constant | Id | Type | Looks like |
|---|---|---|---|
| `SOUP_BUBBLE` | `foundation:soup_bubble` | simple | bubble popping on a surface |
| `SOUP_COOKING_BUBBLE` | `foundation:soup_cooking_bubble` | simple | bubble rising in a pot |
| `SOUP_STEAM` | `foundation:soup_steam` | simple | slow rising steam |
| `WATER_DRIP` | `foundation:water_drip` | simple | drop falling from a tap |
| `WATER_SPLASH` | `foundation:water_splash` | simple | small splash |
| `COLORED_DRIP` | `foundation:colored_drip` | colour | drop in any colour, hangs, falls and lands (juice from a barrel tap …) |
| `DYE_SPLASH` | `foundation:dye_splash` | colour | coloured splash (dyeing, oil …) |
| `FEATHER` | `foundation:feather` | colour | feather drifting down |
| `COLORED_STEAM` | `foundation:colored_steam` | colour | steam in any colour (black smoke from a dying fire …) |
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

## Drifting leaves and fluff

`DriftingParticle` is for particles you bring yourself (own type + textures) that should float out of a block: leaves from hedges or baskets, wool fluff from sheep.

```java
// Architectury
ParticleProviderRegistry.register(MyParticles.HEDGE_LEAF, sprites -> new DriftingParticle.Provider(sprites, DriftingParticle.Style.LEAF, DriftingParticle.MotionProfile.LEAF, MyConfig::leafParticles));
// NeoForge
event.registerSpriteSet(MyParticles.HEDGE_LEAF.get(), sprites -> new DriftingParticle.Provider(sprites, DriftingParticle.Style.LEAF, DriftingParticle.MotionProfile.LEAF));
```

| Style | Looks like |
|---|---|
| `LEAF` | tinted with the biome foliage color, spins, stops on the ground |
| `FLUFF` | whitish, rocks gently, slides a bit on the ground, translucent |

Profiles: `MotionProfile.LEAF`, `LEAF_RISING` (tossed higher), `FLUFF` – or build your own.
The optional `BooleanSupplier` is checked per spawn, handy for a config toggle.
