import { createContext, useContext, useState, ReactNode } from 'react';
import { User } from '../api/client';

interface AuthContextType {
  user: User | null;
  setUser: (user: User | null) => void;
  logout: () => void;
  isAdmin: boolean;
  isStudent: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUserState] = useState<User | null>(() => {
    const stored = localStorage.getItem('user');
    return stored ? JSON.parse(stored) : null;
  });

  const setUser = (user: User | null) => {
    if (user) {
      localStorage.setItem('user', JSON.stringify(user));
      localStorage.setItem('token', user.token);
    } else {
      localStorage.removeItem('user');
      localStorage.removeItem('token');
    }
    setUserState(user);
  };

  const logout = () => setUser(null);

  return (
    <AuthContext.Provider
      value={{
        user,
        setUser,
        logout,
        isAdmin: user?.role === 'ADMIN',
        isStudent: user?.role === 'STUDENT',
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth(): AuthContextType {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
