import React from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider } from "./contexts/AuthContext";
import PrivateRoute from "./utils/PrivateRoute";
import AuthPage from "./components/Auth/AuthPage";
import GroupList from "./components/Group/GroupList";
import GroupDetail from "./components/Group/GroupDetail";
import GroupCreate from "./components/Group/GroupCreate";
import Profile from "./components/User/Profile";
import Header from "./components/Common/Header";
import Footer from "./components/Common/Footer";
import "./assets/styles/global.css";

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="app">
          <Header />
          <main className="main-content">
            <Routes>
              <Route path="/auth" element={<AuthPage />} />
              <Route path="/" element={<PrivateRoute><GroupList /></PrivateRoute>} />
              <Route path="/groups/new" element={<PrivateRoute><GroupCreate /></PrivateRoute>} />
              <Route path="/groups/:groupId" element={<PrivateRoute><GroupDetail /></PrivateRoute>} />
              <Route path="/profile" element={<PrivateRoute><Profile /></PrivateRoute>} />
              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </main>
          <Footer />
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;
