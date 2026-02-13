import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './context/AuthContext';
import Navbar from './components/Navbar';
import ProtectedRoute from './components/ProtectedRoute';
import LoginPage from './pages/LoginPage';
import AdminDashboard from './pages/AdminDashboard';
import AdminProfile from './pages/AdminProfile';
import ManageStudents from './pages/ManageStudents';
import ManageAdmins from './pages/ManageAdmins';
import StudentDashboard from './pages/StudentDashboard';
import StudentProfile from './pages/StudentProfile';

export default function App() {
  const { user } = useAuth();

  return (
    <>
      <Navbar />
      <div className="container">
        <Routes>
          <Route
            path="/"
            element={
              user
                ? <Navigate to={user.role === 'ADMIN' ? '/admin' : '/student'} replace />
                : <Navigate to="/login" replace />
            }
          />
          <Route path="/login" element={<LoginPage />} />

          {/* Admin routes */}
          <Route path="/admin" element={<ProtectedRoute role="ADMIN"><AdminDashboard /></ProtectedRoute>} />
          <Route path="/admin/profile" element={<ProtectedRoute role="ADMIN"><AdminProfile /></ProtectedRoute>} />
          <Route path="/admin/students" element={<ProtectedRoute role="ADMIN"><ManageStudents /></ProtectedRoute>} />
          <Route path="/admin/admins" element={<ProtectedRoute role="ADMIN"><ManageAdmins /></ProtectedRoute>} />

          {/* Student routes */}
          <Route path="/student" element={<ProtectedRoute role="STUDENT"><StudentDashboard /></ProtectedRoute>} />
          <Route path="/student/profile" element={<ProtectedRoute role="STUDENT"><StudentProfile /></ProtectedRoute>} />

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </div>
    </>
  );
}
