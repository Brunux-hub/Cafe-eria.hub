export class User {
}

export interface User {
  id: string;
  username: string;
  email: string;
  fullName?: string;
  role: 'ADMIN' | 'EMPLOYEE' | 'CLIENT';
  createdAt: Date;
}

export interface LoginDto {
  username: string;
  password: string;
}

export interface AuthResponse {
  user: User;
  token: string;
}