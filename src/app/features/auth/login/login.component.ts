import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  loginForm: FormGroup;
  isLoading = false;
  errorMessage = '';
  // register
  showRegister = false;
  registerForm: FormGroup;
  registerLoading = false;
  registerMessage = '';

  constructor() {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });

    this.registerForm = this.fb.group({
      fullName: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirm: ['', [Validators.required]],
      phone: [''],
      address: ['']
    }, { validators: this.passwordsMatch });
  }

  private passwordsMatch(group: FormGroup) {
    const pw = group.get('password')?.value;
    const cf = group.get('confirm')?.value;
    return pw && cf && pw !== cf ? { mismatch: true } : null;
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

      this.authService.login(this.loginForm.value).subscribe({
        next: (res) => {
          // use response user directly to decide redirect
          const role = (res && res.user && res.user.role) ? res.user.role : this.authService.getCurrentUser()?.role;
          if (role === 'ADMIN') this.router.navigate(['/admin/products']);
          else this.router.navigate(['/client/catalog']);
      },
      error: (error: any) => {
        this.errorMessage = error.message || 'Error al iniciar sesión';
        this.isLoading = false;
      }
    });
  }

  toggleRegister(show?: boolean) {
    if (typeof show === 'boolean') this.showRegister = show;
    else this.showRegister = !this.showRegister;
  }

  onRegister() {
    console.log('[LoginComponent.onRegister] Button clicked');
    console.log('[LoginComponent.onRegister] registerLoading:', this.registerLoading);
    console.log('[LoginComponent.onRegister] registerForm.valid:', this.registerForm.valid);
    
    // Prevent duplicate submissions
    if (this.registerLoading) {
      console.log('[LoginComponent.onRegister] Already loading, skipping duplicate submission');
      return;
    }
    
    if (this.registerForm.invalid) {
      console.log('[LoginComponent.onRegister] Form invalid. Marking all as touched');
      this.registerForm.markAllAsTouched();
      this.registerMessage = 'Por favor completa todos los campos requeridos.';
      console.warn('Register form invalid', {
        valid: this.registerForm.valid,
        errors: this.registerForm.errors,
        controls: Object.keys(this.registerForm.controls).reduce((acc, key) => {
          acc[key] = this.registerForm.get(key)?.errors || null; return acc;
        }, {} as any)
      });
      return;
    }

    if (this.registerForm.value.password !== this.registerForm.value.confirm) {
      console.log('[LoginComponent.onRegister] Passwords do not match');
      this.registerMessage = 'Las contraseñas no coinciden';
      return;
    }

    console.log('[LoginComponent.onRegister] Form is valid. Starting register flow');
    this.registerLoading = true;
    this.registerMessage = '';

    console.log('[LoginComponent.onRegister] Calling authService.register()');
    // call mock register on auth service which auto-logins
    this.authService.register(this.registerForm.value).subscribe({ 
      next: (res) => {
        console.log('[LoginComponent.onRegister] Register success response:', res);
        this.registerLoading = false;
        // auto-login performed by AuthService.register via setSession
        // redirect clients to catalog
        const role = (res && res.user && res.user.role) ? res.user.role : this.authService.getCurrentUser()?.role;
        console.log('[LoginComponent.onRegister] Determined role:', role);
        if (role === 'ADMIN') {
          console.log('[LoginComponent.onRegister] Redirecting to /admin/products');
          this.router.navigate(['/admin/products']);
        } else {
          console.log('[LoginComponent.onRegister] Redirecting to /client/catalog');
          this.router.navigate(['/client/catalog']);
        }
      }, 
      error: (err) => {
        console.error('[LoginComponent.onRegister] Register error:', err);
        this.registerLoading = false;
        this.registerMessage = err?.message || 'Error al crear cuenta';
      }
    });
  }  get username() {
    return this.loginForm.get('username');
  }

  get password() {
    return this.loginForm.get('password');
  }
}