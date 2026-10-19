[1.0.12]

**Added**
* Creative tab tooltips: mods register their tab with `CreativeTabTooltips.register(tab, rarity)` and get a rarity frame with the tab icon and name plus their `@modid`. Side tabs of that tab get the same frame with their icon and name

***
[1.0.11]

**Added**
* Tooltip frames for Relic (black) and Endless (red) items

**Changed**
* Relic names now shine in silver and Endless names cycle through shades of red, matching their new frames
* "Can be placed" tooltips are no longer italic

***
[1.0.10]

**Added**
* Attribute icons: armor, armor toughness, knockback resistance, attack damage, attack speed, max health and speed lines get an icon in front of their value, with a config switch. With Quark installed, Foundation's icons replace Quark's attribute tooltips

**Fixed**
* Placeable food and drink items not showing the "Can be placed" tooltip

***
[1.0.9]

**Added**
* Food blocks spawn eating particles of their item when a bite is taken

**Fixed**
* Game crash when the first tooltip is rendered, caused by a tooltip frame injection that does not exist in this Minecraft version
* Rarity tooltip frames not showing on NeoForge
* Possible crash when a food block without an item is eaten
* Possible crash when the Grandmother's Recipe Book tooltip is shown while no world is loaded

***
[1.0.8]

**Added**
* Rarity tooltip frames: items with a Foundation rarity get a matching tooltip frame, other mods can add frames for their own rarities as `<namespace>:tooltip/<rarity>` gui sprites
* Items with a rarity frame show their icon left of the name in a taller title bar
* Rarity tooltips use compact gaps instead of empty lines and drop the duplicated mod name line
* Foundation config (Cloth Config on Fabric, NeoForge config screen) with switches for the improved item tooltips, rarity name colors, item info, armor set info and block notices
* Config screen with the mod logo, a Configure button, the mod version and links to Website, GitHub, Discord, Ko-fi and Patreon. Mods open it with `LetsDoConfigScreen` and ship their logo as `textures/gui/logo.png`

**Changed**
* Common, uncommon, rare, epic and legendary name colors now match their tooltip frames
* Celestial, divine and eternal are now called Relic, Artifact and Endless
* `InfoTooltip` lines wrap at 200 pixels and skip the empty line right below the item name

**Fixed**
* `StorageBlockEntity` and `WardrobeBlockEntity` now expose their inventory as a vanilla container, so hoppers and other mods can access it
* Seats: the player now sits at the correct height and faces the direction of the block

***

[1.0.7]

**Fixed**
* Long model loading and "Unable to load model" warnings for other mods' models with ModernFix dynamic resources: mimic models are now wrapped only for mimic blocks instead of scanning every model
* Chair seat entities being saved with the world and left behind after reloading

***

[1.0.6]

**Added**
* Fly particle (`FoundationParticles.FLY`): a fly buzzing around its spawn point, a positive x speed sets the base lifetime in ticks
* Needle particle (`FoundationParticles.NEEDLE`): falling conifer needles tinted with the evergreen foliage color

***

[1.0.5]

**Added**
* Mimic blocks (`MimicBlockEntity`, `MimicBlock`, `MimicModels`): blocks that take on the texture of an applied block or render a held block, with shared saving, syncing, drops and models on Fabric and NeoForge
* Compostables (`FoundationCompostables`): register compostable items once with presets or custom chances, works on Fabric and NeoForge (NeoForge ignores the vanilla compostables map)
* Villager trades (`SellItemFactory`, `BuyForOneEmeraldFactory`): shared trade listings for selling and buying items for emeralds
* Zombie equipment (`ZombieEquipment`): give spawning zombies items with a chance, optionally adults only, without a mixin in your mod
* Placeable drinks (`PlaceableDrinkItem`): drink items that can be placed as blocks, with optional crouch to place, drinking at any time, a returned container and an effect tooltip

**Fixed**
* Effect tooltips of `EffectDrinkItem`, `EffectFoodItem` and `PlaceableEffectFoodItem` showing shorter durations for effects with a chance below 100%

***

[1.0.4]

**Fixed**
* Crash when a block info panel is larger than the available screen space

***

[1.0.3]

**Added**
* Flammable blocks (`FoundationFlammables`): register blocks with wood, wool, hay or plant presets, or custom fire odds, applied automatically on common setup

***

[1.0.2]

**Added**
* Shared biome fog (`BiomeFog`): mods register fog sources, Foundation blends in the densest one so several mods no longer overwrite each other's fog
* Tooltips for ghost items in the recipe book, showing the item the slot currently displays

***

[1.0.1]

**Added**
* Built-in recipe book
* Custom rarities 

***

[1.0.0]

**Added**
* Initial setup

***
