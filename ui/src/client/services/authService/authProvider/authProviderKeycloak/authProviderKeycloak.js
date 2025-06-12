import Keycloak from 'keycloak-js';
import AuthProviderAbstract from '../authProvider';
import keycloakJson from './config/keycloak.json';

export default class authProviderKeycloak extends AuthProviderAbstract {
  constructor() {
    super();
    this.keycloak = Keycloak(keycloakJson);
  }

  init() {
    return new Promise((resolve, reject) => {
      this.keycloak.init({ onLoad: 'login-required', checkLoginIframe: false})
        .success(() => {
          resolve(true);
        })
        .error((error) => {
          reject(error);
        });
    });
  }

  login() {
    return this.keycloak.login();
  }

  logout() {
    this.keycloak.logout();
  }

  createLogoutUrl() {
    return this.keycloak.createLogoutUrl();
  }

  loadUserInfo() {
    return new Promise((resolve, reject) => {
      this.keycloak.loadUserInfo()
        .success(res => resolve(res))
        .error(error => reject(error));
    });
  }

  loadUserProfile() {
    return new Promise((resolve, reject) => {
      this.keycloak.loadUserProfile()
        .success(res => resolve(res))
        .error(error => reject(error));
    });
  }

  updateToken(minValidity = 5) {
    return new Promise((resolve, reject) => {
      this.keycloak.updateToken(minValidity)
        .success(() => {
          resolve(this.keycloak.token);
        })
        .error(error => reject(error));
    });
  }
}
