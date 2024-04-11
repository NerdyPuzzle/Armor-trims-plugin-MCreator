<#include "../transformer.ftl">
{
  "replace": false,
  "values": [
    <#list data.materials as material>
        ${mappedMCItemToRegistryName(material)}<#sep>,
    </#list>
  ]
}