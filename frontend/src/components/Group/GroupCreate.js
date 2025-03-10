import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Formik, Form, Field, ErrorMessage as FormikErrorMessage } from 'formik';
import * as Yup from 'yup';
import GroupService from '../../services/group.service';
import { useAuth } from '../../contexts/AuthContext';
import GroupUserSelect from './GroupUserSelect';
import Loading from '../Common/Loading';
import ErrorMessage from '../Common/ErrorMessage';

const GroupCreate = () => {
  const navigate = useNavigate();
  const { currentUser } = useAuth();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [selectedUsers, setSelectedUsers] = useState([]);

  const initialValues = {
    name: '',
    description: ''
  };

  const validationSchema = Yup.object({
    name: Yup.string()
      .required('Название группы обязательно')
      .min(3, 'Название должно содержать минимум 3 символа')
      .max(50, 'Название не должно превышать 50 символов'),
    description: Yup.string()
      .max(200, 'Описание не должно превышать 200 символов')
  });

  const handleSubmit = async (values, { setSubmitting }) => {
    try {
      setLoading(true);
      setError(null);

      const groupData = {
        ...values,
        users: selectedUsers.map(user => user.value)
      };

      const response = await GroupService.createGroup(groupData);
      navigate(`/groups/${response.id}`);
    } catch (err) {
      setError(err.response.data.message);
    } finally {
      setLoading(false);
      setSubmitting(false);
    }
  };

  if (loading) return <Loading />;

  return (
    <div className="group-create-container">
      <div className="card">
        <h2>Создание новой группы</h2>
        
        {error && <ErrorMessage message={error} />}
        
        <Formik
          initialValues={initialValues}
          validationSchema={validationSchema}
          onSubmit={handleSubmit}
        >
          {({ isSubmitting }) => (
            <Form>
              <div className="form-group">
                <label htmlFor="name">Название группы*</label>
                <Field type="text" name="name" id="name" className="form-control" />
                <FormikErrorMessage name="name" component="div" className="error-message" />
              </div>
              
              <div className="form-group">
                <label htmlFor="description">Описание</label>
                <Field 
                  as="textarea" 
                  name="description" 
                  id="description" 
                  className="form-control" 
                  rows="3"
                />
                <FormikErrorMessage name="description" component="div" className="error-message" />
              </div>
              
              <div className="form-actions">
                <button 
                  type="button" 
                  className="btn btn-secondary"
                  onClick={() => navigate('/')}
                >
                  Отмена
                </button>
                <button 
                  type="submit" 
                  className="btn btn-primary" 
                  disabled={isSubmitting}
                >
                  {isSubmitting ? 'Создание...' : 'Создать группу'}
                </button>
              </div>
            </Form>
          )}
        </Formik>
      </div>
    </div>
  );
};

export default GroupCreate;
