# Completionist Banner (`net.satisfy.foundation.banner`)

The "you cooked everything" reward banner. Every mod brings its own texture; everything else is shared.

## Register

```java
private static final BannerSettings BANNER = new BannerSettings(
        () -> MyBlockEntities.BANNER.get(),            // block entity type
        () -> MyBlocks.MY_WALL_BANNER.get(),            // wall variant
        MyMod.identifier("textures/banner/my_banner.png"),
        "tooltip.mymod.banner",                         // tooltip key prefix
        MobEffects.REGENERATION);                       // effect for players nearby (radius 8)

public static final RegistrySupplier<Block> MY_BANNER = registerWithItem("my_banner",
        () -> new CompletionistBannerBlock(Properties.of().strength(1F).noCollission().sound(SoundType.WOOD), BANNER));
public static final RegistrySupplier<Block> MY_WALL_BANNER = registerWithoutItem("my_wall_banner",
        () -> new CompletionistWallBannerBlock(Properties.of().strength(1F).noCollission().sound(SoundType.WOOD), BANNER));

// block entity
BlockEntityType.Builder.of(CompletionistBannerEntity::new, MY_BANNER.get(), MY_WALL_BANNER.get()).build(null);
```

Different radius? Pass it as sixth argument: `new BannerSettings(..., MobEffects.WATER_BREATHING, 6)`.
The effect is refreshed every second for every player within the radius.

## Client

```java
BlockEntityRendererRegistry.register(MyBlockEntities.BANNER.get(), CompletionistBannerRenderer::new);
```

The model layer is registered by Foundation.

## Lang

```json
"tooltip.mymod.banner.thankyou_1": "A reward for cooking all dishes",
"tooltip.mymod.banner.thankyou_2": "When placed:",
"tooltip.mymod.banner.thankyou_3": "Thank you for playing My Mod!",
"tooltip.mymod.banner.thankyou_4": "Grants Regeneration in a radius of 8 blocks"
```

Values from a config? Pass suppliers for radius and amplifier: `new BannerSettings(..., MobEffects.DAMAGE_RESISTANCE, MyConfig::bannerRadius, MyConfig::bannerAmplifier)`.

A radius of 0 turns the effect off, handy for a config switch: `() -> MyConfig.bannerEffect ? MyConfig.bannerRadius : 0`.
