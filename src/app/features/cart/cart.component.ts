import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CartService } from '../../core/services/cart.service';
import { CartItem } from '../../core/models/cart.model';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.scss']
})
export class CartComponent {
  private cartService = inject(CartService);

  items$ = this.cartService.items$;
  total$ = this.items$.pipe(map(items => items.reduce((acc, i) => acc + i.price * i.quantity, 0)));

  updateQuantity(item: CartItem, qty: number) {
    const quantity = Math.max(1, Number(qty) || 1);
    // replace item with updated quantity
    this.cartService.remove(item.productId);
    this.cartService.add({ ...item, quantity });
  }

  removeItem(id: string) {
    this.cartService.remove(id);
  }

  clear() {
    this.cartService.clear();
  }
}
