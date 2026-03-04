import { describe, it, expect, beforeEach } from 'vitest';
import { renderHook, act } from '@testing-library/react';
import { ReactNode } from 'react';
import { AuthProvider, useAuth } from './AuthContext';

function wrapper({ children }: { children: ReactNode }) {
  return <AuthProvider>{children}</AuthProvider>;
}

describe('AuthContext', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it('should initialize with null user when localStorage is empty', () => {
    const { result } = renderHook(() => useAuth(), { wrapper });
    expect(result.current.user).toBeNull();
    expect(result.current.isAdmin).toBe(false);
    expect(result.current.isStudent).toBe(false);
  });

  it('should load user from localStorage on init', () => {
    const user = { id: 1, email: 'admin@sms.com', name: 'Admin', role: 'ADMIN', token: 'tok' };
    localStorage.setItem('user', JSON.stringify(user));

    const { result } = renderHook(() => useAuth(), { wrapper });
    expect(result.current.user).toEqual(user);
    expect(result.current.isAdmin).toBe(true);
    expect(result.current.isStudent).toBe(false);
  });

  it('should set user and persist to localStorage', () => {
    const { result } = renderHook(() => useAuth(), { wrapper });
    const user = { id: 2, email: 'student@sms.com', name: 'Student', role: 'STUDENT', token: 'tok' };

    act(() => {
      result.current.setUser(user);
    });

    expect(result.current.user).toEqual(user);
    expect(result.current.isStudent).toBe(true);
    expect(localStorage.getItem('token')).toBe('tok');
    expect(JSON.parse(localStorage.getItem('user')!)).toEqual(user);
  });

  it('should clear user on logout', () => {
    const user = { id: 1, email: 'admin@sms.com', name: 'Admin', role: 'ADMIN', token: 'tok' };
    localStorage.setItem('user', JSON.stringify(user));
    localStorage.setItem('token', 'tok');

    const { result } = renderHook(() => useAuth(), { wrapper });

    act(() => {
      result.current.logout();
    });

    expect(result.current.user).toBeNull();
    expect(localStorage.getItem('user')).toBeNull();
    expect(localStorage.getItem('token')).toBeNull();
  });

  it('should throw when useAuth is used outside AuthProvider', () => {
    expect(() => {
      renderHook(() => useAuth());
    }).toThrow('useAuth must be used within an AuthProvider');
  });
});
