import React, { useState } from 'react';
import ConfirmDialog from '../Common/ConfirmDialog';

const UserDebts = ({ debts, onReturnCredit }) => {
  const [selectedDebt, setSelectedDebt] = useState(null);
  const [showReturnDialog, setShowReturnDialog] = useState(false);
  const [returnAmount, setReturnAmount] = useState('');
  const [error, setError] = useState(null);

  const handleReturn = async () => {
    if (!selectedDebt) return;
    
    const amount = parseFloat(returnAmount);
    if (isNaN(amount) || amount <= 0) {
      setError('Введите корректную сумму возврата');
      return;
    }

    const remainingAmount = selectedDebt.creditAmount - selectedDebt.returnedAmount;
    if (amount > remainingAmount) {
      setError(`Сумма возврата не может превышать оставшуюся сумму долга (${remainingAmount} ₽)`);
      return;
    }

    await onReturnCredit(selectedDebt.id, amount);
    setShowReturnDialog(false);
    setSelectedDebt(null);
    setReturnAmount('');
    setError(null);
  };

  if (debts.length === 0) {
    return (
      <div className="no-data card">
        <p>У вас нет долгов.</p>
      </div>
    );
  }

  return (
    <div className="user-debts">
      {debts.map(debt => {
        const remainingAmount = debt.creditAmount - debt.returnedAmount;
        const isFullyPaid = remainingAmount <= 0;
        
        return (
          <div key={debt.id} className="debt-item card">
            <div className="debt-header">
              <h3>Долг пользователю {debt.username}</h3>
              <span className={`debt-status ${debt.approved ? 'paid' : ''}`}>
                {debt.approved ? 'Оплачено' : isFullyPaid ? 'Ожидает подтверждения' : 'Активен'}
              </span>
            </div>
            
            <div className="debt-info">
              <p><strong>Общая сумма:</strong> {debt.creditAmount} ₽</p>
              <p><strong>Возвращено:</strong> {debt.returnedAmount} ₽</p>
              <p><strong>Осталось вернуть:</strong> {remainingAmount > 0 ? `${remainingAmount} ₽` : '0 ₽'}</p>
            </div>
            
            {!debt.approved && remainingAmount > 0 && (
              <button 
                className="btn btn-primary"
                onClick={() => {
                  setSelectedDebt(debt);
                  setReturnAmount('');
                  setShowReturnDialog(true);
                }}
              >
                Вернуть долг
              </button>
            )}
          </div>
        );
      })}

      {showReturnDialog && selectedDebt && (
        <ConfirmDialog
          title="Возврат долга"
          message="Укажите сумму, которую вы хотите вернуть"
          onConfirm={handleReturn}
          onCancel={() => {
            setShowReturnDialog(false);
            setSelectedDebt(null);
            setReturnAmount('');
            setError(null);
          }}
          confirmText="Вернуть"
        >
          {error && <div className="error-message">{error}</div>}
          
          <div className="form-group">
            <label htmlFor="returnAmount">Сумма возврата (₽)</label>
            <input 
              type="number" 
              id="returnAmount"
              className="form-control"
              placeholder="Введите сумму"
              value={returnAmount}
              onChange={(e) => setReturnAmount(e.target.value)}
              min="0.01"
              step="0.01"
              max={selectedDebt.creditAmount - selectedDebt.returnedAmount}
            />
            <small className="help-text">
              Максимальная сумма: {selectedDebt.creditAmount - selectedDebt.returnedAmount} ₽
            </small>
          </div>
        </ConfirmDialog>
      )}
    </div>
  );
};

export default UserDebts;
