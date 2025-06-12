/* eslint-disable class-methods-use-this */
export default class AuthProviderAbstract {
  constructor() {
    if (new.target === AuthProviderAbstract) {
      throw new TypeError('Cannot construct Abstract instances directly');
    }
  }

  init() {
    throw new Error('no implementation of init');
  }

  login() {
    throw new Error('no implementation of login');
  }

  logout() {
    throw new Error('no implementation of logout');
  }

  createLogoutUrl() {
    throw new Error('no implementation of createLogoutUrl');
  }

  updateToken() {
    throw new Error('no implementation of updateToken');
  }

  loadUserInfo() {
    throw new Error('no implementation of loadUserInfo');
  }

  loadUserProfile() {
    throw new Error('no implementation of loadUserInfo');
  }
}
