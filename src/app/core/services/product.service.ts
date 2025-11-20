import { Injectable, Inject, Optional } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { delay, map, tap, catchError } from 'rxjs/operators';
import { Product, CreateProductDto, UpdateProductDto } from '../models/product.model';
import { API_BASE_URL } from '../../app.config';

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

  constructor(
    private http: HttpClient,
    @Optional() @Inject(API_BASE_URL) private apiUrl?: string
  ) {}

  // Obtener todos los productos
  getProducts(): Observable<Product[]> {
    if (this.apiUrl) {
      return this.http.get<any[]>(`${this.apiUrl}/productos`).pipe(
        map(products => products.map(p => this.mapBackendProduct(p))),
        tap(products => {
          this.mockProducts = products;
          this.productsSubject.next(products);
        }),
        catchError(() => {
          // Fallback a mock si falla
          return of(this.mockProducts).pipe(delay(500));
        })
      );
    }
    return of(this.mockProducts).pipe(delay(500));
  }

  private mapBackendProduct(backend: any): Product {
    return {
      id: backend.id?.toString() || backend.idProducto?.toString(),
      name: backend.name || backend.nombre,
      description: backend.description || backend.descripcion,
      category: backend.category || this.getCategoryName(backend.categoria),
      price: typeof backend.price === 'number' ? backend.price : parseFloat(backend.precio),
      stock: backend.stock,
      image: backend.image || backend.imagenUrl || 'assets/images/default.svg',
      isActive: backend.isActive !== undefined ? backend.isActive : backend.activo,
      createdAt: backend.createdAt ? new Date(backend.createdAt) : new Date(backend.fechaCreacion),
      updatedAt: backend.updatedAt ? new Date(backend.updatedAt) : new Date(backend.fechaActualizacion)
    };
  }

  private getCategoryName(categoria: any): string {
    return categoria?.nombre || 'Sin categoría';
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
    if (this.apiUrl) {
      return this.http.post<any>(`${this.apiUrl}/productos`, dto).pipe(
        map(p => this.mapBackendProduct(p)),
        tap(product => {
          this.mockProducts.push(product);
          this.productsSubject.next([...this.mockProducts]);
        })
      );
    }

    // Fallback mock
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
    if (this.apiUrl && dto.id) {
      return this.http.put<any>(`${this.apiUrl}/productos/${dto.id}`, dto).pipe(
        map(p => this.mapBackendProduct(p)),
        tap(updatedProduct => {
          const index = this.mockProducts.findIndex(p => p.id === dto.id);
          if (index !== -1) {
            this.mockProducts[index] = updatedProduct;
            this.productsSubject.next([...this.mockProducts]);
          }
        })
      );
    }

    // Fallback mock
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