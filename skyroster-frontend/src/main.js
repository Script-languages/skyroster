import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import keycloak from './security/keycloak'

keycloak.init({
  onLoad: 'login-required',
  pkceMethod: 'S256'
}).then((authenticated) => {
 if (!authenticated) {
    return keycloak.login();
  }
  const app = createApp(App)
  app.use(createPinia())
  app.use(router)
  app.mount('#app')
}).catch((error) => {
  console.error('Keycloak initialization error:', error);
})
