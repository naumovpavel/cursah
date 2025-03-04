import React, { useState } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import { Formik, Form, Field, ErrorMessage } from 'formik';
import * as Yup from 'yup';
import { ERROR_MESSAGES } from '../../utils/constants';

const loginSchema = Yup.object().shape({
  name: Yup.string()
    .required(ERROR_MESSAGES.REQUIRED)
    .min(3, ERROR_MESSAGES.INVALID_NAME)
    .max(50, ERROR_MESSAGES.INVALID_NAME),
  password: Yup.string()
    .required(ERROR_MESSAGES.REQUIRED)
    .min(6, ERROR_MESSAGES.INVALID_PASSWORD)
});

const Login = () => {
  const { login } = useAuth();
  const [error, setError] = useState(null);

  const handleSubmit = async (values, { setSubmitting }) => {
    try {
      await login(values.name, values.password);
    } catch (err) {
      setError(ERROR_MESSAGES.LOGIN_FAILED);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="login-form">
      <h2>Вход в систему</h2>
      {error && <div className="error-message">{error}</div>}
      
      <Formik
        initialValues={{ name: '', password: '' }}
        validationSchema={loginSchema}
        onSubmit={handleSubmit}
      >
        {({ isSubmitting }) => (
          <Form>
            <div className="form-group">
              <label htmlFor="name">Имя пользователя</label>
              <Field 
                type="text" 
                name="name" 
                id="name" 
                className="form-control" 
                placeholder="Введите имя пользователя" 
              />
              <ErrorMessage name="name" component="div" className="error-message" />
            </div>

            <div className="form-group">
            <label htmlFor="password">Пароль</label>
              <Field 
                type="password" 
                name="password" 
                id="password" 
                className="form-control" 
                placeholder="Введите пароль" 
              />
              <ErrorMessage name="password" component="div" className="error-message" />
            </div>

            <button 
              type="submit" 
              className="btn btn-primary" 
              disabled={isSubmitting}
            >
              {isSubmitting ? 'Выполняется вход...' : 'Войти'}
            </button>
          </Form>
        )}
      </Formik>
    </div>
  );
};

export default Login;
