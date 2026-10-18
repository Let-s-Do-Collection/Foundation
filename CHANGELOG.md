[1.0.8]

**Added**
* Rarity tooltip frames: items with a Foundation rarity get a matching tooltip frame, other mods can add frames for their own rarities as `<namespace>:tooltip/<rarity>` gui sprites

**Changed**
* Common, uncommon, rare, epic and legendary name colors now match their tooltip frames

**Fixed**
* `StorageBlockEntity` and `WardrobeBlockEntity` now expose their inventory as a vanilla container, so hoppers and other mods can access it

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
