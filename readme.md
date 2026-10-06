# [Let's Do] Foundation

The shared library behind the Let's Do mods (Minecraft 1.21.1, Fabric & NeoForge via Architectury).

Foundation holds the things every Let's Do mod needs but no single mod should own: HUD panels, tooltips, seats, storage
shelves, food with effects, particles, banners and a handful of helpers. Players never install it by hand – every mod
ships it inside its own jar (jar-in-jar), and the loader always picks the newest copy.

---

## What's inside

| Area | Package | What it gives you | Guide |
|---|---|---|---|
| HUD panels | `overlay` | "Look at a block, see what to do" panels | [overlay.md](docs/overlay.md) |
| Creative tabs | `client.creative` | Categories as side tabs on the left of your creative tab | [creative_tabs.md](docs/creative_tabs.md) |
| Armor | `armor`, `client.armor` | Textured and dyeable armor items, custom 3D armor models, armor sets with set tooltips | [armor.md](docs/armor.md) |
| Model templates | `assets/foundation/models` | Shared parent models for cabinets, drawers, tables and wall decorations | [models.md](docs/models.md) |
| Tooltips | `tooltip` | "Can be placed" + `[SHIFT]` details, coloured tooltip borders | [tooltips.md](docs/tooltips.md) |
| Particles | `particle`, `registry` | Soup, steam, water, dye splash, feather, firefly, drifting leaves/fluff | [particles.md](docs/particles.md) |
| Ambience | `ambient` | Fireflies around lanterns at night | [ambient.md](docs/ambient.md) |
| Seats | `seat`, `block` | Chairs, benches, anything you can sit on | [seating.md](docs/seating.md) |
| Storage | `storage` | Shelves / racks that show their items, ready-made wall shelf | [storage.md](docs/storage.md) |
| Food | `food` | Food & drinks with effects, placeable dishes, ingredient effects | [food.md](docs/food.md) |
| Blocks | `block` | Facing, line-connecting, stackable, cake, berry bush, sink, wardrobe, dresser, window, shutter … | [blocks.md](docs/blocks.md) |
| Woods | `wood`, `client.wood` | Signs, hanging signs, boats and chest boats for custom wood types | [wood.md](docs/wood.md) |
| Menus | `menu` | Filtered slots, result slots with XP | [menus.md](docs/menus.md) |
| Mob AI | `entity.ai` | Attack goal that waits for the animation, random idle actions | [entity_ai.md](docs/entity_ai.md) |
| Banner | `banner` | The Completionist Banner every mod gets | [banner.md](docs/banner.md) |
| Recipes | `recipe`, `compat` | Recipe unlock book, REI/JEI layout helpers | [recipes.md](docs/recipes.md) |
| Utilities | `util`, `render`, `text`, `advancement` | Registration, shapes, dyeing, item rendering, triggers … | [utilities.md](docs/utilities.md) |

Coming from Farm & Charm's old helpers? See the [migration table](docs/migration.md).

---

## Add Foundation to a mod

**1. Publish Foundation locally** (once per Foundation change):

```bash
./gradlew publishToMavenLocal
```

**2. `gradle.properties`**

```properties
foundation_version=1.0.1
```

**3. Root `build.gradle`** – inside `allprojects { repositories { … } }`:

```groovy
mavenLocal()
```

**4. Dependencies** – `include(...)` embeds Foundation into your jar:

```groovy
// common/build.gradle
modCompileOnlyApi("net.satisfy.foundation:foundation-common:${rootProject.foundation_version}") { transitive = false }

// fabric/build.gradle
include(modImplementation("net.satisfy.foundation:foundation-fabric:${rootProject.foundation_version}") { transitive = false })

// neoforge/build.gradle
include(modImplementation("net.satisfy.foundation:foundation-neoforge:${rootProject.foundation_version}") { transitive = false })
```

**5. Declare the dependency**

```json
// fabric.mod.json → "depends"
"foundation": ">=1.0.0"
```

```toml
# neoforge.mods.toml
[[dependencies.your_mod_id]]
modId = "foundation"
type = "required"
versionRange = "[1.0.0,)"
ordering = "AFTER"
side = "BOTH"
```

That's it. Foundation initialises itself – you only register *your* content with it.

---

## Quick start

```java
// client init of your mod
BlockInfoOverlay.registerProvider(new MyInfoProvider());          // HUD panel for your blocks
InfoTooltip.of(MY_SHELF.get()).placeable().details(2).register(); // tooltip with [SHIFT] details
FireflyAmbience.init(MyConfig::fireflies);                        // fireflies, switchable by your config
```

```java
// a chair
public class StoolBlock extends ChairBlock {
    public StoolBlock(Properties properties) {
        super(properties);
    }
}
```

---

## Rules of thumb

Something belongs in Foundation when **all** of these are true:

1. At least two Let's Do mods would use it unchanged.
2. It has no content of its own – no items, blocks or recipes that players see.
3. It does not read any mod's config. Mods pass switches in (see `FireflyAmbience.init`, `InfoTooltip.when`).

Everything else stays in the mod. When in doubt, keep it in the mod and move it later.

## Project layout

- `common` – all shared code and resources
- `fabric` / `neoforge` – loader entrypoints only

## Versioning

Foundation follows semantic versioning. Mods declare a minimum version; because every mod embeds its own copy,
the loader picks the newest one. Never remove or rename public API in a minor version.
