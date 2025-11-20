import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpInterceptor,
  HttpHandler,
  HttpRequest,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';

/**
 * Interceptor para manejar errores HTTP globalmente.
 */
@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  constructor(private router: Router) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        let errorMessage = 'Ocurrió un error desconocido';

        if (error.error instanceof ErrorEvent) {
          // Error del lado del cliente
          errorMessage = `Error: ${error.error.message}`;
        } else {
          // Error del lado del servidor
          switch (error.status) {
            case 401:
              errorMessage = 'No autorizado. Por favor inicia sesión.';
              // Limpiar sesión y redirigir al login
              localStorage.removeItem('currentUser');
              localStorage.removeItem('token');
              this.router.navigate(['/login']);
              break;
            case 403:
              errorMessage = 'Acceso denegado. No tienes permisos suficientes.';
              break;
            case 404:
              errorMessage = 'Recurso no encontrado.';
              break;
            case 500:
              errorMessage = 'Error interno del servidor.';
              break;
            default:
              errorMessage = error.error?.message || `Error: ${error.status} - ${error.statusText}`;
          }
        }

        console.error('HTTP Error:', errorMessage, error);
        return throwError(() => new Error(errorMessage));
      })
    );
  }
}
