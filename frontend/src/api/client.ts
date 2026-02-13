import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 || error.response?.status === 403) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export interface User {
  id: number;
  email: string;
  name: string;
  role: string;
  token: string;
}

export interface Profile {
  id: number;
  email: string;
  name: string;
  description: string;
  createdAt: string;
  updatedAt: string;
}

// Auth
export const login = (email: string, password: string) =>
  api.post('/auth/login', { email, password });

// Admin profile
export const getAdminProfile = () => api.get<Profile>('/admin/profile');
export const updateAdminProfile = (data: { name?: string; description?: string }) =>
  api.put<Profile>('/admin/profile', data);

// Student management (admin)
export const getStudents = () => api.get<Profile[]>('/admin/students');
export const addStudent = (data: { email: string; password: string; name?: string }) =>
  api.post<Profile>('/admin/students', data);
export const updateStudent = (id: number, data: { email?: string; password?: string }) =>
  api.put<Profile>(`/admin/students/${id}`, data);
export const removeStudent = (id: number) => api.delete(`/admin/students/${id}`);

// Admin management (admin)
export const getAdmins = () => api.get<Profile[]>('/admin/admins');
export const addAdmin = (data: { email: string; password: string; name?: string }) =>
  api.post<Profile>('/admin/admins', data);
export const removeAdmin = (id: number) => api.delete(`/admin/admins/${id}`);

// Student profile
export const getStudentProfile = () => api.get<Profile>('/student/profile');
export const updateStudentProfile = (data: { name?: string; description?: string }) =>
  api.put<Profile>('/student/profile', data);

export default api;
