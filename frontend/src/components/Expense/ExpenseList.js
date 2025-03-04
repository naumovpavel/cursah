import React, { useState } from 'react';
import ExpenseService from '../../services/expense.service';
import { useAuth } from '../../contexts/AuthContext';
import ConfirmDialog from '../Common/ConfirmDialog';

const ExpenseList = ({ expenses, groupUsers, setExpenses, isGroupClosed }) => {
  const { currentUser } = useAuth();
  const [selectedExpense, setSelectedExpense] = useState(null);
  const [showConfirmJoinDialog, setShowConfirmJoinDialog] = useState(false);
  const [error, setError] = useState(null);

  const handleJoinExpense = async () => {
    if (!selectedExpense) return;

    try {
      await ExpenseService.joinExpense(selectedExpense.id);
      
      const updatedExpenses = expenses.map(expense => {
        if (expense.id === selectedExpense.id) {
          return {
            ...expense,
            participants: [...(expense.participants || []), currentUser.id]
          };
        }
        return expense;
      });
      
      setExpenses(updatedExpenses);
      setShowConfirmJoinDialog(false);
      setSelectedExpense(null);
    } catch (err) {
      setError('Не удалось присоединиться к трате.');
    }
  };

  const getParticipantNames = (expense) => {
    const participants = expense.participants || [];
    return participants.map(userId => {
      const user = groupUsers.find(u => u.id === userId);
      return user ? user.username : 'Неизвестный пользователь';
    }).join(', ');
  };

  const isParticipant = (expense) => {
    return expense.participants && expense.participants.includes(currentUser.id);
  };

  if (expenses.length === 0) {
    return (
      <div className="no-expenses card">
        <p>В этой группе пока нет трат.</p>
      </div>
    );
  }

  return (
    <div className="expense-list">
      {error && <div className="error-message">{error}</div>}
      
      {expenses.map(expense => (
        <div key={expense.id} className="expense-item card">
          <div className="expense-header">
            <h3>{expense.name}</h3>
            <span className="expense-value">{expense.value} ₽</span>
          </div>
          
          <div className="expense-participants">
            <p>
              <strong>Участники:</strong> {getParticipantNames(expense) || 'Пока нет участников'}
            </p>
          </div>
          
          {!isGroupClosed && !isParticipant(expense) && (
            <button 
              className="btn btn-secondary"
              onClick={() => {
                setSelectedExpense(expense);
                setShowConfirmJoinDialog(true);
              }}
            >
              Присоединиться
            </button>
          )}
        </div>
      ))}

      {showConfirmJoinDialog && selectedExpense && (
        <ConfirmDialog
          title="Присоединиться к трате"
          message={`Вы уверены, что хотите присоединиться к трате "${selectedExpense.name}" на сумму ${selectedExpense.value} ₽?`}
          onConfirm={handleJoinExpense}
          onCancel={() => {
            setShowConfirmJoinDialog(false);
            setSelectedExpense(null);
          }}
          confirmText="Присоединиться"
        />
      )}
    </div>
  );
};

export default ExpenseList;
