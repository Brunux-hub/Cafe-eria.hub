import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

interface SaleRow {
  id: string;
  customer: string;
  date: string;
  products: string[];
  discount: number;
  total: number;
  status: string;
}

@Component({
  selector: 'app-sales-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './sales-list.component.html',
  styleUrls: ['./sales-list.component.scss']
})
export class SalesListComponent implements OnInit {
  // summary KPIs (mock)
  totalSales = 25.6;
  transactions = 2;
  productsSold = 7;
  avgPerSale = 12.8;
  discounts = 5.1;

  sales: SaleRow[] = [];

  ngOnInit(): void {
    // mock sales rows
    this.sales = [
      {
        id: '#1',
        customer: 'Juan Pérez',
        date: '15/10/2024 10:30:00',
        products: ['2x Espresso', '2x Cappuccino'],
        discount: -3.2,
        total: 14.4,
        status: 'COMPLETADA'
      },
      {
        id: '#3',
        customer: 'Carlos López',
        date: '17/10/2024 9:15:00',
        products: ['2x Latte', '1x Té Verde'],
        discount: -1.9,
        total: 11.2,
        status: 'COMPLETADA'
      }
    ];
  }
}
