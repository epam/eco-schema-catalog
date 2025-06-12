/* eslint-disable class-methods-use-this */
import axios from 'axios';
import AuthProviders from './authProvider/authProviders';

export default class AuthService {
  static instace = null

  static getInstance(providerId) {
    if (!AuthService.instace) {
      AuthService.instace = new AuthService(providerId);
    }
    return AuthService.instace;
  }

  constructor(providerId) {
    if (!providerId) {
      this.provider = null;
    } else {
      this.provider = AuthProviders[providerId];
    }
  }

  init() {
    if (!this.provider) {
      return Promise.resolve(true);
    }
    axios.interceptors.request.use(config => this.provider.updateToken()
      .then((token) => {
        const cloneConfig = config;
        cloneConfig.headers.Authorization = `Bearer ${token}`;
        return Promise.resolve(cloneConfig);
      })
      .catch(() => { this.provider.login(); }));
    return this.provider.init();
  }

  logout() {
    if (!this.provider) {
      return;
    }
    this.provider.logout();
  }

  getlogoutUrl() {
    if (!this.provider) {
      return '/';
    }
    return this.provider.createLogoutUrl();
  }

  getUserInfo() {
    if (!this.provider) {
      const error = 'no user info';
      return Promise.reject(error);
    }
    return new Promise((resolve, reject) => {
      this.provider.loadUserInfo()
        .then(res => resolve(res))
        .catch(error => reject(error));
    });
  }

  getUserProfile() {
    if (!this.provider) {
      return Promise.resolve('no user profile');
    }
    return new Promise((resolve, reject) => {
      this.provider.loadUserProfile()
        .then(res => resolve(res))
        .catch(error => reject(error));
    });
  }

  isAdmin() {
    if (!this.provider) {
      return false;
    }
    return this.provider.keycloak.hasResourceRole('ROLE_ECO-SC-UI-ADMIN');
  }
}
