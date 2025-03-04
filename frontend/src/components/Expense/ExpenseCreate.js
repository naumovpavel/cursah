import React from 'react';
import { Formik, Form, Field, ErrorMessage } from 'formik';
import * as Yup from 'yup';

const expenseSchema = Yup.object().shape({
  name: Yup.string()
    .required('Название траты обязательно')
    .min(2, 'Название должно содержать минимум 2 символа')
    .max(100, 'Название должно содержать максимум 100 символов'),
  value: Yup.number()
    .required('Сумма траты обязательна')
    .positive('Сумма должна быть положительной')
    .typeError('Сумма должна быть числом')
});

const ExpenseCreate = ({ onSubmit, onCancel }) => {
  return (
    <div className="expense-create">
      <h3>Добавить новую трату</h3>
      
      <Formik
        initialValues={{ name: '', value: '' }}
        validationSchema={expenseSchema}
        onSubmit={onSubmit}
      >
        {({ isSubmitting }) => (
          <Form>
            <div className="form-group">
              <label htmlFor="name">Название траты*</label>
              <Field 
                type="text" 
                name="name" 
                id="name" 
                className="form-control" 
                placeholder="Например: Продукты, Транспорт, Ресторан" 
              />
              <ErrorMessage name="name" component="div" className="error-message" />
            </div>

            <div className="form-group">
            <label htmlFor="value">Сумма траты, ₽*</label>
              <Field 
                type="number" 
                name="value" 
                id="value" 
                className="form-control" 
                placeholder="Введите сумму" 
              />
              <ErrorMessage name="value" component="div" className="error-message" />
            </div>

            <div className="form-actions">
              <button 
                type="button" 
                className="btn btn-secondary"
                onClick={onCancel}
              >
                Отмена
              </button>
              <button 
                type="submit" 
                className="btn btn-primary" 
                disabled={isSubmitting}
              >
                {isSubmitting ? 'Добавление...' : 'Добавить трату'}
              </button>
            </div>
          </Form>
        )}
      </Formik>
    </div>
  );
};

export default ExpenseCreate;