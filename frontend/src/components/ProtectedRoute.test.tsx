import { describe, it, expect, beforeEach } from 'vitest';
import { screen } from '@testing-library/react';
import ProtectedRoute from './ProtectedRoute';
import { renderWithProviders, setLoggedInUser, clearAuth, mockAdminUser, mockStudentUser } from '../test/helpers';

describe('ProtectedRoute', () => {
  beforeEach(() => {
    clearAuth();
  });

  it('should render children when user has correct role', () => {
    setLoggedInUser(mockAdminUser);
    renderWithProviders(
      <ProtectedRoute role="ADMIN">
        <div>Admin Content</div>
      </ProtectedRoute>
    );
    expect(screen.getByText('Admin Content')).toBeInTheDocument();
  });

  it('should redirect when user is not logged in', () => {
    renderWithProviders(
      <ProtectedRoute role="ADMIN">
        <div>Admin Content</div>
      </ProtectedRoute>
    );
    expect(screen.queryByText('Admin Content')).not.toBeInTheDocument();
  });

  it('should redirect when user has wrong role', () => {
    setLoggedInUser(mockStudentUser);
    renderWithProviders(
      <ProtectedRoute role="ADMIN">
        <div>Admin Content</div>
      </ProtectedRoute>
    );
    expect(screen.queryByText('Admin Content')).not.toBeInTheDocument();
  });
});
