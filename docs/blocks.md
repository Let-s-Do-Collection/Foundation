# Blocks (`net.satisfy.foundation.block`)

| Class | What it does |
|---|---|
| `FacingBlock` | horizontal `FACING`, rotates/mirrors correctly |
| `LineConnectingBlock` | connects to equal neighbours: `TYPE` = `NONE` / `LEFT` / `MIDDLE` / `RIGHT` (`LineConnectingType`) – tables, counters, troughs |
| `ChairBlock`, `BenchBlock` | sittable, see [seating.md](seating.md) |
| `StackableBlock(props, maxStack)` | placing the same item again stacks it (plates, crates) |
| `StackableEatableBlock` | stackable and eatable |
| `EatableBoxBlock` | box eaten in 6 bites (1 hunger each), gives a chest back – chocolate boxes, bread baskets |
| `SliceableCakeBlock(props, slice)` | cut slices with anything in `#foundation:cake_cutters` |
| `BerryBushBlock(props, berry[, blossom])` | bush that grows and drops berries, bonemealable |
| `BonemealableFlowerBlock`, `BonemealableTallFlowerBlock` | flowers that spread when bonemealed |
| `SinkBlock` | tap + basin with water drip particles |

## Cake cutters

Add your knives to `data/foundation/tags/item/cake_cutters.json`:

```json
{
  "replace": false,
  "values": [{ "id": "#mymod:knives", "required": false }]
}
```
