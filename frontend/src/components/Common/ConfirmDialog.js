import React from 'react';

const ConfirmDialog = ({ 
  title, 
  message, 
  onConfirm, 
  onCancel, 
  confirmText = 'Подтвердить', 
  cancelText = 'Отмена',
  confirmButtonClass = 'btn-primary',
  children
}) => {
  return (
    <div className="confirm-dialog-backdrop">
      <div className="confirm-dialog card">
        <div className="dialog-header">
          <h3>{title}</h3>
        </div>
        
        <div className="dialog-content">
          {message && <p>{message}</p>}
          {children}
        </div>
        
        <div className="dialog-actions">
          <button 
            className="btn btn-secondary"
            onClick={onCancel}
          >
            {cancelText}
          </button>
          <button 
            className={`btn ${confirmButtonClass}`}
            onClick={onConfirm}
          >
            {confirmText}
          </button>
        </div>
      </div>
    </div>
  );
};

export default ConfirmDialog;
