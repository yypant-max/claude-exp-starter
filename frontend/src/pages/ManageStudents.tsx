import { useEffect, useState, FormEvent } from 'react';
import { getStudents, addStudent, updateStudent, removeStudent, Profile } from '../api/client';

export default function ManageStudents() {
  const [students, setStudents] = useState<Profile[]>([]);
  const [showAddForm, setShowAddForm] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);

  // Add form fields
  const [newEmail, setNewEmail] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [newName, setNewName] = useState('');

  // Edit form fields
  const [editEmail, setEditEmail] = useState('');
  const [editPassword, setEditPassword] = useState('');

  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  const loadStudents = () => {
    getStudents().then((res) => setStudents(res.data));
  };

  useEffect(() => {
    loadStudents();
  }, []);

  const handleAdd = async (e: FormEvent) => {
    e.preventDefault();
    setError('');
    setMessage('');
    try {
      await addStudent({ email: newEmail, password: newPassword, name: newName || undefined });
      setMessage('Student added successfully');
      setNewEmail('');
      setNewPassword('');
      setNewName('');
      setShowAddForm(false);
      loadStudents();
    } catch (err: any) {
      setError(err.response?.data?.error || 'Failed to add student');
    }
  };

  const handleEdit = async (e: FormEvent, id: number) => {
    e.preventDefault();
    setError('');
    setMessage('');
    const data: { email?: string; password?: string } = {};
    if (editEmail) data.email = editEmail;
    if (editPassword) data.password = editPassword;
    try {
      await updateStudent(id, data);
      setMessage('Student updated successfully');
      setEditingId(null);
      loadStudents();
    } catch (err: any) {
      setError(err.response?.data?.error || 'Failed to update student');
    }
  };

  const handleRemove = async (id: number) => {
    if (!confirm('Are you sure you want to remove this student?')) return;
    setError('');
    setMessage('');
    try {
      await removeStudent(id);
      setMessage('Student removed successfully');
      loadStudents();
    } catch (err: any) {
      setError(err.response?.data?.error || 'Failed to remove student');
    }
  };

  const startEdit = (student: Profile) => {
    setEditingId(student.id);
    setEditEmail(student.email);
    setEditPassword('');
  };

  return (
    <div className="page">
      <div className="page-header">
        <h1>Manage Students</h1>
        <button className="btn btn-primary" onClick={() => setShowAddForm(!showAddForm)}>
          {showAddForm ? 'Cancel' : 'Add Student'}
        </button>
      </div>

      {message && <div className="alert alert-success">{message}</div>}
      {error && <div className="alert alert-error">{error}</div>}

      {showAddForm && (
        <form onSubmit={handleAdd} className="form card">
          <h3>Add New Student</h3>
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
          <button type="submit" className="btn btn-primary">Add Student</button>
        </form>
      )}

      {students.length === 0 ? (
        <p className="empty-state">No students found. Add one to get started.</p>
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
            {students.map((student) => (
              <tr key={student.id}>
                {editingId === student.id ? (
                  <>
                    <td>{student.id}</td>
                    <td>{student.name || '-'}</td>
                    <td>
                      <form onSubmit={(e) => handleEdit(e, student.id)} className="inline-form">
                        <input type="email" value={editEmail} onChange={(e) => setEditEmail(e.target.value)} placeholder="Email" />
                        <input type="password" value={editPassword} onChange={(e) => setEditPassword(e.target.value)} placeholder="New password (optional)" />
                        <button type="submit" className="btn btn-small btn-primary">Save</button>
                        <button type="button" className="btn btn-small btn-secondary" onClick={() => setEditingId(null)}>Cancel</button>
                      </form>
                    </td>
                    <td>{new Date(student.createdAt).toLocaleDateString()}</td>
                    <td></td>
                  </>
                ) : (
                  <>
                    <td>{student.id}</td>
                    <td>{student.name || '-'}</td>
                    <td>{student.email}</td>
                    <td>{new Date(student.createdAt).toLocaleDateString()}</td>
                    <td>
                      <button className="btn btn-small btn-secondary" onClick={() => startEdit(student)}>Edit</button>
                      <button className="btn btn-small btn-danger" onClick={() => handleRemove(student.id)}>Remove</button>
                    </td>
                  </>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
