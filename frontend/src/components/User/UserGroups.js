import React from 'react';
import { Link } from 'react-router-dom';

const UserGroups = ({ groups }) => {
  if (groups.length === 0) {
    return (
      <div className="no-data card">
        <p>У вас пока нет групп.</p>
        <Link to="/" className="btn btn-primary">Создать группу</Link>
      </div>
    );
  }

  return (
    <div className="user-groups">
      {groups.map(group => (
        <div key={group.id} className="group-item card">
          <div className="group-header">
            <h3>{group.name}</h3>
            {group.closedAt && (
              <span className="group-status">Закрыта</span>
            )}
          </div>
          
          {group.description && (
            <p className="group-description">{group.description}</p>
          )}
          
          {group.closedAt && (
            <p className="group-closed-date">
              Закрыта: {new Date(group.closedAt).toLocaleDateString()}
            </p>
          )}
          
          <Link to={`/groups/${group.id}`} className="btn btn-secondary">
            Открыть
          </Link>
        </div>
      ))}
    </div>
  );
};

export default UserGroups;
