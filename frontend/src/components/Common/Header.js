import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';

const Header = () => {
  const { currentUser, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/auth');
  };

  return (
    <header className="app-header">
      <div className="header-content">
        <div className="logo">
          <Link to="/">Split me please</Link>
        </div>
        
        {isAuthenticated && (
          <nav className="main-nav">
            <ul>
              <li>
                <Link to="/">Группы</Link>
              </li>
              <li>
                <Link to="/profile">Личный кабинет</Link>
              </li>
            </ul>
          </nav>
        )}
        
        <div className="user-actions">
          {isAuthenticated ? (
            <>
              <span className="username">{currentUser.username}</span>
              <button className="btn btn-logout" onClick={handleLogout}>
              Выйти
              </button>
            </>
          ) : (
            <Link to="/auth" className="btn btn-login">
              Войти
            </Link>
          )}
        </div>
      </div>
    </header>
  );
};

export default Header;
