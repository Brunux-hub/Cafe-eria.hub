import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { delay } from 'rxjs/operators';
import { Promotion, CreatePromotionDto } from '../models/promotion.model';

@Injectable({
  providedIn: 'root'
})
export class PromotionService {
  // Mock data inicial
  private mockPromotions: Promotion[] = [
    {
      id: '1',
      name: 'Descuento Cafés Calientes',
      description: '20% de descuento en todos los cafés',
      discountPercentage: 20,
      startDate: new Date('2024-09-30'),
      endDate: new Date('2024-10-30'),
      productIds: ['1', '2', '3', '4'],
      isActive: true,
      createdAt: new Date('2024-09-25')
    }
  ];

  private promotionsSubject = new BehaviorSubject<Promotion[]>(this.mockPromotions);
  public promotions$ = this.promotionsSubject.asObservable();

  constructor() {}

  // Obtener todas las promociones
  getPromotions(): Observable<Promotion[]> {
    return of(this.mockPromotions).pipe(delay(500));
  }

  // Obtener promoción por ID
  getPromotionById(id: string): Observable<Promotion> {
    const promotion = this.mockPromotions.find(p => p.id === id);
    if (promotion) {
      return of(promotion).pipe(delay(300));
    }
    return throwError(() => new Error('Promoción no encontrada'));
  }

  // Crear promoción
  createPromotion(dto: CreatePromotionDto): Observable<Promotion> {
    const newPromotion: Promotion = {
      id: Date.now().toString(),
      ...dto,
      isActive: true,
      createdAt: new Date()
    };

    this.mockPromotions.push(newPromotion);
    this.promotionsSubject.next([...this.mockPromotions]);

    return of(newPromotion).pipe(delay(500));
  }

  // Actualizar promoción
  updatePromotion(id: string, dto: Partial<CreatePromotionDto>): Observable<Promotion> {
    const index = this.mockPromotions.findIndex(p => p.id === id);
    
    if (index === -1) {
      return throwError(() => new Error('Promoción no encontrada'));
    }

    this.mockPromotions[index] = {
      ...this.mockPromotions[index],
      ...dto
    };

    this.promotionsSubject.next([...this.mockPromotions]);

    return of(this.mockPromotions[index]).pipe(delay(500));
  }

  // Eliminar promoción
  deletePromotion(id: string): Observable<void> {
    const index = this.mockPromotions.findIndex(p => p.id === id);
    
    if (index === -1) {
      return throwError(() => new Error('Promoción no encontrada'));
    }

    this.mockPromotions.splice(index, 1);
    this.promotionsSubject.next([...this.mockPromotions]);

    return of(void 0).pipe(delay(500));
  }

  // Toggle estado activo/inactivo
  togglePromotionStatus(id: string): Observable<Promotion> {
    const index = this.mockPromotions.findIndex(p => p.id === id);
    
    if (index === -1) {
      return throwError(() => new Error('Promoción no encontrada'));
    }

    this.mockPromotions[index].isActive = !this.mockPromotions[index].isActive;
    this.promotionsSubject.next([...this.mockPromotions]);

    return of(this.mockPromotions[index]).pipe(delay(500));
  }

  // Obtener promociones activas
  getActivePromotions(): Observable<Promotion[]> {
    const active = this.mockPromotions.filter(p => {
      const now = new Date();
      return p.isActive && 
             new Date(p.startDate) <= now && 
             new Date(p.endDate) >= now;
    });
    return of(active).pipe(delay(500));
  }

  // Obtener descuento aplicable para un producto
  getDiscountForProduct(productId: string): Observable<number> {
    const activePromotion = this.mockPromotions.find(p => 
      p.isActive && 
      p.productIds.includes(productId) &&
      new Date(p.startDate) <= new Date() &&
      new Date(p.endDate) >= new Date()
    );

    return of(activePromotion ? activePromotion.discountPercentage : 0).pipe(delay(300));
  }
}