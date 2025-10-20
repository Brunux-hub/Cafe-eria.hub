import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProductService } from '../../../core/services/product.service';
import { Product } from '../../../core/models/product.model';

interface Promotion {
  id: string;
  title: string;
  description: string;
  active: boolean;
}

@Component({
  selector: 'app-promotion-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './promotion-list.component.html',
  styleUrls: ['./promotion-list.component.scss']
})
export class PromotionListComponent implements OnInit {
  promotions: Promotion[] = [];
  showNewPromotion = false;
  newPromotionForm: FormGroup;
  products: Product[] = [];
  selectedProductIds: Set<string> = new Set();

  constructor(private fb: FormBuilder, private productService: ProductService) {
    this.newPromotionForm = this.fb.group({
      title: ['', Validators.required],
      description: [''],
      discount: [10, [Validators.required, Validators.min(0)]],
      active: [true]
    });
  }

  ngOnInit(): void {
    this.promotions = [
      { id: '#p1', title: 'Descuento 10% - Cafés', description: '10% de descuento en todos los cafés', active: true },
      { id: '#p2', title: 'Combo Merienda', description: 'Café + Pastel a precio especial', active: false }
    ];

    // load products for modal product selection
    this.productService.getProducts().subscribe(p => this.products = p);
  }

  toggleNewPromotion(open?: boolean) {
    if (typeof open === 'boolean') {
      this.showNewPromotion = open;
    } else {
      this.showNewPromotion = !this.showNewPromotion;
    }
  }

  onToggleProduct(productId: string, checked: boolean) {
    if (checked) this.selectedProductIds.add(productId);
    else this.selectedProductIds.delete(productId);
  }

  submitNewPromotion() {
    if (this.newPromotionForm.invalid) return;
    const val = this.newPromotionForm.value;
    const newP: Promotion = {
      id: `#p${this.promotions.length + 1}`,
      title: val.title,
      description: val.description,
      active: !!val.active
    };
    this.promotions = [newP, ...this.promotions];
    this.toggleNewPromotion(false);
    this.newPromotionForm.reset({ title: '', description: '', discount: 10, active: true });
  }
}
