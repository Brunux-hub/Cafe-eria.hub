import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { ClientNavbarComponent } from '../client-navbar/client-navbar.component';

@Component({
  selector: 'app-client-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, ClientNavbarComponent],
  template: `
    <app-client-navbar></app-client-navbar>
    <div class="container mx-auto px-6 py-6">
      <router-outlet></router-outlet>
    </div>
  `,
  styles: []
})
export class ClientLayoutComponent {}
