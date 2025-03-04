import api from './api';

const UserService = {
  getAllUsers: async () => {
    const response = await api.get('/api/users');
    return response.data;
  },

  getUserById: async (userId) => {
    const response = await api.get(`/api/users/${userId}`);
    return response.data;
  },

  getGroups: async () => {
    const response = await api.get(`/api/users/groups`);
    return response.data;
  },

  getUserInvites: async () => {
    const response = await api.get('/api/users/myInvites');
    return response.data;
  },

  approveInvite: async (inviteId) => {
    const response = await api.post(`/api/users/myInvites/${inviteId}/approve`);
    return response.data;
  },

  rejectInvite: async (inviteId) => {
    const response = await api.post(`/api/users/myInvites/${inviteId}/reject`);
    return response.data;
  }
};

export default UserService;
