import React from 'react';
import { INVITE_STATUS } from '../../utils/constants';

const UserInvites = ({ invites, onApprove, onReject }) => {
  const pendingInvites = invites.filter(invite => invite.status === INVITE_STATUS.PENDING);
  
  if (invites.length === 0) {
    return (
      <div className="no-data card">
        <p>У вас нет приглашений.</p>
      </div>
    );
  }

  return (
    <div className="user-invites">
      <h3>Ожидающие ответа приглашения ({pendingInvites.length})</h3>
      
      {pendingInvites.length === 0 ? (
        <div className="no-data card">
          <p>У вас нет ожидающих приглашений.</p>
        </div>
      ) : (
        pendingInvites.map(invite => (
          <div key={invite.id} className="invite-item card">
            <div className="invite-content">
              <p>
                <strong>От пользователя:</strong> {invite.fromUser}
              </p>
              <p>
                <strong>Группа ID:</strong> {invite.groupId}
              </p>
            </div>
            
            <div className="invite-actions">
              <button 
                className="btn btn-primary"
                onClick={() => onApprove(invite.id)}
              >
                Принять
              </button>
              <button 
                className="btn btn-secondary"
                onClick={() => onReject(invite.id)}
              >
                Отклонить
              </button>
            </div>
          </div>
        ))
      )}
      
      <h3>История приглашений</h3>
      
      {invites.filter(invite => invite.status !== INVITE_STATUS.PENDING).length === 0 ? (
        <div className="no-data card">
          <p>История приглашений пуста.</p>
        </div>
      ) : (
        invites
          .filter(invite => invite.status !== INVITE_STATUS.PENDING)
          .map(invite => (
            <div key={invite.id} className="invite-item card">
              <div className="invite-content">
                <p>
                  <strong>От пользователя:</strong> {invite.fromUser}
                </p>
                <p>
                  <strong>Группа ID:</strong> {invite.groupId}
                </p>
                <p>
                  <strong>Статус:</strong> {
                    invite.status === INVITE_STATUS.ACCEPTED ? 'Принято' : 'Отклонено'
                  }
                </p>
              </div>
            </div>
          ))
      )}
    </div>
  );
};

export default UserInvites;
