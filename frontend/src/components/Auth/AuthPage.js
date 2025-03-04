import React, { useState } from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import Login from './Login';
import Register from './Register';
import '../../assets/styles/global.css';

const AuthPage = () => {
  const [isLoginView, setIsLoginView] = useState(true);
  const { isAuthenticated } = useAuth();

  if (isAuthenticated) {
    return <Navigate to="/" />;
  }

  return (
    <div className="auth-container">
      <div className="auth-card card">
        <div className="tabs">
          <div 
            className={`tab ${isLoginView ? 'active' : ''}`}
            onClick={() => setIsLoginView(true)}
          >
            Вход
          </div>
          <div 
            className={`tab ${!isLoginView ? 'active' : ''}`}
            onClick={() => setIsLoginView(false)}
          >
            Регистрация
          </div>
        </div>
        
        {isLoginView ? <Login /> : <Register />}
      </div>
    </div>
  );
};

export default AuthPage;