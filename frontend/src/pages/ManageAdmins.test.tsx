import { describe, it, expect, beforeEach, vi } from 'vitest';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import ManageAdmins from './ManageAdmins';
import { renderWithProviders, setLoggedInUser, clearAuth, mockAdminUser } from '../test/helpers';

vi.mock('../api/client', () => ({
  getAdmins: vi.fn(),
  addAdmin: vi.fn(),
  removeAdmin: vi.fn(),
}));

import { getAdmins, addAdmin } from '../api/client';

const mockAdmins = [
  { id: 1, email: 'admin@sms.com', name: 'Admin', createdAt: '2024-01-01' },
  { id: 2, email: 'admin2@sms.com', name: 'Admin Two', createdAt: '2024-01-02' },
];

describe('ManageAdmins', () => {
  beforeEach(() => {
    clearAuth();
    vi.clearAllMocks();
    setLoggedInUser(mockAdminUser);
    (getAdmins as ReturnType<typeof vi.fn>).mockResolvedValue({ data: mockAdmins });
  });

  it('should display page title', () => {
    renderWithProviders(<ManageAdmins />);
    expect(screen.getByText('Manage Admins')).toBeInTheDocument();
  });

  it('should display admin list', async () => {
    renderWithProviders(<ManageAdmins />);

    await waitFor(() => {
      expect(screen.getByText('admin@sms.com')).toBeInTheDocument();
      expect(screen.getByText('admin2@sms.com')).toBeInTheDocument();
    });
  });

  it('should show "You" badge for current admin', async () => {
    renderWithProviders(<ManageAdmins />);

    await waitFor(() => {
      expect(screen.getByText('You')).toBeInTheDocument();
    });
  });

  it('should show Remove button only for other admins', async () => {
    renderWithProviders(<ManageAdmins />);

    await waitFor(() => {
      const removeButtons = screen.getAllByText('Remove');
      expect(removeButtons).toHaveLength(1);
    });
  });

  it('should toggle add form', async () => {
    const user = userEvent.setup();
    renderWithProviders(<ManageAdmins />);

    await user.click(screen.getByText('Add Admin'));
    expect(screen.getByText('Add New Admin')).toBeInTheDocument();
  });

  it('should call addAdmin API on form submit', async () => {
    const user = userEvent.setup();
    (addAdmin as ReturnType<typeof vi.fn>).mockResolvedValue({
      data: { id: 3, email: 'new@sms.com', name: 'New' },
    });

    renderWithProviders(<ManageAdmins />);

    await waitFor(() => {
      expect(screen.getByText('admin@sms.com')).toBeInTheDocument();
    });

    await user.click(screen.getByText('Add Admin'));

    const form = screen.getByText('Add New Admin').closest('form')!;
    const inputs = form.querySelectorAll('input');
    const emailInput = inputs[0];
    const passwordInput = inputs[1];

    await user.type(emailInput, 'newadmin@sms.com');
    await user.type(passwordInput, 'password123');

    await user.click(screen.getByRole('button', { name: 'Add Admin' }));

    await waitFor(() => {
      expect(addAdmin).toHaveBeenCalled();
    });
  });
});
