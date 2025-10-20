import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Product } from '../../core/models/product.model';
import { ProductService } from '../../core/services/product.service';
import { PromotionService } from '../../core/services/promotion.service';
import { Observable, forkJoin, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { CartService } from '../../core/services/cart.service';

@Component({
  selector: 'app-catalog',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './catalog.component.html',
  styles: [``]
})
export class CatalogComponent implements OnInit {
  private productService = inject(ProductService);
  private promotionService = inject(PromotionService);
  private cartService = inject(CartService);

  products: Product[] = [];
  filtered: Product[] = [];
  categories: string[] = [];
  selectedCategory = 'Todas las categorías';
  activePromotions$ = this.promotionService.getActivePromotions();
  // Mapa de descuentos/precios precalculados
  private discountMap: Record<string, { price: number; discount: number }> = {};

  ngOnInit(): void {
    this.productService.getProducts().subscribe(p => {
      this.products = p;
      this.filtered = p;
      this.categories = Array.from(new Set(p.map(x => x.category)));
      this.categories.unshift('Todas las categorías');

      // Precalcular descuentos de forma paralela y cachearlos
      const requests = p.map(prod =>
        this.promotionService.getDiscountForProduct(prod.id).pipe(
          map(discount => ({ id: prod.id, discount }))
        )
      );

      if (requests.length) {
        forkJoin(requests).subscribe(results => {
          results.forEach(({ id, discount }) => {
            const base = this.products.find(pr => pr.id === id)!;
            const price = discount > 0 ? +(base.price * (1 - discount / 100)).toFixed(2) : base.price;
            this.discountMap[id] = { price, discount };
          });
        });
      }
    });
  }

  filterCategory(cat: string) {
    this.selectedCategory = cat;
    if (cat === 'Todas las categorías') {
      this.filtered = this.products;
    } else {
      this.filtered = this.products.filter(p => p.category === cat);
    }
  }

  // Obtener precio/discount sincrónicamente desde el mapa (fallback al precio base)
  pricedOf(product: Product): { price: number; discount: number } {
    return this.discountMap[product.id] ?? { price: product.price, discount: 0 };
  }

  addToCart(p: Product) {
    const { price } = this.pricedOf(p);
    this.cartService.add({
      productId: p.id,
      name: p.name,
      price: price,
      quantity: 1,
      image: p.image
    });
  }
}
