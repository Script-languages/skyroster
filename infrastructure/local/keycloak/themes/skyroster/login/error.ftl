<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=false; section>
  <#if section = "header">
    ${msg("errorTitle")}
  <#elseif section = "form">
    <div class="sr-alert sr-alert--error" role="alert">
      <span>${kcSanitize(message.summary)?no_esc}</span>
    </div>
    <#if client?? && client.baseUrl?has_content>
      <a href="${client.baseUrl}" class="sr-button sr-button--secondary">${msg("backToLogin")}</a>
    <#else>
      <a href="/" class="sr-button sr-button--secondary">${msg("backToLogin")}</a>
    </#if>
  </#if>
</@layout.registrationLayout>
