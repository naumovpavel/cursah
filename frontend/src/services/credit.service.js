import api from './api';

const CreditService = {
  getCreditsTo: async (userId) => {
    const response = await api.get(`/api/credit/to/${userId}`);
    return response.data;
  },

  getCreditsFrom: async (userId) => {
    const response = await api.get(`/api/credit/from/${userId}`);
    return response.data;
  },

  returnCredit: async (creditId, amount) => {
    const amountStr = parseFloat(amount);
    const returnCreditRequest = { "amount": amountStr, "creditId": creditId };
    const response = await api.post(`/api/credit/return`, returnCreditRequest);
    return response.data;
  },

  approveCredit: async (creditId) => {
    const response = await api.post(`/api/credit/approve/${creditId}`);
    return response.data;
  }
};

export default CreditService;
