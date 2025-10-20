import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { delay, map } from 'rxjs/operators';
import { Product, CreateProductDto, UpdateProductDto } from '../models/product.model';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  // Mock data inicial
  private mockProducts: Product[] = [
    {
      id: '1',
      name: 'Espresso',
      description: 'Café espresso italiano tradicional',
      category: 'Café Caliente',
      price: 3.50,
      stock: 100,
  image: 'assets/images/espresso.svg',
      isActive: true,
      createdAt: new Date('2024-01-01'),
      updatedAt: new Date('2024-01-01')
    },
    {
      id: '2',
      name: 'Cappuccino',
      description: 'Espresso con leche vaporizada y espuma',
      category: 'Café Caliente',
      price: 4.50,
      stock: 100,
  image: 'assets/images/cappuccino.svg',
      isActive: true,
      createdAt: new Date('2024-01-01'),
      updatedAt: new Date('2024-01-01')
    },
    {
      id: '3',
      name: 'Latte',
      description: 'Café con leche y arte latte',
      category: 'Café Caliente',
      price: 4.75,
      stock: 100,
  image: 'assets/images/latte.svg',
      isActive: true,
      createdAt: new Date('2024-01-01'),
      updatedAt: new Date('2024-01-01')
    },
    {
      id: '4',
      name: 'Americano',
      description: 'Espresso diluido con agua caliente',
      category: 'Café Caliente',
      price: 3.75,
      stock: 100,
  image: 'assets/images/americano.svg',
      isActive: true,
      createdAt: new Date('2024-01-01'),
      updatedAt: new Date('2024-01-01')
    },
    {
      id: '5',
      name: 'Té Verde',
      description: 'Té verde premium japonés',
      category: 'Bebidas Calientes',
      price: 3.25,
      stock: 80,
  image: 'assets/images/te-verde.svg',
      isActive: true,
      createdAt: new Date('2024-01-01'),
      updatedAt: new Date('2024-01-01')
    },
    {
      id: '6',
      name: 'Té Negro',
      description: 'Té negro inglés tradicional',
      category: 'Bebidas Calientes',
      price: 3.00,
      stock: 80,
  image: 'assets/images/te-negro.svg',
      isActive: true,
      createdAt: new Date('2024-01-01'),
      updatedAt: new Date('2024-01-01')
    }
  ];

  private productsSubject = new BehaviorSubject<Product[]>(this.mockProducts);
  public products$ = this.productsSubject.asObservable();

  constructor() {}

  // Obtener todos los productos
  getProducts(): Observable<Product[]> {
    return of(this.mockProducts).pipe(delay(500));
  }

  // Obtener producto por ID
  getProductById(id: string): Observable<Product> {
    const product = this.mockProducts.find(p => p.id === id);
    if (product) {
      return of(product).pipe(delay(300));
    }
    return throwError(() => new Error('Producto no encontrado'));
  }

  // Crear producto
  createProduct(dto: CreateProductDto): Observable<Product> {
    const newProduct: Product = {
      id: Date.now().toString(),
      name: dto.name,
      description: dto.description,
      category: dto.category as 'Café Caliente' | 'Bebidas Calientes' | 'Postres' | 'Snacks',
      price: dto.price,
      stock: dto.stock,
      image: dto.image,
      isActive: true,
      createdAt: new Date(),
      updatedAt: new Date()
    };

    this.mockProducts.push(newProduct);
    this.productsSubject.next([...this.mockProducts]);

    return of(newProduct).pipe(delay(500));
  }

  // Actualizar producto
  updateProduct(dto: UpdateProductDto): Observable<Product> {
    const index = this.mockProducts.findIndex(p => p.id === dto.id);
    
    if (index === -1) {
      return throwError(() => new Error('Producto no encontrado'));
    }

    const updatedProduct: Product = {
      ...this.mockProducts[index],
      ...(dto.name && { name: dto.name }),
      ...(dto.description && { description: dto.description }),
      ...(dto.category && { category: dto.category as 'Café Caliente' | 'Bebidas Calientes' | 'Postres' | 'Snacks' }),
      ...(dto.price !== undefined && { price: dto.price }),
      ...(dto.stock !== undefined && { stock: dto.stock }),
      ...(dto.image && { image: dto.image }),
      updatedAt: new Date()
    };

    this.mockProducts[index] = updatedProduct;
    this.productsSubject.next([...this.mockProducts]);

    return of(updatedProduct).pipe(delay(500));
  }

  // Eliminar producto
  deleteProduct(id: string): Observable<void> {
    const index = this.mockProducts.findIndex(p => p.id === id);
    
    if (index === -1) {
      return throwError(() => new Error('Producto no encontrado'));
    }

    this.mockProducts.splice(index, 1);
    this.productsSubject.next([...this.mockProducts]);

    return of(void 0).pipe(delay(500));
  }

  // Obtener productos por categoría
  getProductsByCategory(category: string): Observable<Product[]> {
    return this.products$.pipe(
      map(products => products.filter(p => p.category === category))
    );
  }

  // Buscar productos
  searchProducts(query: string): Observable<Product[]> {
    const lowerQuery = query.toLowerCase();
    return this.products$.pipe(
      map(products => 
        products.filter(p => 
          p.name.toLowerCase().includes(lowerQuery) ||
          p.description.toLowerCase().includes(lowerQuery)
        )
      )
    );
  }
}