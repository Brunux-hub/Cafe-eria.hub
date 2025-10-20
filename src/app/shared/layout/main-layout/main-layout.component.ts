import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Router, NavigationEnd } from '@angular/router';
import { NavbarComponent } from '../navbar/navbar.component';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProductService } from '../../../core/services/product.service';
import { UiService } from '../../../core/services/ui.service';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [CommonModule, RouterModule, NavbarComponent, FormsModule, ReactiveFormsModule],
  templateUrl: './main-layout.component.html',
  styleUrls: ['./main-layout.component.scss']
})
export class MainLayoutComponent {
  showNewProductModal = false;
  newProductForm: FormGroup;
  isProductRoute = false;
  editingProductId: string | null = null;

  constructor(private fb: FormBuilder, private productService: ProductService, private router: Router, private ui: UiService) {
    this.newProductForm = this.fb.group({
      name: ['', Validators.required],
      price: [0, [Validators.required, Validators.min(0.01)]],
      description: [''],
      category: ['Café Caliente'],
      imageUrl: [''],
      stock: [0, [Validators.required, Validators.min(0)]]
    });

    // watch route changes to determine if we are on products page
    this.isProductRoute = this.router.url.includes('/admin/products');
    this.router.events.subscribe((ev) => {
      if (ev instanceof NavigationEnd) {
        this.isProductRoute = ev.urlAfterRedirects.includes('/admin/products');
      }
    });

    // subscribe to UI events
    this.ui.openNewProduct$.subscribe((open) => this.toggleNewProductModal(open));
    this.ui.editProduct$.subscribe((prod) => {
      if (prod) {
        // fill form and open modal in edit mode
        this.editingProductId = prod.id;
        this.newProductForm.patchValue({
          name: prod.name || '',
          price: prod.price ?? 0,
          description: prod.description || '',
          category: prod.category || 'Café Caliente',
          stock: prod.stock ?? 0,
          imageUrl: prod.image || ''
        });
        this.toggleNewProductModal(true);
      } else {
        this.editingProductId = null;
      }
    });
  }

  toggleNewProductModal(open?: boolean) {
    if (typeof open === 'boolean') {
      this.showNewProductModal = open;
    } else {
      this.showNewProductModal = !this.showNewProductModal;
    }
  }

  submitNewProduct() {
    if (this.newProductForm.invalid) return;
    const dto = this.newProductForm.value;
    if (this.editingProductId) {
      const updateDto = { id: this.editingProductId, ...dto };
      this.productService.updateProduct(updateDto).subscribe({ next: () => {
        this.toggleNewProductModal(false);
        this.newProductForm.reset({ name: '', price: 0, description: '', category: 'Café Caliente', stock: 0, imageUrl: '' });
        this.editingProductId = null;
      }});
    } else {
      this.productService.createProduct(dto).subscribe({ next: () => {
        this.toggleNewProductModal(false);
        this.newProductForm.reset({ name: '', price: 0, description: '', category: 'Café Caliente', stock: 0, imageUrl: '' });
      }});
    }
  }
}
