import { useEffect, useState, FormEvent } from 'react';
import { useAuth } from '../context/AuthContext';
import { getAdmins, addAdmin, removeAdmin, Profile } from '../api/client';

export default function ManageAdmins() {
  const { user } = useAuth();
  const [admins, setAdmins] = useState<Profile[]>([]);
  const [showAddForm, setShowAddForm] = useState(false);
  const [newEmail, setNewEmail] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [newName, setNewName] = useState('');
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  const loadAdmins = () => {
    getAdmins().then((res) => setAdmins(res.data));
  };

  useEffect(() => {
    loadAdmins();
  }, []);

  const handleAdd = async (e: FormEvent) => {
    e.preventDefault();
    setError('');
    setMessage('');
    try {
      await addAdmin({ email: newEmail, password: newPassword, name: newName || undefined });
      setMessage('Admin added successfully');
      setNewEmail('');
      setNewPassword('');
      setNewName('');
      setShowAddForm(false);
      loadAdmins();
    } catch (err: any) {
      setError(err.response?.data?.error || 'Failed to add admin');
    }
  };

  const handleRemove = async (id: number) => {
    if (!confirm('Are you sure you want to remove this admin?')) return;
    setError('');
    setMessage('');
    try {
      await removeAdmin(id);
      setMessage('Admin removed successfully');
      loadAdmins();
    } catch (err: any) {
      setError(err.response?.data?.error || 'Failed to remove admin');
    }
  };

  return (
    <div className="page">
      <div className="page-header">
        <h1>Manage Admins</h1>
        <button className="btn btn-primary" onClick={() => setShowAddForm(!showAddForm)}>
          {showAddForm ? 'Cancel' : 'Add Admin'}
        </button>
      </div>

      {message && <div className="alert alert-success">{message}</div>}
      {error && <div className="alert alert-error">{error}</div>}

      {showAddForm && (
        <form onSubmit={handleAdd} className="form card">
          <h3>Add New Admin</h3>
          <div className="form-row">
            <div className="form-group">
              <label>Email *</label>
              <input type="email" value={newEmail} onChange={(e) => setNewEmail(e.target.value)} required />
            </div>
            <div className="form-group">
              <label>Password *</label>
              <input type="password" value={newPassword} onChange={(e) => setNewPassword(e.target.value)} required minLength={6} />
            </div>
            <div className="form-group">
              <label>Name</label>
              <input type="text" value={newName} onChange={(e) => setNewName(e.target.value)} />
            </div>
          </div>
          <button type="submit" className="btn btn-primary">Add Admin</button>
        </form>
      )}

      {admins.length === 0 ? (
        <p className="empty-state">No admins found.</p>
      ) : (
        <table className="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Email</th>
              <th>Created</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {admins.map((admin) => (
              <tr key={admin.id}>
                <td>{admin.id}</td>
                <td>{admin.name || '-'}</td>
                <td>{admin.email}</td>
                <td>{new Date(admin.createdAt).toLocaleDateString()}</td>
                <td>
                  {admin.email !== user?.email ? (
                    <button className="btn btn-small btn-danger" onClick={() => handleRemove(admin.id)}>Remove</button>
                  ) : (
                    <span className="badge">You</span>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
