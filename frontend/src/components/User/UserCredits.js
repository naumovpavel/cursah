import React, { useState } from 'react';
import ConfirmDialog from '../Common/ConfirmDialog';

const UserCredits = ({ credits, onApproveCredit }) => {
  const [selectedCredit, setSelectedCredit] = useState(null);
  const [showApproveDialog, setShowApproveDialog] = useState(false);

  const handleApprove = async () => {
    if (!selectedCredit) return;
    
    await onApproveCredit(selectedCredit.id);
    setShowApproveDialog(false);
    setSelectedCredit(null);
  };

  if (credits.length === 0) {
    return (
      <div className="no-data card">
        <p>Вам никто не должен.</p>
      </div>
    );
  }

  return (
    <div className="user-credits">
      {credits.map(credit => {
        const remainingAmount = credit.creditAmount - credit.returnedAmount;
        return (
            <div key={credit.id} className="credit-item card">
            <div className="credit-header">
              <h3>Долг от пользователя {credit.username}</h3>
              <span className="credit-status">
                {credit.approved ? 'Подтверждено' : 'Ожидает подтверждения'}
              </span>
            </div>
            
            <div className="credit-info">
              <p><strong>Общая сумма:</strong> {credit.creditAmount} ₽</p>
              <p><strong>Возвращено:</strong> {credit.returnedAmount} ₽</p>
              <p><strong>Осталось вернуть:</strong> {remainingAmount} ₽</p>
            </div>
            
            {!credit.approved && remainingAmount === 0 && (
              <button 
                className="btn btn-primary"
                onClick={() => {
                  setSelectedCredit(credit);
                  setShowApproveDialog(true);
                }}
              >
                Подтвердить возврат
              </button>
            )}
          </div>
        );
      })}

      {showApproveDialog && selectedCredit && (
        <ConfirmDialog
          title="Подтверждение возврата"
          message="Вы уверены, что хотите подтвердить полный возврат долга?"
          onConfirm={handleApprove}
          onCancel={() => {
            setShowApproveDialog(false);
            setSelectedCredit(null);
          }}
          confirmText="Подтвердить"
        />
      )}
    </div>
  );
};

export default UserCredits;