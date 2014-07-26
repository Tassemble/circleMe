<#if hasInvitation?string("1", "0") == "1">
    <#include "/pages/member/invitationIndex.ftl" />
<#else>
    <#include "/pages/901index.ftl" />
</#if>
<!-- 引用src目录下的模板文件,这样做的目的,是因为打包工具只能找到src目录下的template文件  -->