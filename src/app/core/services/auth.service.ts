import { Injectable, signal, Inject } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { delay, tap } from 'rxjs/operators';
import { User, LoginDto, AuthResponse } from '../models/user.model';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  // Mock user data
  private readonly MOCK_USER: User = {
    id: '1',
    username: 'admin',
    email: 'admin@cafeteriasoma.com',
    role: 'ADMIN',
    createdAt: new Date()
  };

  private readonly MOCK_PASSWORD = 'admin123';

  // State management
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  // Signal para nuevo enfoque de Angular
  public isAuthenticated = signal<boolean>(false);

  // If you provided API_BASE_URL token in appConfig, it will be injected here.
  constructor(private router: Router, private http: HttpClient, @Inject('API_BASE_URL') private BASE_URL: string) {
    // Migrar usuarios antiguos y cargar usuario desde localStorage si existe
    this.migrateUsersStorage();
    this.loadUserFromStorage();
  }

  // Mock register: store a basic client user (not ADMIN)
  register(dto: { fullName: string; email: string; password: string; phone?: string; address?: string; }) {
    console.log('[AuthService.register] Starting registration with email:', dto.email);
    
    // Prevent creation of the preconfigured admin account via registration
    if (dto.email === this.MOCK_USER.email || dto.email === 'admin' || dto.email === this.MOCK_USER.username) {
      console.log('[AuthService.register] Admin email rejected');
      return throwError(() => new Error('No está permitido registrar la cuenta de administrador')).pipe(delay(400));
    }

    const user: User = {
      id: Date.now().toString(),
      username: dto.email,
      email: dto.email,
      fullName: dto.fullName,
      role: 'CLIENT',
      createdAt: new Date()
    };

    console.log('[AuthService.register] Created user object:', user);

    // Create a mock auth response and persist the registered user and credentials
    const resp: AuthResponse = { user, token: 'mock-reg-token-' + Date.now() };

    // Persist user credentials (mock) into a 'users' list in localStorage
    const raw = localStorage.getItem('users');
    const users: Array<any> = raw ? JSON.parse(raw) : [];
    users.push({ email: dto.email, password: dto.password, user });
    localStorage.setItem('users', JSON.stringify(users));
    console.log('[AuthService.register] User persisted to localStorage');

    // If a real backend is configured, forward the registration to the backend.
    if (this.BASE_URL) {
      try {
        return this.http.post<AuthResponse>(`${this.BASE_URL}/auth/register`, dto).pipe(
          tap(r => this.setSession(r))
        );
      } catch (e) {
        // Fallback to mock behavior if request fails synchronously (rare)
        console.warn('[AuthService.register] Backend call failed, falling back to mock', e);
      }
    }

    // Fallback mock registration (no backend)
    return of(resp).pipe(
      delay(800),
      tap(r => {
        console.log('[AuthService.register] Calling setSession with response:', r);
        this.setSession(r);
      })
    );
  }

  login(credentials: LoginDto): Observable<AuthResponse> {
    // If a backend API base url is provided, call real auth endpoint
    if (this.BASE_URL) {
      return this.http.post<AuthResponse>(`${this.BASE_URL}/auth/login`, credentials).pipe(
        tap(res => this.setSession(res))
      );
    }

    // --- Mock behavior when no backend is configured ---
    // Admin shortcut
    if (credentials.username === 'admin' && credentials.password === this.MOCK_PASSWORD) {
      const response: AuthResponse = {
        user: this.MOCK_USER,
        token: 'mock-jwt-token-' + Date.now()
      };

      return of(response).pipe(
        delay(1000), // Simular latencia de red
        tap(res => {
          this.setSession(res);
        })
      );
    }

    // Check registered users in localStorage (mock)
    const raw = localStorage.getItem('users');
    if (raw) {
      try {
        const users = JSON.parse(raw) as Array<any>;
        const found = users.find(u => (u.email === credentials.username || u.user?.username === credentials.username) && u.password === credentials.password);
        if (found) {
          // ensure fullName exists
          const ensuredUser = this.ensureFullName(found.user);
          // persist back if changed
          if (ensuredUser !== found.user) {
            found.user = ensuredUser;
            localStorage.setItem('users', JSON.stringify(users));
          }
          const response: AuthResponse = {
            user: ensuredUser,
            token: 'mock-jwt-token-' + Date.now()
          };

          return of(response).pipe(delay(800), tap(res => this.setSession(res)));
        }
      } catch (e) {
        // ignore parse errors
      }
    }
    return throwError(() => new Error('Credenciales inválidas')).pipe(delay(1000));
  }

  logout(): void {
    localStorage.removeItem('currentUser');
    localStorage.removeItem('token');
    this.currentUserSubject.next(null);
    this.isAuthenticated.set(false);
    this.router.navigate(['/login']);
  }

  private setSession(authResponse: AuthResponse): void {
    localStorage.setItem('currentUser', JSON.stringify(authResponse.user));
    localStorage.setItem('token', authResponse.token);
    this.currentUserSubject.next(authResponse.user);
    this.isAuthenticated.set(true);
  }

  private loadUserFromStorage(): void {
    const userJson = localStorage.getItem('currentUser');
    const token = localStorage.getItem('token');
    
    if (userJson && token) {
      const user = JSON.parse(userJson);
      this.currentUserSubject.next(user);
      this.isAuthenticated.set(true);
    }
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  // --- migration helpers ---
  private migrateUsersStorage(): void {
    try {
      const raw = localStorage.getItem('users');
      if (raw) {
        let users = JSON.parse(raw) as Array<any>;
        let changed = false;
        users = users.map(entry => {
          if (entry && entry.user) {
            const ensured = this.ensureFullName(entry.user);
            if (ensured !== entry.user) {
              changed = true;
              return { ...entry, user: ensured };
            }
          }
          return entry;
        });
        if (changed) {
          localStorage.setItem('users', JSON.stringify(users));
        }
      }

      // also ensure currentUser has fullName
      const cu = localStorage.getItem('currentUser');
      if (cu) {
        const user = JSON.parse(cu);
        const ensured = this.ensureFullName(user);
        if (ensured !== user) {
          localStorage.setItem('currentUser', JSON.stringify(ensured));
        }
      }
    } catch {
      // ignore
    }
  }

  private ensureFullName(user: User): User {
    if (user && (!user.fullName || user.fullName.trim().length === 0)) {
      const guessed = user.email ? this.guessFullNameFromEmail(user.email) : (user.username || '');
      return { ...user, fullName: guessed };
    }
    return user;
  }

  private guessFullNameFromEmail(email: string): string {
    const local = (email || '').split('@')[0];
    const parts = local.split(/[._-]+/).filter(Boolean);
    if (parts.length === 0) return email || '';
    return parts
      .map(p => p.length ? p.charAt(0).toUpperCase() + p.slice(1).toLowerCase() : p)
      .join(' ');
  }
}