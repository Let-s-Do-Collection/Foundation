# Menu slots (`net.satisfy.foundation.menu`)

| Class | Use |
|---|---|
| `ExtendedSlot(container, index, x, y, filter)` | slot that only accepts items matching the filter |
| `OutputSlot(player, container, index, x, y)` | take-only result slot; fires `onCraftedBy` (advancements, stats) |
| `ExperienceSource` | implement it on your block entity, `OutputSlot` calls `dropExperience(level, pos)` when the result is taken |

```java
addSlot(new ExtendedSlot(container, 0, 30, 17, stack -> stack.is(MyTags.INGREDIENTS)));
addSlot(new OutputSlot(inventory.player, container, 3, 124, 35));
```
