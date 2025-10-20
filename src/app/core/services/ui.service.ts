import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class UiService {
  private openNewProductSource = new Subject<boolean>();
  openNewProduct$ = this.openNewProductSource.asObservable();

  openNewProduct(open = true) {
    this.openNewProductSource.next(open);
  }

  // allow passing a product to edit
  private editProductSource = new Subject<any | null>();
  editProduct$ = this.editProductSource.asObservable();

  openEditProduct(product: any | null) {
    this.editProductSource.next(product);
    // also open the modal
    this.openNewProduct(true);
  }
}
