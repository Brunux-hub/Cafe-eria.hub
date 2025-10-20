import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'admin',
    loadComponent: () => import('./shared/layout/main-layout/main-layout.component').then(m => m.MainLayoutComponent),
    children: [
      {
        path: 'dashboard',
        loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent)
      },
      {
        path: 'products',
        loadComponent: () => import('./features/products/product-list/product-list.component').then(m => m.ProductListComponent)
      },
      {
        path: 'sales',
        loadComponent: () => import('./features/sales/sales-list/sales-list.component').then(m => m.SalesListComponent)
      },
      {
        path: 'promotions',
        loadComponent: () => import('./features/promotions/promotion-list/promotion-list.component').then(m => m.PromotionListComponent)
      },
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      }
    ]
  },
  {
    path: 'client',
    loadComponent: () => import('./shared/layout/client-layout/client-layout.component').then(m => m.ClientLayoutComponent),
    children: [
      {
        path: 'catalog',
        loadComponent: () => import('./features/catalog/catalog.component').then(m => m.CatalogComponent)
      },
      {
        path: 'cart',
        loadComponent: () => import('./features/cart/cart.component').then(m => m.CartComponent)
      },
      {
        path: '',
        redirectTo: 'catalog',
        pathMatch: 'full'
      }
    ]
  },
  {
    path: '',
    redirectTo: '/login',
    pathMatch: 'full'
  },
  {
    path: '**',
    redirectTo: '/login'
  }
];