export interface CartItem {
  productId: string;
  name: string;
  price: number; // unit price
  quantity: number;
  image?: string;
}
