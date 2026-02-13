import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Navbar() {
  const { user, logout, isAdmin } = useAuth();
  const navigate = useNavigate();

  if (!user) return null;

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="navbar">
      <div className="navbar-brand">
        <Link to={isAdmin ? '/admin' : '/student'}>Student Management System</Link>
      </div>
      <div className="navbar-links">
        {isAdmin && (
          <>
            <Link to="/admin">Dashboard</Link>
            <Link to="/admin/profile">Profile</Link>
            <Link to="/admin/students">Students</Link>
            <Link to="/admin/admins">Admins</Link>
          </>
        )}
        {!isAdmin && (
          <>
            <Link to="/student">Dashboard</Link>
            <Link to="/student/profile">Profile</Link>
          </>
        )}
      </div>
      <div className="navbar-user">
        <span>{user.name || user.email}</span>
        <button onClick={handleLogout} className="btn btn-secondary">Logout</button>
      </div>
    </nav>
  );
}
