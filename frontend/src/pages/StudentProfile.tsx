import { useEffect, useState, FormEvent } from 'react';
import { getStudentProfile, updateStudentProfile, Profile } from '../api/client';

export default function StudentProfile() {
  const [profile, setProfile] = useState<Profile | null>(null);
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    getStudentProfile().then((res) => {
      setProfile(res.data);
      setName(res.data.name || '');
      setDescription(res.data.description || '');
    });
  }, []);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setMessage('');
    setError('');
    try {
      const res = await updateStudentProfile({ name, description });
      setProfile(res.data);
      setMessage('Profile updated successfully');
    } catch (err: any) {
      setError(err.response?.data?.error || 'Failed to update profile');
    }
  };

  if (!profile) return <div className="page">Loading...</div>;

  return (
    <div className="page">
      <h1>My Profile</h1>
      <div className="card">
        <p><strong>Email:</strong> {profile.email}</p>
        <p><strong>Member since:</strong> {new Date(profile.createdAt).toLocaleDateString()}</p>
      </div>

      {message && <div className="alert alert-success">{message}</div>}
      {error && <div className="alert alert-error">{error}</div>}

      <form onSubmit={handleSubmit} className="form">
        <div className="form-group">
          <label htmlFor="name">Name</label>
          <input id="name" type="text" value={name} onChange={(e) => setName(e.target.value)} />
        </div>
        <div className="form-group">
          <label htmlFor="description">Description</label>
          <textarea
            id="description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            rows={4}
            placeholder="Tell us about yourself..."
          />
        </div>
        <button type="submit" className="btn btn-primary">Save Changes</button>
      </form>
    </div>
  );
}
