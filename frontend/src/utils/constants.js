export const ROLES = {
    USER: 'ROLE_USER',
    ADMIN: 'ROLE_ADMIN',
    MODERATOR: 'ROLE_MODERATOR'
  };
  
  export const INVITE_STATUS = {
    PENDING: 0,
    ACCEPTED: 1,
    REJECTED: 2
  };
  
  export const ERROR_MESSAGES = {
    REQUIRED: 'Это поле обязательно для заполнения',
    INVALID_NAME: 'Имя должно содержать от 3 до 50 символов',
    INVALID_PASSWORD: 'Пароль должен содержать минимум 6 символов',
    LOGIN_FAILED: 'Неверное имя пользователя или пароль',
    REGISTRATION_FAILED: 'Ошибка при регистрации. Возможно, имя пользователя уже занято',
    NETWORK_ERROR: 'Ошибка сети. Пожалуйста, проверьте подключение'
  };