import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import GroupService from '../../services/group.service';
import ExpenseService from '../../services/expense.service';
import UserService from '../../services/user.service';
import { useAuth } from '../../contexts/AuthContext';
import Loading from '../Common/Loading';
import ErrorMessage from '../Common/ErrorMessage';
import ExpenseList from '../Expense/ExpenseList';
import ExpenseCreate from '../Expense/ExpenseCreate';
import ConfirmDialog from '../Common/ConfirmDialog';

const GroupDetail = () => {
  const { groupId } = useParams();
  const navigate = useNavigate();
  const { currentUser } = useAuth();
  
  const [group, setGroup] = useState(null);
  const [expenses, setExpenses] = useState([]);
  const [users, setUsers] = useState([]);
  const [allUsers, setAllUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showAddExpense, setShowAddExpense] = useState(false);
  const [showInviteDialog, setShowInviteDialog] = useState(false);
  const [selectedUserToInvite, setSelectedUserToInvite] = useState(null);
  const [showCloseGroupDialog, setShowCloseGroupDialog] = useState(false);
  const [showAcknowledgmentAllExpensesDialog, setAcknowledgmentAllExpensesDialog] = useState(false);
  const [showAcknowledgmentAllExpensesButton, setAcknowledgmentAllExpensesButton] = useState(false);
  const [showAcknowledgmentAllExpenseParticipantenseDialog, setShowAcknowledgmentAllExpenseParticipantenseDialog] = useState(false);
  const [showAcknowledgmentAllExpenseParticipantenseButton, setShowAcknowledgmentAllExpenseParticipantenseButton] = useState(false);
  
  const [shouldBePaidByUser, setShouldBePaidByUser] = useState(null);
  const [selectedPaidByUserId, setSelectedPaidByUserId] = useState(null);
  const [showPaidBySelector, setShowPaidBySelector] = useState(false);
  const [paidByUser, setPaidByUser] = useState(null);

  const fetchGroupData = async () => {
    try {
      setLoading(true);
      const groupData = await GroupService.getGroupById(groupId);
      setGroup(groupData.group);
      setUsers(groupData.users);
      
      if (groupData.group.shouldBePaidBy) {
        const paidByUserData = await UserService.getUserById(groupData.group.shouldBePaidBy);
        setShouldBePaidByUser(paidByUserData);
      }
      
      if (groupData.group.paidBy) {
        const actualPaidByUser = await UserService.getUserById(groupData.group.paidBy);
        setPaidByUser(actualPaidByUser);
      }
      
      const expensesData = await GroupService.getGroupExpenses(groupId);
      
      const expensesWithParticipants = await Promise.all(
        expensesData.map(async (expense) => {
          const participants = await ExpenseService.getExpenseParticipants(expense.id);
          expense.participants = participants;
          return expense;
        })
      );

      setExpenses(expensesWithParticipants);
      
      const allUsersData = await UserService.getAllUsers();
      setAllUsers(allUsersData);

      const acknowledgmentsData = await GroupService.getAcknowledgments(groupId);
      console.log(acknowledgmentsData);
      setAcknowledgmentAllExpensesButton(!acknowledgmentsData.expense);
      setShowAcknowledgmentAllExpenseParticipantenseButton(!acknowledgmentsData.expenseParticipants);
    } catch (err) {
      setError(err.response.data.message);
    } finally {
      setLoading(false);
    }
  };
  
  useEffect(() => {
    fetchGroupData();
  }, [groupId]);


  const handleAddExpense = async (newExpense) => {
    try {
      const createdExpense = await ExpenseService.createExpense({
        ...newExpense,
        groupId: parseInt(groupId)
      });
      setExpenses([...expenses, createdExpense]);
      setShowAddExpense(false);
    } catch (err) {
      setError(err.response.data.message);
    }
  };

  const handleInviteUser = async () => {
    if (!selectedUserToInvite) return;
    
    try {
      await GroupService.inviteUser({
        groupId: parseInt(groupId),
        toUser: selectedUserToInvite
      });
      setShowInviteDialog(false);
      setSelectedUserToInvite(null);
    } catch (err) {
      setError(err.response.data.message);
    }
  };

  const handleCloseGroup = async () => {
    try {
      await GroupService.closeGroup(groupId);
      setGroup({ ...group, closedAt: new Date().toISOString() });
      setShowCloseGroupDialog(false);
    } catch (err) {
      setError(err.response.data.message);
    }
  };

  const handleAcknowledgmentAllExpenses = async () => {
    try {
      await GroupService.setAcknowledgmentAllExpenses(groupId);
      setAcknowledgmentAllExpensesDialog(false);
      setAcknowledgmentAllExpensesButton(false);
      fetchGroupData();
    } catch (err) {
      setError(err.response.data.message);
    }
  };

  const handleAcknowledgmentAllExpenseParticipantense = async () => {
    try {
      await GroupService.setAcknowledgmentAllExpenseParticipantense(groupId);
      setShowAcknowledgmentAllExpenseParticipantenseDialog(false);
      setShowAcknowledgmentAllExpenseParticipantenseButton(false);
      fetchGroupData();
    } catch (err) {
      setError(err.response.data.message);
    }
  };
  
  const handlePaidButtonClick = () => {
    setShowPaidBySelector(true);
  };

  const handlePaidBySubmit = async () => {
    if (!selectedPaidByUserId) {
      setError(err.response.data.message);
      return;
    }

    try {
      setLoading(true);
      await GroupService.setPaidBy(groupId, selectedPaidByUserId);
      
      const groupData = await GroupService.getGroupById(groupId);
      setGroup(groupData.group);
      
      if (groupData.group.shouldBePaidBy) {
        const paidByUserData = await UserService.getUserById(groupData.group.shouldBePaidBy);
        setShouldBePaidByUser(paidByUserData);
      }

      if (groupData.group.paidBy) {
        const actualPaidByUser = await UserService.getUserById(groupData.group.paidBy);
        setPaidByUser(actualPaidByUser);
      }

      
      setShowPaidBySelector(false);
      setSelectedPaidByUserId(null);
    } catch (err) {
      setError(err.response.data.message);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <Loading />;
  if (error) return <ErrorMessage message={error} />;
  if (!group) return <ErrorMessage message="Группа не найдена" />;

  const usersToInvite = allUsers.filter(
    user => !users.some(groupUser => groupUser.id === user.id) && user.id !== currentUser.id
  );

  const canCloseGroup = !group.closedAt;

  return (
    <div className="group-detail-container">
      <div className="group-header">
        <h1>{group.name}</h1>
        <div className="group-actions">
          {showAcknowledgmentAllExpensesButton && (
            <button
                className="btn btn-danger"
                onClick={() => setAcknowledgmentAllExpensesDialog(true)}
              >
                Утвердить траты
            </button>
          )}
          {showAcknowledgmentAllExpenseParticipantenseButton && (
            <button 
              className="btn btn-danger"
              onClick={() => setShowAcknowledgmentAllExpenseParticipantenseDialog(true)}
            >
              Утвердить участия в тратах
            </button>
          )}
          
          {canCloseGroup && (
            <button 
              className="btn btn-danger"
              onClick={() => setShowCloseGroupDialog(true)}
            >
              Закрыть группу
            </button>
          )}
          <button 
            className="btn btn-secondary"
            onClick={() => setShowInviteDialog(true)}
          >
            Пригласить пользователя
          </button>
        </div>
      </div>

      {group.description && (
        <div className="group-description card">
          <h3>Описание</h3>
          <p>{group.description}</p>
        </div>
      )}

      {group.closedAt && (
        <div className="group-closed-info card">
          <p>Группа закрыта {new Date(group.closedAt).toLocaleDateString()}</p>
        </div>
      )}

      <div className="group-payment-info card">
        <h3>Предлагаемый пользователь для оплаты</h3>
        <div className="payment-info-content">
          {shouldBePaidByUser ? (
            <p><strong>{shouldBePaidByUser.username}</strong></p>
          ) : (
            <p>Не назначен</p>
          )}

          <div className="payment-info-column">
            <h3>Оплатил</h3>
            <div className="payment-info-content">
              {paidByUser ? (
                <p><strong>{paidByUser.username}</strong></p>
              ) : (
                <p>Еще не оплачено</p>
              )}
            </div>
          </div>

          
          {!group.closedAt && (
            <div className="payment-actions">
              <button 
                className="btn btn-primary"
                onClick={handlePaidButtonClick}
              >
                Оплатил
              </button>
              
              {showPaidBySelector && (
                <div className="paid-by-selector mt-3">
                  <div className="form-group">
                    <select 
                      className="form-control"
                      onChange={(e) => setSelectedPaidByUserId(parseInt(e.target.value))}
                      value={selectedPaidByUserId || ''}
                    >
                      <option value="">Выберите пользователя</option>
                      {users.map(user => (
                        <option key={user.id} value={user.id}>
                          {user.username}
                        </option>
                      ))}
                    </select>
                  </div>
                  <button 
                    className="btn btn-success mt-2"
                    onClick={handlePaidBySubmit}
                  >
                    Подтвердить
                  </button>
                </div>
              )}
            </div>
          )}
        </div>
      </div>

      <div className="group-users card">
        <h3>Участники ({users.length})</h3>
        <div className="user-list">
          {users.map(user => (
            <div key={user.id} className="user-item">
              <span className="user-name">{user.username}</span>
              {user.id === group.paidBy && <span className="user-badge">Организатор</span>}
            </div>
          ))}
        </div>
      </div>

      <div className="group-expenses">
        <div className="expenses-header">
          <h2>Траты ({expenses.length})</h2>
          {!group.closedAt && (
            <button 
              className="btn btn-primary"
              onClick={() => setShowAddExpense(true)}
            >
              Добавить трату
            </button>
          )}
        </div>

        {showAddExpense ? (
          <div className="card">
            <ExpenseCreate 
              onSubmit={handleAddExpense}
              onCancel={() => setShowAddExpense(false)}
            />
          </div>
        ) : (
          <ExpenseList 
            expenses={expenses} 
            groupUsers={users}
            fetchGroupData={fetchGroupData} 
            isGroupClosed={!!group.closedAt}
          />
        )}
      </div>

      {showInviteDialog && (
        <ConfirmDialog
          title="Пригласить пользователя"
          message="Выберите пользователя для приглашения в группу"
          onConfirm={handleInviteUser}
          onCancel={() => {
            setShowInviteDialog(false);
            setSelectedUserToInvite(null);
          }}
          confirmText="Пригласить"
        >
          <div className="form-group">
            <select 
              className="form-control"
              onChange={(e) => setSelectedUserToInvite(parseInt(e.target.value))}
              value={selectedUserToInvite || ''}
            >
              <option value="">Выберите пользователя</option>
              {usersToInvite.map(user => (
                <option key={user.id} value={user.id}>
                  {user.username}
                </option>
              ))}
            </select>
          </div>
        </ConfirmDialog>
      )}

      {showCloseGroupDialog && (
        <ConfirmDialog
          title="Закрыть группу"
          message="После закрытия группы нельзя будет добавлять новые траты. Вы уверены?"
          onConfirm={handleCloseGroup}
          onCancel={() => setShowCloseGroupDialog(false)}
          confirmText="Закрыть группу"
          confirmButtonClass="btn-danger"
        />
      )}

      {showAcknowledgmentAllExpensesDialog && (
        <ConfirmDialog
          title="Утвердить траты"
          message="Вы уверены, что хотите утрвердить траты?"
          onConfirm={handleAcknowledgmentAllExpenses}
          onCancel={() => setAcknowledgmentAllExpensesDialog(false)}
          confirmText="Утвердить траты"
          confirmButtonClass="btn-danger"
        />
      )}

      {showAcknowledgmentAllExpenseParticipantenseDialog && (
        <ConfirmDialog
          title="Утвердить участия в тратах"
          message="Вы уверены, что хотите утвердить участия в тратах?"
          onConfirm={handleAcknowledgmentAllExpenseParticipantense}
          onCancel={() => setShowAcknowledgmentAllExpenseParticipantenseDialog(false)}
          confirmText="Утвердить участия в тратах"
          confirmButtonClass="btn-danger"
        />
      )}
    </div>
  );
};

export default GroupDetail;
