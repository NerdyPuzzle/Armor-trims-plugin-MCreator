<#include "../transformer.ftl">
{
  "asset_name": ${mappedMCItemToName(data.material)},
  "description": {
    "color": "${data.color}",
    "translate": ${mappedMCItemToName(data.material)}
  },
  "ingredient": ${mappedMCItemToRegistryName(data.material)},
  "item_model_index": 0.0
}