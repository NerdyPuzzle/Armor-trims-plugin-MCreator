<#include "../transformer.ftl">
{
  "asset_id": "${modid}:${data.getModElement().getRegistryName()}",
  "description": {
    "translate": "${name}"
  },
  "template_item": ${mappedMCItemToRegistryName(data.item)}
}