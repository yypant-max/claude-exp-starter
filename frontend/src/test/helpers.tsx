import { ReactNode } from 'react';
import { render } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { AuthProvider } from '../context/AuthContext';

export function renderWithProviders(ui: ReactNode) {
  return render(
    <BrowserRouter>
      <AuthProvider>
        {ui}
      </AuthProvider>
    </BrowserRouter>
  );
}

export const mockAdminUser = {
  id: 1,
  email: 'admin@sms.com',
  name: 'Admin',
  role: 'ADMIN',
  token: 'mock-jwt-token',
};

export const mockStudentUser = {
  id: 2,
  email: 'student@sms.com',
  name: 'Student',
  role: 'STUDENT',
  token: 'mock-jwt-token',
};

export function setLoggedInUser(user: typeof mockAdminUser) {
  localStorage.setItem('user', JSON.stringify(user));
  localStorage.setItem('token', user.token);
}

export function clearAuth() {
  localStorage.removeItem('user');
  localStorage.removeItem('token');
}
