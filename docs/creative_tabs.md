# Creative side tabs (`net.satisfy.foundation.client.creative`)

Split one creative tab into categories. They show up as tabs on the left side of the creative inventory while your tab is open, and only the items of the selected category are listed.

## 1. Fill the tab from one method per category

```java
public static final RegistrySupplier<CreativeModeTab> MY_TAB = TABS.register("mymod", () -> CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, 0)
        .icon(() -> new ItemStack(ObjectRegistry.COOKING_POT.get()))
        .title(Component.translatable("creativetab.mymod.tab"))
        .displayItems((parameters, output) -> {
            acceptFood(output);
            acceptFurniture(output);
        })
        .build());

public static void acceptFood(CreativeModeTab.Output output) {
    output.accept(ObjectRegistry.LASAGNE.get());
}

public static void acceptFurniture(CreativeModeTab.Output output) {
    output.accept(ObjectRegistry.OAK_CHAIR.get());
}
```

## 2. Register the side tabs on the client

```java
CreativeSideTabs.register(TabRegistry.MY_TAB.getKey(),
        CreativeSideTabs.SideTab.of(Component.translatable("creativetab.mymod.side.food"), ObjectRegistry.LASAGNE.get(), TabRegistry::acceptFood),
        CreativeSideTabs.SideTab.of(Component.translatable("creativetab.mymod.side.furniture"), ObjectRegistry.OAK_CHAIR.get(), TabRegistry::acceptFurniture));
```

Each side tab has a title (shown as tooltip), an icon and the same method that fills the tab, so the categories always match the tab contents.
The first side tab is selected by default; the last selected one is remembered per tab while the game runs.
