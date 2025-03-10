import React, { useState } from 'react';
import ExpenseService from '../../services/expense.service';
import { useAuth } from '../../contexts/AuthContext';
import ConfirmDialog from '../Common/ConfirmDialog';

const ExpenseList = ({ expenses, groupUsers, fetchGroupData, isGroupClosed }) => {
  const { currentUser } = useAuth();
  const [selectedExpense, setSelectedExpense] = useState(null);
  const [showConfirmJoinDialog, setShowConfirmJoinDialog] = useState(false);
  const [showAddParticipantDialog, setShowAddParticipantDialog] = useState(false);
  const [selectedUserToAdd, setSelectedUserToAdd] = useState(null);
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
      
      fetchGroupData();
      setShowConfirmJoinDialog(false);
      setSelectedExpense(null);
    } catch (err) {
      setError(err.response.data.message);
    }
  };
  
  const handleAddParticipant = async () => {
    if (!selectedExpense || !selectedUserToAdd) return;

    try {
      await ExpenseService.joinExpense(selectedExpense.id, selectedUserToAdd);
      
      const updatedExpenses = expenses.map(expense => {
        if (expense.id === selectedExpense.id) {
          const newParticipant = {
            userId: selectedUserToAdd,
            confirmed: false
          };
          
          const updatedParticipants = [...(expense.participants || []), newParticipant];
          
          return {
            ...expense,
            participants: updatedParticipants
          };
        }
        return expense;
      });
      
      fetchGroupData();
      setShowAddParticipantDialog(false);
      setSelectedExpense(null);
      setSelectedUserToAdd(null);
    } catch (err) {
      setError(err.response.data.message);
    }
  };
  
  const handleConfirmExpense = async (expenseId) => {
    try {
      await ExpenseService.confirmExpense(expenseId);
      
      const updatedExpenses = expenses.map(expense => {
        if (expense.id === expenseId) {
          const updatedParticipants = expense.participants.map(participant => {
            if (participant.userId === currentUser.id) {
              return { ...participant, confirmed: true };
            }
            return participant;
          });
          
          return {
            ...expense,
            participants: updatedParticipants
          };
        }
        return expense;
      });
      
      fetchGroupData();
    } catch (err) {
      setError(err.response.data.message);
    }
  };
  
  const handleRejectExpense = async (expenseId) => {
    try {
      await ExpenseService.rejectExpense(expenseId);
      
      const updatedExpenses = expenses.map(expense => {
        if (expense.id === expenseId) {
          const updatedParticipants = expense.participants.filter(
            participant => participant.userId !== currentUser.id
          );
          
          return {
            ...expense,
            participants: updatedParticipants
          };
        }
        return expense;
      });
      
      fetchGroupData();
    } catch (err) {
      setError(err.response.data.message);
    }
  };

  const getParticipantNames = (expense) => {
    if (!expense.participants) return '';
    
    if (typeof expense.participants[0] === 'object') {
      return expense.participants.map(participant => {
        const user = groupUsers.find(u => u.id === participant.userId);
        const status = participant.confirmed ? '' : ' (ожидает подтверждения)';
        return user ? user.username + status : 'Неизвестный пользователь' + status;
      }).join(', ');
    } else {
      return expense.participants.map(userId => {
        const user = groupUsers.find(u => u.id === userId);
        return user ? user.username : 'Неизвестный пользователь';
      }).join(', ');
    }
  };

  const isParticipant = (expense) => {
    if (!expense.participants || !expense.participants.length) return false;
    console.log(expense, currentUser);
    return expense.participants.some(p => p.userId === currentUser.id && p.confirmed);
  };
  
  const needsConfirmation = (expense) => {
    if (!expense.participants || !expense.participants.length) return false;
    
    return expense.participants.some(p => p.userId === currentUser.id && !p.confirmed);
  };
  
  const getNonParticipantUsers = (expense) => {
    if (!expense.participants || !expense.participants.length) return groupUsers;
    const participantUserIds = typeof expense.participants[0] === 'object' 
      ? expense.participants.map(p => p.userId)
      : expense.participants || [];
      
    return groupUsers.filter(user => !participantUserIds.includes(user.id));
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
          
          <div className="expense-actions">
            {!isGroupClosed && !isParticipant(expense) && !needsConfirmation(expense) && (
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
            
            {!isGroupClosed && (
              <button 
                className="btn btn-outline-primary"
                onClick={() => {
                  setSelectedExpense(expense);
                  setShowAddParticipantDialog(true);
                }}
              >
                Добавить участника
              </button>
            )}
            
            {needsConfirmation(expense) && (
              <div className="confirmation-actions">
                <button 
                  className="btn btn-success mr-2"
                  onClick={() => handleConfirmExpense(expense.id)}
                >
                  Принять
                  </button>
                <button 
                  className="btn btn-danger"
                  onClick={() => handleRejectExpense(expense.id)}
                >
                  Отклонить
                </button>
              </div>
            )}
          </div>
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
      
      {showAddParticipantDialog && selectedExpense && (
        <ConfirmDialog
          title="Добавить участника к трате"
          message="Выберите пользователя, которого хотите добавить к трате"
          onConfirm={handleAddParticipant}
          onCancel={() => {
            setShowAddParticipantDialog(false);
            setSelectedExpense(null);
            setSelectedUserToAdd(null);
          }}
          confirmText="Добавить"
        >
          <div className="form-group">
            <select 
              className="form-control"
              onChange={(e) => setSelectedUserToAdd(parseInt(e.target.value))}
              value={selectedUserToAdd || ''}
            >
              <option value="">Выберите пользователя</option>
              {getNonParticipantUsers(selectedExpense).map(user => (
                <option key={user.id} value={user.id}>
                  {user.username}
                </option>
              ))}
            </select>
          </div>
        </ConfirmDialog>
      )}
    </div>
  );
};

export default ExpenseList;
