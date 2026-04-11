import { createApp } from 'vue'
import { createPinia } from 'pinia'
import PrimeVue from 'primevue/config'
import ToastService from 'primevue/toastservice'
import ConfirmationService from 'primevue/confirmationservice'

import 'primeicons/primeicons.css'
import './assets/global.css'

import App from './App.vue'
import router from './router'
import keycloak from './security/keycloak'
import { primeVueConfig } from './config/primeVueConfig'

keycloak.init({
  onLoad: 'login-required',
  pkceMethod: 'S256'
}).then((authenticated) => {
  if (!authenticated) {
    return keycloak.login();
  }
  const app = createApp(App)
  app.use(createPinia())
  app.use(PrimeVue, primeVueConfig)
  app.use(ToastService)
  app.use(ConfirmationService)
  app.use(router)
  app.mount('#app')
}).catch((error) => {
  console.error('Keycloak initialization error:', error);
})
