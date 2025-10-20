import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { map } from 'rxjs/operators';
import { CartItem } from '../models/cart.model';

const STORAGE_KEY = 'cart_items';

@Injectable({ providedIn: 'root' })
export class CartService {
  private itemsSubject = new BehaviorSubject<CartItem[]>(this.load());
  public items$ = this.itemsSubject.asObservable();
  // numeric count of total items (quantities summed)
  public count$ = this.items$.pipe(
    map(items => items.reduce((acc, i) => acc + i.quantity, 0))
  );

  add(item: CartItem) {
    const items = [...this.itemsSubject.value];
    const idx = items.findIndex(i => i.productId === item.productId);
    if (idx >= 0) {
      items[idx] = { ...items[idx], quantity: items[idx].quantity + item.quantity };
    } else {
      items.push(item);
    }
    this.persist(items);
  }

  remove(productId: string) {
    const items = this.itemsSubject.value.filter(i => i.productId !== productId);
    this.persist(items);
  }

  clear() {
    this.persist([]);
  }

  getCount(): number {
    return this.itemsSubject.value.reduce((acc, i) => acc + i.quantity, 0);
  }

  private persist(items: CartItem[]) {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(items));
    this.itemsSubject.next(items);
  }

  private load(): CartItem[] {
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      return raw ? JSON.parse(raw) : [];
    } catch {
      return [];
    }
  }
}
