import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';
import { CartService } from '../../../core/services/cart.service';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-client-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './client-navbar.component.html',
  styleUrls: ['./client-navbar.component.scss']
})
export class ClientNavbarComponent {
  private authService = inject(AuthService);
  private router = inject(Router);
  private cartService = inject(CartService);

  currentUser = this.authService.getCurrentUser();
  cartCount$ = this.cartService.count$;

  logout() {
    this.authService.logout();
  }
}
