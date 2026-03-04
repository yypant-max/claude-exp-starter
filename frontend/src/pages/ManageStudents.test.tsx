import { describe, it, expect, beforeEach, vi } from 'vitest';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import ManageStudents from './ManageStudents';
import { renderWithProviders, setLoggedInUser, clearAuth, mockAdminUser } from '../test/helpers';

vi.mock('../api/client', () => ({
  getStudents: vi.fn(),
  addStudent: vi.fn(),
  updateStudent: vi.fn(),
  removeStudent: vi.fn(),
}));

import { getStudents, addStudent, removeStudent } from '../api/client';

const mockStudents = [
  { id: 1, email: 'student1@sms.com', name: 'Student One', createdAt: '2024-01-01', updatedAt: '2024-01-01' },
  { id: 2, email: 'student2@sms.com', name: 'Student Two', createdAt: '2024-01-02', updatedAt: '2024-01-02' },
];

describe('ManageStudents', () => {
  beforeEach(() => {
    clearAuth();
    vi.clearAllMocks();
    setLoggedInUser(mockAdminUser);
    (getStudents as ReturnType<typeof vi.fn>).mockResolvedValue({ data: mockStudents });
  });

  it('should display page title and add button', async () => {
    renderWithProviders(<ManageStudents />);

    expect(screen.getByText('Manage Students')).toBeInTheDocument();
    expect(screen.getByText('Add Student')).toBeInTheDocument();
  });

  it('should display student list', async () => {
    renderWithProviders(<ManageStudents />);

    await waitFor(() => {
      expect(screen.getByText('student1@sms.com')).toBeInTheDocument();
      expect(screen.getByText('student2@sms.com')).toBeInTheDocument();
      expect(screen.getByText('Student One')).toBeInTheDocument();
    });
  });

  it('should show empty state when no students', async () => {
    (getStudents as ReturnType<typeof vi.fn>).mockResolvedValue({ data: [] });
    renderWithProviders(<ManageStudents />);

    await waitFor(() => {
      expect(screen.getByText(/No students found/)).toBeInTheDocument();
    });
  });

  it('should toggle add form when clicking Add Student', async () => {
    const user = userEvent.setup();
    renderWithProviders(<ManageStudents />);

    await user.click(screen.getByText('Add Student'));

    expect(screen.getByText('Add New Student')).toBeInTheDocument();

    await user.click(screen.getByText('Cancel'));

    expect(screen.queryByText('Add New Student')).not.toBeInTheDocument();
  });

  it('should call addStudent API when submitting add form', async () => {
    const user = userEvent.setup();
    (addStudent as ReturnType<typeof vi.fn>).mockResolvedValue({
      data: { id: 3, email: 'new@sms.com', name: 'New' },
    });

    renderWithProviders(<ManageStudents />);

    await waitFor(() => {
      expect(screen.getByText('student1@sms.com')).toBeInTheDocument();
    });

    await user.click(screen.getByText('Add Student'));

    const form = screen.getByText('Add New Student').closest('form')!;
    const inputs = form.querySelectorAll('input');
    const emailInput = inputs[0];
    const passwordInput = inputs[1];
    const nameInput = inputs[2];

    await user.type(emailInput, 'new@sms.com');
    await user.type(passwordInput, 'password123');
    await user.type(nameInput, 'New Student');

    await user.click(screen.getByRole('button', { name: 'Add Student' }));

    await waitFor(() => {
      expect(addStudent).toHaveBeenCalledWith({
        email: 'new@sms.com',
        password: 'password123',
        name: 'New Student',
      });
    });
  });

  it('should call removeStudent API on remove confirmation', async () => {
    const user = userEvent.setup();
    window.confirm = vi.fn(() => true);
    (removeStudent as ReturnType<typeof vi.fn>).mockResolvedValue({});

    renderWithProviders(<ManageStudents />);

    await waitFor(() => {
      expect(screen.getByText('student1@sms.com')).toBeInTheDocument();
    });

    const removeButtons = screen.getAllByText('Remove');
    await user.click(removeButtons[0]);

    await waitFor(() => {
      expect(removeStudent).toHaveBeenCalledWith(1);
    });
  });
});
