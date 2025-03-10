import React, { useState, useEffect } from 'react';
import Select from 'react-select';
import UserService from '../../services/user.service';

const GroupUserSelect = ({ onChange, value, currentUserId, excludeUsers = [] }) => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        setLoading(true);
        setError(null);
        const userData = await UserService.getAllUsers();
        setUsers(userData || []);
      } catch (err) {
        setError(err.response.data.message);
        setUsers([]);
      } finally {
        setLoading(false);
      }
    };

    fetchUsers();
  }, []);

  const userOptions = users && users.length > 0 
    ? users
        .filter(user => 
          user.id !== currentUserId && 
          !excludeUsers.includes(user.id)
        )
        .map(user => ({
          value: user.id,
          label: user.username
        }))
    : [];

  return (
    <div className="group-user-select">
      <Select
        isMulti
        options={userOptions}
        value={value}
        onChange={onChange}
        isLoading={loading}
        placeholder={loading ? "Загрузка пользователей..." : "Выберите пользователей..."}
        noOptionsMessage={() => error || "Нет доступных пользователей"}
        className="react-select-container"
        classNamePrefix="react-select"
      />
      {error && <div className="error-message">{error}</div>}
    </div>
  );
};

export default GroupUserSelect;