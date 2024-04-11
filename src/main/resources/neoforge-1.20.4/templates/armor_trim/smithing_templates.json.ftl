<#include "../transformer.ftl">
{
  "values": [
    <#list armortrims as trim>
        ${mappedMCItemToRegistryName(trim.item)}<#sep>,
    </#list>
  ],
  "replace": false
}