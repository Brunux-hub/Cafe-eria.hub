import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../../core/services/product.service';
import { Product } from '../../../core/models/product.model';
import { Observable } from 'rxjs';
import { UiService } from '../../../core/services/ui.service';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.scss']
})
export class ProductListComponent implements OnInit {
  products$!: Observable<Product[]>;

  constructor(private productService: ProductService, private ui: UiService) {}

  openNewProduct() {
    this.ui.openNewProduct(true);
  }

  editProduct(p: Product) {
    // Open modal in edit mode and pass product
    this.ui.openEditProduct(p);
  }

  deleteProduct(p: Product) {
    if (!confirm(`¿Eliminar el producto "${p.name}"?`)) return;
    this.productService.deleteProduct(p.id).subscribe(() => {
      // refresh is automatic via BehaviorSubject in service
    });
  }

  ngOnInit(): void {
    this.products$ = this.productService.products$;
    // Kick off an initial load (ProductService already has mock data)
    this.productService.getProducts().subscribe();
  }
}
