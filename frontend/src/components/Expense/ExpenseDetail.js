import React, { useState, useEffect } from 'react';
import ExpenseService from '../../services/expense.service';
import { useAuth } from '../../contexts/AuthContext';
import Loading from '../Common/Loading';
import ErrorMessage from '../Common/ErrorMessage';

const ExpenseDetail = ({ expenseId, groupUsers, onClose }) => {
  const { currentUser } = useAuth();
  const [expense, setExpense] = useState(null);
  const [participants, setParticipants] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchExpenseDetails = async () => {
      try {
        setLoading(true);
        const participantsData = await ExpenseService.getExpenseParticipants(expenseId);
        setParticipants(participantsData);
      } catch (err) {
        setError(err.response.data.message);
      } finally {
        setLoading(false);
      }
    };

    fetchExpenseDetails();
  }, [expenseId]);

  const handleJoinExpense = async () => {
    try {
      await ExpenseService.joinExpense(expenseId);
      setParticipants([...participants, currentUser.id]);
    } catch (err) {
      setError(err.response.data.message);
    }
  };

  const handleConfirmExpense = async () => {
    try {
      await ExpenseService.confirmExpense(expenseId);
      onClose();
    } catch (err) {
      setError(err.response.data.message);
    }
  };

  if (loading) return <Loading />;
  if (error) return <ErrorMessage message={error} />;

  const participantNames = participants.map(userId => {
    const user = groupUsers.find(u => u.id === userId);
    return user ? user.username : 'Неизвестный пользователь';
  });

  const isParticipant = participants.includes(currentUser.id);
  const perPersonAmount = expense ? expense.value / (participants.length || 1) : 0;

  return (
    <div className="expense-detail card">
      <h2>{expense?.name}</h2>
      
      <div className="expense-info">
        <p><strong>Общая сумма:</strong> {expense?.value} ₽</p>
        <p><strong>Количество участников:</strong> {participants.length}</p>
        <p><strong>Сумма на человека:</strong> {perPersonAmount.toFixed(2)} ₽</p>
      </div>
      
      <div className="expense-participants">
        <h3>Участники:</h3>
        {participantNames.length > 0 ? (
          <ul>
            {participantNames.map((name, index) => (
              <li key={index}>{name}</li>
            ))}
          </ul>
        ) : (
          <p>Пока нет участников</p>
        )}
      </div>
      
      <div className="expense-actions">
        {!isParticipant && (
          <button 
            className="btn btn-primary"
            onClick={handleJoinExpense}
          >
            Присоединиться к трате
            </button>
        )}
        
        <button 
          className="btn btn-secondary"
          onClick={handleConfirmExpense}
        >
          Подтвердить трату
        </button>
        
        <button 
          className="btn btn-secondary"
          onClick={onClose}
        >
          Закрыть
        </button>
      </div>
    </div>
  );
};

export default ExpenseDetail;
