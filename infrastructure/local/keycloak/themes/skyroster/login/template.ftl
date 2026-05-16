<#macro registrationLayout displayInfo=false displayMessage=true displayRequiredFields=false>
<!DOCTYPE html>
<html lang="${locale.currentLanguageTag}">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>${msg("loginTitle", (realm.displayName!realm.name))}</title>
  <link rel="icon" href="${url.resourcesPath}/img/favicon.ico" type="image/x-icon"/>
  <#if properties.styles?has_content>
    <#list properties.styles?split(' ') as style>
      <link href="${url.resourcesPath}/${style}" rel="stylesheet"/>
    </#list>
  </#if>
</head>
<body class="sr-body">
  <main class="sr-card-wrapper">
    <section class="sr-card">
      <header class="sr-card__header">
        <h1 class="sr-brand">SkyRoster</h1>
        <p class="sr-card__subtitle"><#nested "header"></p>
      </header>

      <#if displayMessage && message?has_content && (message.type != 'warning' || !isAppInitiatedAction??)>
        <div class="sr-alert sr-alert--${message.type}">
          <span>${kcSanitize(message.summary)?no_esc}</span>
        </div>
      </#if>

      <div class="sr-card__body">
        <#nested "form">
      </div>

      <#if displayInfo>
        <div class="sr-card__info">
          <#nested "info">
        </div>
      </#if>
    </section>
  </main>
</body>
</html>
</#macro>
