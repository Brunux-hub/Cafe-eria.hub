export class Sale {
}

export interface SaleItem {
  productId: string;
  productName: string;
  quantity: number;
  unitPrice: number;
  subtotal: number;
}

export interface Sale {
  id: string;
  customerName: string;
  items: SaleItem[];
  subtotal: number;
  discount: number;
  total: number;
  status: 'COMPLETADA' | 'PENDIENTE' | 'CANCELADA';
  createdAt: Date;
}

export interface CreateSaleDto {
  customerName: string;
  items: SaleItem[];
  discount?: number;
}