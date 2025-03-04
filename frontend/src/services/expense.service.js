import api from './api';

const ExpenseService = {
  createExpense: async (expenseData) => {
    const response = await api.post('/api/expenses', expenseData);
    return response.data;
  },

  joinExpense: async (expenseId, userId = null) => {
    const url = userId 
      ? `/api/expenses/${expenseId}/join?userId=${userId}`
      : `/api/expenses/${expenseId}/join`;
    const response = await api.post(url);
    return response.data;
  },

  confirmExpense: async (expenseId) => {
    const response = await api.post(`/api/expenses/${expenseId}/confirm`);
    return response.data;
  },

  rejectExpense: async (expenseId) => {
    const response = await api.post(`/api/expenses/${expenseId}/reject`);
    return response.data;
  },

  getExpenseParticipants: async (expenseId) => {
    const response = await api.get(`/api/expenses/${expenseId}/participants`);
    return response.data;
  }
};

export default ExpenseService;
