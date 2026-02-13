import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { getStudentProfile, Profile } from '../api/client';

export default function StudentDashboard() {
  const { user } = useAuth();
  const [profile, setProfile] = useState<Profile | null>(null);

  useEffect(() => {
    getStudentProfile().then((res) => setProfile(res.data));
  }, []);

  return (
    <div className="page">
      <h1>Student Dashboard</h1>
      <p>Welcome, {user?.name || user?.email}!</p>

      {profile && (
        <div className="card">
          <h2>Your Profile</h2>
          <div className="profile-details">
            <p><strong>Email:</strong> {profile.email}</p>
            <p><strong>Name:</strong> {profile.name || 'Not set'}</p>
            <p><strong>Description:</strong> {profile.description || 'Not set'}</p>
            <p><strong>Member since:</strong> {new Date(profile.createdAt).toLocaleDateString()}</p>
          </div>
          <Link to="/student/profile" className="btn btn-primary">Edit Profile</Link>
        </div>
      )}
    </div>
  );
}
