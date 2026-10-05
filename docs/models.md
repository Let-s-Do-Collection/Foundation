# Model templates (`assets/foundation/models/block`)

Shared block models. Use them as parent and only set the textures:

```json
{
  "parent": "foundation:block/template_cabinet",
  "textures": {
    "front": "mymod:block/oak_cabinet_front",
    "side": "mymod:block/oak_cabinet_side",
    "top": "mymod:block/oak_cabinet_top"
  }
}
```

| Template | Textures | For |
|---|---|---|
| `template_cabinet` | `front`, `side`, `top` | Cabinet closed and open, drawer closed (only the front differs) |
| `template_drawer_open` | `front`, `side`, `inside`, `top` | Open drawer |
| `template_table` | `top`, `side` | Table end piece |
| `template_table_middle` | `top`, `side` | Table middle piece |
| `template_table_connected_support` | `top`, `side` | Table piece with support |
| `template_big_table` | `table` | Big table |
| `template_big_table_adv` | `table` | Big table, second variant |
| `template_wall_decoration` | `decoration` | Wall decoration (heart, gingerbread) |
| `template_stove` | `front`, `side`, `top`, `bottom` | Stove (off) |
| `template_counter` | `side`, `top`, `bottom` | Kitchen counter |
| `template_kitchen_sink` | `side`, `bottom`, `top` | Kitchen sink, water and tap are drawn by `SinkBlock` |
