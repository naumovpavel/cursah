import React from 'react';

const ErrorMessage = ({ message }) => {
  return (
    <div className="error-container card">
      <div className="error-icon">⚠️</div>
      <h3>Произошла ошибка</h3>
      <p>{message || 'Что-то пошло не так. Пожалуйста, попробуйте еще раз.'}</p>
    </div>
  );
};

export default ErrorMessage;
