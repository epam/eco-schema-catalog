import AuthService from '../../services/authService/authService';
import { GOT_USER_INFO, GOT_USER_ROLE, GOT_USER_LOGOUT_URL } from '../../consts/consts';

export const gotUserInfo = (name, email) => ({
  type: GOT_USER_INFO,
  name,
  email,
});

export const gotRole = isAdmin => ({
  type: GOT_USER_ROLE,
  isAdmin,
});

export const getLogoutUrl = () => {
  const authService = AuthService.getInstance();
  return {
    type: GOT_USER_LOGOUT_URL,
    logoutUrl: authService.getlogoutUrl(),
  };
};

export const getUserAsync = () => (dispatch) => {
  const authService = AuthService.getInstance();
  authService.getUserInfo()
    .then((res) => {
      dispatch(gotUserInfo(res.name, res.email));
    });
};
