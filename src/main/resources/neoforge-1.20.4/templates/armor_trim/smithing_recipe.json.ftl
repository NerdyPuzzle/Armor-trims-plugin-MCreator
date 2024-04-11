<#include "../transformer.ftl">
{
  "type": "minecraft:smithing_trim",
  "addition": {
    <#if data.type == "Vanilla">
        "tag": "minecraft:trim_materials"
    <#else>
        "tag": "${modid}:${data.getModElement().getRegistryName()}_materials"
    </#if>
  },
  "base": {
    "tag": "minecraft:trimmable_armor"
  },
  "template": {
    "item": ${mappedMCItemToRegistryName(data.item)}
  }
}