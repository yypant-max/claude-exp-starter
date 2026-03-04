import { describe, it, expect, beforeEach, vi } from 'vitest';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import LoginPage from './LoginPage';
import { renderWithProviders, clearAuth, setLoggedInUser, mockAdminUser } from '../test/helpers';

vi.mock('../api/client', () => ({
  login: vi.fn(),
}));

import { login } from '../api/client';

describe('LoginPage', () => {
  beforeEach(() => {
    clearAuth();
    vi.clearAllMocks();
  });

  it('should render login form', () => {
    renderWithProviders(<LoginPage />);

    expect(screen.getByText('Student Management System')).toBeInTheDocument();
    expect(screen.getByText('Sign In')).toBeInTheDocument();
    expect(screen.getByLabelText('Email')).toBeInTheDocument();
    expect(screen.getByLabelText('Password')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Sign In' })).toBeInTheDocument();
  });

  it('should show default credentials hint', () => {
    renderWithProviders(<LoginPage />);
    expect(screen.getByText(/admin@sms.com/)).toBeInTheDocument();
  });

  it('should call login API on form submit', async () => {
    const user = userEvent.setup();
    (login as ReturnType<typeof vi.fn>).mockResolvedValue({
      data: { token: 'tok', role: 'ADMIN', id: 1, email: 'admin@sms.com', name: 'Admin' },
    });

    renderWithProviders(<LoginPage />);

    await user.type(screen.getByLabelText('Email'), 'admin@sms.com');
    await user.type(screen.getByLabelText('Password'), 'admin123');
    await user.click(screen.getByRole('button', { name: 'Sign In' }));

    await waitFor(() => {
      expect(login).toHaveBeenCalledWith('admin@sms.com', 'admin123');
    });
  });

  it('should show error message on login failure', async () => {
    const user = userEvent.setup();
    (login as ReturnType<typeof vi.fn>).mockRejectedValue({
      response: { data: { error: 'Invalid email or password' } },
    });

    renderWithProviders(<LoginPage />);

    await user.type(screen.getByLabelText('Email'), 'admin@sms.com');
    await user.type(screen.getByLabelText('Password'), 'wrong');
    await user.click(screen.getByRole('button', { name: 'Sign In' }));

    await waitFor(() => {
      expect(screen.getByText('Invalid email or password')).toBeInTheDocument();
    });
  });

  it('should redirect if user is already logged in', () => {
    setLoggedInUser(mockAdminUser);
    renderWithProviders(<LoginPage />);

    expect(screen.queryByText('Sign In')).not.toBeInTheDocument();
  });
});
