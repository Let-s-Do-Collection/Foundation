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
