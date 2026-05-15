<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('username','password'); section>
  <#if section = "header">
    ${msg("loginAccountTitle")}
  <#elseif section = "form">
    <form id="kc-form-login" action="${url.loginAction}" method="post" novalidate="novalidate">

      <div class="sr-field">
        <label for="username" class="sr-label">${msg("usernameOrEmail")}</label>
        <input
          id="username"
          class="sr-input"
          name="username"
          value="${(login.username!'')}"
          type="text"
          autofocus
          autocomplete="username"
          aria-invalid="${(messagesPerField.existsError('username','password'))?c}"
        />
        <span class="sr-field__error" aria-live="polite">
          <#if messagesPerField.existsError('username','password')>
            ${kcSanitize(messagesPerField.getFirstError('username','password'))?no_esc}
          </#if>
        </span>
      </div>

      <div class="sr-field">
        <label for="password" class="sr-label">${msg("password")}</label>
        <input
          id="password"
          class="sr-input"
          name="password"
          type="password"
          autocomplete="current-password"
          aria-invalid="${(messagesPerField.existsError('username','password'))?c}"
        />
      </div>

      <#if realm.rememberMe && !usernameHidden??>
        <div class="sr-checkbox">
          <input id="rememberMe" name="rememberMe" type="checkbox"
            <#if login.rememberMe??>checked</#if>
          />
          <label for="rememberMe">${msg("rememberMe")}</label>
        </div>
      </#if>

      <input type="hidden" id="id-hidden-input" name="credentialId" <#if auth.selectedCredential?has_content>value="${auth.selectedCredential}"</#if>/>

      <button
        class="sr-button sr-button--primary"
        name="login"
        id="kc-login"
        type="submit"
      >${msg("doLogIn")}</button>

    </form>
  </#if>
</@layout.registrationLayout>
