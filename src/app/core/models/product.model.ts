export type ProductCategory = 'Café Caliente' | 'Bebidas Calientes' | 'Postres' | 'Snacks';

export interface Product {
  id: string;
  name: string;
  description: string;
  category: ProductCategory;
  price: number;
  stock: number;
  image: string;
  isActive: boolean;
  createdAt: Date;
  updatedAt: Date;
}

export interface CreateProductDto {
  name: string;
  description: string;
  category: ProductCategory;
  price: number;
  stock: number;
  image: string;
}

export interface UpdateProductDto extends Partial<CreateProductDto> {
  id: string;
}