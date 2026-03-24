import axios from 'axios';
import keycloak from '../security/keycloak';

const apiClient = axios.create({
  baseURL: '/api',
});

// Interceptor adding a token to each request
apiClient.interceptors.request.use(
  async (config) => {
    try {
      await keycloak.updateToken(30);

      config.headers.Authorization = `Bearer ${keycloak.token}`;
      return config;
    } catch (error) {
      console.error("Token refresh error. Redirection to login.");
      await keycloak.login();
      return Promise.reject(error);
    }
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default apiClient;
