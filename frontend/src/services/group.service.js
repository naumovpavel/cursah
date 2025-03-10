import api from './api';

const GroupService = {
  getAllGroups: async () => {
    const response = await api.get('/api/users/groups');
    return response.data;
  },

  getGroupById: async (groupId) => {
    const response = await api.get(`/api/groups/${groupId}`);
    return response.data;
  },

  createGroup: async (groupData) => {
    const response = await api.post('/api/groups/new', groupData);
    return response.data;
  },

  inviteUser: async (inviteData) => {
    const response = await api.post('/api/groups/invite', inviteData);
    return response.data;
  },

  getGroupExpenses: async (groupId) => {
    const response = await api.get(`/api/groups/${groupId}/expense`);
    return response.data;
  },

  closeGroup: async (groupId) => {
    const response = await api.post(`/api/groups/close/${groupId}`);
    return response.data;
  },

  setAcknowledgmentAllExpenses: async (groupId) => {
    const response = await api.post(`/api/groups/setAcknowledgmentAllExpenses/${groupId}`);
    return response.data;
  },

  setAcknowledgmentAllExpenseParticipantense: async (groupId) => {
    const response = await api.post(`/api/groups/setAcknowledgmentAllExpenseParticipantense/${groupId}`);
    return response.data;
  },

  getAcknowledgments: async (groupId) => {
    const response = await api.get(`/api/groups/getAcknowledgments/${groupId}`);
    return response.data;
  },

  setPaidBy: async (groupId, userId) => {
    const response = await api.post(`/api/groups/${groupId}/setPaidBy/${userId}`);
    return response.data;
  }
};

export default GroupService;
