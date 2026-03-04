import { describe, it, expect, beforeEach, vi } from 'vitest';
import { screen, waitFor } from '@testing-library/react';
import AdminDashboard from './AdminDashboard';
import { renderWithProviders, setLoggedInUser, clearAuth, mockAdminUser } from '../test/helpers';

vi.mock('../api/client', () => ({
  getStudents: vi.fn(),
  getAdmins: vi.fn(),
}));

import { getStudents, getAdmins } from '../api/client';

describe('AdminDashboard', () => {
  beforeEach(() => {
    clearAuth();
    vi.clearAllMocks();
    setLoggedInUser(mockAdminUser);
  });

  it('should display welcome message', async () => {
    (getStudents as ReturnType<typeof vi.fn>).mockResolvedValue({ data: [] });
    (getAdmins as ReturnType<typeof vi.fn>).mockResolvedValue({ data: [] });

    renderWithProviders(<AdminDashboard />);

    expect(screen.getByText('Admin Dashboard')).toBeInTheDocument();
    expect(screen.getByText(/Welcome, Admin/)).toBeInTheDocument();
  });

  it('should display student and admin counts', async () => {
    (getStudents as ReturnType<typeof vi.fn>).mockResolvedValue({
      data: [
        { id: 1, email: 's1@sms.com', name: 'S1', createdAt: '2024-01-01' },
        { id: 2, email: 's2@sms.com', name: 'S2', createdAt: '2024-01-02' },
      ],
    });
    (getAdmins as ReturnType<typeof vi.fn>).mockResolvedValue({
      data: [{ id: 1, email: 'admin@sms.com', name: 'Admin' }],
    });

    renderWithProviders(<AdminDashboard />);

    await waitFor(() => {
      expect(screen.getByText('2')).toBeInTheDocument();
      expect(screen.getByText('1')).toBeInTheDocument();
    });
  });

  it('should display recent students table', async () => {
    (getStudents as ReturnType<typeof vi.fn>).mockResolvedValue({
      data: [{ id: 1, email: 'student@sms.com', name: 'Test Student', createdAt: '2024-01-01' }],
    });
    (getAdmins as ReturnType<typeof vi.fn>).mockResolvedValue({ data: [] });

    renderWithProviders(<AdminDashboard />);

    await waitFor(() => {
      expect(screen.getByText('Recently Added Students')).toBeInTheDocument();
      expect(screen.getByText('Test Student')).toBeInTheDocument();
      expect(screen.getByText('student@sms.com')).toBeInTheDocument();
    });
  });
});
