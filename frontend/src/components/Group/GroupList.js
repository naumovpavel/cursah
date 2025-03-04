import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import GroupService from '../../services/group.service';
import Loading from '../Common/Loading';
import ErrorMessage from '../Common/ErrorMessage';

const GroupList = () => {
  const [groups, setGroups] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchGroups = async () => {
      try {
        const data = await GroupService.getAllGroups();
        setGroups(data);
      } catch (err) {
        setError('Не удалось загрузить список групп.');
      } finally {
        setLoading(false);
      }
    };

    fetchGroups();
  }, []);

  if (loading) return <Loading />;
  if (error) return <ErrorMessage message={error} />;

  return (
    <div className="group-list-container">
      <div className="group-list-header">
        <h1>Мои группы</h1>
        <Link to="/groups/new" className="btn btn-primary">
          Создать группу
        </Link>
      </div>

      {groups.length === 0 ? (
        <div className="no-groups">
          <p>У вас пока нет групп. Создайте новую группу, чтобы начать.</p>
        </div>
      ) : (
        <div className="group-cards">
          {groups.map((group) => (
            <div key={group.id} className="card group-card">
              <h3 className="group-name">{group.name}</h3>
              <p className="group-description">{group.description || 'Без описания'}</p>
              {group.closedAt && (
                <p className="group-closed">
                  Закрыта: {new Date(group.closedAt).toLocaleDateString()}
                </p>
              )}
              <Link to={`/groups/${group.id}`} className="btn btn-secondary">
                Открыть
              </Link>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default GroupList;
