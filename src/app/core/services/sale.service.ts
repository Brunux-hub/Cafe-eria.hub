import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';
import { Sale, CreateSaleDto, SaleItem } from '../models/sale.model';

@Injectable({
  providedIn: 'root'
})
export class SaleService {
  // Mock data inicial
  private mockSales: Sale[] = [
    {
      id: '1',
      customerName: 'Juan Pérez',
      items: [
        {
          productId: '1',
          productName: 'Espresso',
          quantity: 2,
          unitPrice: 3.50,
          subtotal: 7.00
        },
        {
          productId: '2',
          productName: 'Cappuccino',
          quantity: 2,
          unitPrice: 4.50,
          subtotal: 9.00
        }
      ],
      subtotal: 16.00,
      discount: 3.20,
      total: 14.40,
      status: 'COMPLETADA',
      createdAt: new Date('2024-10-15T10:30:00')
    },
    {
      id: '3',
      customerName: 'Carlos López',
      items: [
        {
          productId: '3',
          productName: 'Latte',
          quantity: 2,
          unitPrice: 4.75,
          subtotal: 9.50
        },
        {
          productId: '5',
          productName: 'Té Verde',
          quantity: 1,
          unitPrice: 3.25,
          subtotal: 3.25
        }
      ],
      subtotal: 12.75,
      discount: 1.90,
      total: 11.20,
      status: 'COMPLETADA',
      createdAt: new Date('2024-10-17T09:15:00')
    }
  ];

  private salesSubject = new BehaviorSubject<Sale[]>(this.mockSales);
  public sales$ = this.salesSubject.asObservable();

  constructor() {}

  // Obtener todas las ventas
  getSales(): Observable<Sale[]> {
    return of(this.mockSales).pipe(delay(500));
  }

  // Obtener venta por ID
  getSaleById(id: string): Observable<Sale | undefined> {
    const sale = this.mockSales.find(s => s.id === id);
    return of(sale).pipe(delay(300));
  }

  // Crear venta
  createSale(dto: CreateSaleDto): Observable<Sale> {
    const subtotal = dto.items.reduce((sum, item) => sum + item.subtotal, 0);
    const discount = dto.discount || 0;
    const total = subtotal - discount;

    const newSale: Sale = {
      id: Date.now().toString(),
      customerName: dto.customerName,
      items: dto.items,
      subtotal,
      discount,
      total,
      status: 'COMPLETADA',
      createdAt: new Date()
    };

    this.mockSales.unshift(newSale); // Agregar al inicio
    this.salesSubject.next([...this.mockSales]);

    return of(newSale).pipe(delay(500));
  }

  // Estadísticas del dashboard
  getSalesStats(): Observable<{
    totalSales: number;
    totalRevenue: number;
    productsSold: number;
    averageTicket: number;
    totalDiscount: number;
  }> {
    const totalSales = this.mockSales.length;
    const totalRevenue = this.mockSales.reduce((sum, sale) => sum + sale.total, 0);
    const productsSold = this.mockSales.reduce((sum, sale) => 
      sum + sale.items.reduce((itemSum, item) => itemSum + item.quantity, 0), 0
    );
    const averageTicket = totalSales > 0 ? totalRevenue / totalSales : 0;
    const totalDiscount = this.mockSales.reduce((sum, sale) => sum + sale.discount, 0);

    return of({
      totalSales,
      totalRevenue,
      productsSold,
      averageTicket,
      totalDiscount
    }).pipe(delay(500));
  }

  // Obtener ventas por rango de fechas
  getSalesByDateRange(startDate: Date, endDate: Date): Observable<Sale[]> {
    const filtered = this.mockSales.filter(sale => {
      const saleDate = new Date(sale.createdAt);
      return saleDate >= startDate && saleDate <= endDate;
    });
    return of(filtered).pipe(delay(500));
  }
}