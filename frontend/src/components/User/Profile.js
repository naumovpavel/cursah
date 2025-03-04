import React, { useState, useEffect } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import UserService from '../../services/user.service';
import CreditService from '../../services/credit.service';
import UserGroups from './UserGroups';
import UserCredits from './UserCredits';
import UserDebts from './UserDebts';
import UserInvites from './UserInvites';
import Loading from '../Common/Loading';
import ErrorMessage from '../Common/ErrorMessage';

const Profile = () => {
  const { currentUser } = useAuth();
  const [activeTab, setActiveTab] = useState('groups');
  const [userGroups, setUserGroups] = useState([]);
  const [userCredits, setUserCredits] = useState([]);
  const [userDebts, setUserDebts] = useState([]);
  const [userInvites, setUserInvites] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchUserData = async () => {
      try {
        setLoading(true);
        const userId = currentUser.id;
        
        const [groups, credits, debts, invites] = await Promise.all([
          UserService.getGroups(),
          CreditService.getCreditsTo(userId),
          CreditService.getCreditsFrom(userId),
          UserService.getUserInvites()
        ]);
        
        setUserGroups(groups);

        const creditsWithUsernames = await Promise.all(
          credits.map(async (credit) => {
            const user = await UserService.getUserById(credit.fromUser);
            credit.username = user.username;
            return credit;
          })
        );

        const debtsWithUsernames = await Promise.all(
          debts.map(async (debt) => {
            const user = await UserService.getUserById(debt.toUser);
            debt.username = user.username;
            return debt;
          })
        );


        setUserCredits(creditsWithUsernames);
        setUserDebts(debtsWithUsernames);
        setUserInvites(invites);
      } catch (err) {
        setError('Не удалось загрузить данные профиля.');
      } finally {
        setLoading(false);
      }
    };

    fetchUserData();
  }, [currentUser.id]);

  const handleApproveInvite = async (inviteId) => {
    try {
      await UserService.approveInvite(inviteId);
      setUserInvites(userInvites.filter(invite => invite.id !== inviteId));
      const groups = await UserService.getGroups();
      setUserGroups(groups);
    } catch (err) {
      setError('Не удалось принять приглашение.');
    }
  };

  const handleRejectInvite = async (inviteId) => {
    try {
      await UserService.rejectInvite(inviteId);
      setUserInvites(userInvites.filter(invite => invite.id !== inviteId));
    } catch (err) {
      setError('Не удалось отклонить приглашение.');
    }
  };

  const handleReturnCredit = async (creditId, amount) => {
    try {
      await CreditService.returnCredit(creditId, amount);
      const [credits, debts] = await Promise.all([
        CreditService.getCreditsTo(currentUser.id),
        CreditService.getCreditsFrom(currentUser.id)
      ]);
      
      setUserCredits(credits);
      setUserDebts(debts);
    } catch (err) {
      setError('Не удалось вернуть долг.');
    }
  };

  const handleApproveCredit = async (creditId) => {
    try {
      await CreditService.approveCredit(creditId);
      const [credits, debts] = await Promise.all([
        CreditService.getCreditsTo(currentUser.id),
        CreditService.getCreditsFrom(currentUser.id)
      ]);
      
      setUserCredits(credits);
      setUserDebts(debts);
    } catch (err) {
      setError('Не удалось подтвердить возврат долга.');
    }
  };

  if (loading) return <Loading />;
  if (error) return <ErrorMessage message={error} />;

  const pendingInvites = userInvites.filter(invite => invite.status === 0).length;

  return (
    <div className="profile-container">
      <div className="profile-header card">
        <h1>Личный кабинет</h1>
        <div className="user-info">
          <p>Пользователь: <strong>{currentUser.username}</strong></p>
        </div>
      </div>

      <div className="tabs">
        <div 
          className={`tab ${activeTab === 'groups' ? 'active' : ''}`} 
          onClick={() => setActiveTab('groups')}
        >
          Мои группы ({userGroups.length})
        </div>
        <div 
          className={`tab ${activeTab === 'credits' ? 'active' : ''}`} 
          onClick={() => setActiveTab('credits')}
        >
          Мне должны ({userCredits.length})
        </div>
        <div 
          className={`tab ${activeTab === 'debts' ? 'active' : ''}`} 
          onClick={() => setActiveTab('debts')}
        >
          Я должен ({userDebts.length})
        </div>
        <div 
          className={`tab ${activeTab === 'invites' ? 'active' : ''}`} 
          onClick={() => setActiveTab('invites')}
        >
          Приглашения {pendingInvites > 0 && (`${pendingInvites}`)}
        </div>
      </div>

      <div className="tab-content">
        {activeTab === 'groups' && (
          <UserGroups groups={userGroups} />
        )}
        
        {activeTab === 'credits' && (
          <UserCredits 
            credits={userCredits} 
            onApproveCredit={handleApproveCredit} 
          />
        )}
        
        {activeTab === 'debts' && (
          <UserDebts 
            debts={userDebts} 
            onReturnCredit={handleReturnCredit} 
          />
        )}
        
        {activeTab === 'invites' && (
          <UserInvites 
            invites={userInvites} 
            onApprove={handleApproveInvite} 
            onReject={handleRejectInvite} 
          />
        )}
      </div>
    </div>
  );
};

export default Profile;
