import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { getStudents, getAdmins, Profile } from '../api/client';

export default function AdminDashboard() {
  const { user } = useAuth();
  const [studentCount, setStudentCount] = useState(0);
  const [adminCount, setAdminCount] = useState(0);
  const [recentStudents, setRecentStudents] = useState<Profile[]>([]);

  useEffect(() => {
    getStudents().then((res) => {
      setStudentCount(res.data.length);
      setRecentStudents(res.data.slice(-5).reverse());
    });
    getAdmins().then((res) => setAdminCount(res.data.length));
  }, []);

  return (
    <div className="page">
      <h1>Admin Dashboard</h1>
      <p>Welcome, {user?.name || user?.email}!</p>

      <div className="stats-grid">
        <div className="stat-card">
          <h3>{studentCount}</h3>
          <p>Total Students</p>
          <Link to="/admin/students">Manage Students</Link>
        </div>
        <div className="stat-card">
          <h3>{adminCount}</h3>
          <p>Total Admins</p>
          <Link to="/admin/admins">Manage Admins</Link>
        </div>
      </div>

      {recentStudents.length > 0 && (
        <div className="section">
          <h2>Recently Added Students</h2>
          <table className="table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Added</th>
              </tr>
            </thead>
            <tbody>
              {recentStudents.map((s) => (
                <tr key={s.id}>
                  <td>{s.name || '-'}</td>
                  <td>{s.email}</td>
                  <td>{new Date(s.createdAt).toLocaleDateString()}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
