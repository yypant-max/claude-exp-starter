import { describe, it, expect, beforeEach } from 'vitest';
import { screen } from '@testing-library/react';
import Navbar from './Navbar';
import { renderWithProviders, setLoggedInUser, clearAuth, mockAdminUser, mockStudentUser } from '../test/helpers';

describe('Navbar', () => {
  beforeEach(() => {
    clearAuth();
  });

  it('should not render when user is not logged in', () => {
    const { container } = renderWithProviders(<Navbar />);
    expect(container.querySelector('nav')).toBeNull();
  });

  it('should show admin links for admin user', () => {
    setLoggedInUser(mockAdminUser);
    renderWithProviders(<Navbar />);

    expect(screen.getByText('Dashboard')).toBeInTheDocument();
    expect(screen.getByText('Profile')).toBeInTheDocument();
    expect(screen.getByText('Students')).toBeInTheDocument();
    expect(screen.getByText('Admins')).toBeInTheDocument();
    expect(screen.getByText('Logout')).toBeInTheDocument();
  });

  it('should show student links for student user', () => {
    setLoggedInUser(mockStudentUser);
    renderWithProviders(<Navbar />);

    expect(screen.getByText('Dashboard')).toBeInTheDocument();
    expect(screen.getByText('Profile')).toBeInTheDocument();
    expect(screen.queryByText('Students')).not.toBeInTheDocument();
    expect(screen.queryByText('Admins')).not.toBeInTheDocument();
  });

  it('should display user name', () => {
    setLoggedInUser(mockAdminUser);
    renderWithProviders(<Navbar />);

    expect(screen.getByText('Admin')).toBeInTheDocument();
  });
});
