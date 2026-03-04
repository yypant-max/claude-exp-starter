import { describe, it, expect, beforeEach, vi } from 'vitest';
import { screen, waitFor } from '@testing-library/react';
import StudentDashboard from './StudentDashboard';
import { renderWithProviders, setLoggedInUser, clearAuth, mockStudentUser } from '../test/helpers';

vi.mock('../api/client', () => ({
  getStudentProfile: vi.fn(),
}));

import { getStudentProfile } from '../api/client';

describe('StudentDashboard', () => {
  beforeEach(() => {
    clearAuth();
    vi.clearAllMocks();
    setLoggedInUser(mockStudentUser);
  });

  it('should display welcome message', () => {
    (getStudentProfile as ReturnType<typeof vi.fn>).mockResolvedValue({
      data: { id: 2, email: 'student@sms.com', name: 'Student', description: '', createdAt: '2024-01-01' },
    });

    renderWithProviders(<StudentDashboard />);
    expect(screen.getByText('Student Dashboard')).toBeInTheDocument();
    expect(screen.getByText(/Welcome, Student/)).toBeInTheDocument();
  });

  it('should display profile details', async () => {
    (getStudentProfile as ReturnType<typeof vi.fn>).mockResolvedValue({
      data: {
        id: 2,
        email: 'student@sms.com',
        name: 'Test Student',
        description: 'A test student',
        createdAt: '2024-01-01',
      },
    });

    renderWithProviders(<StudentDashboard />);

    await waitFor(() => {
      expect(screen.getByText('student@sms.com')).toBeInTheDocument();
      expect(screen.getByText('Test Student')).toBeInTheDocument();
      expect(screen.getByText('A test student')).toBeInTheDocument();
    });
  });

  it('should show Edit Profile link', async () => {
    (getStudentProfile as ReturnType<typeof vi.fn>).mockResolvedValue({
      data: { id: 2, email: 'student@sms.com', name: 'Student', description: '', createdAt: '2024-01-01' },
    });

    renderWithProviders(<StudentDashboard />);

    await waitFor(() => {
      expect(screen.getByText('Edit Profile')).toBeInTheDocument();
    });
  });
});
