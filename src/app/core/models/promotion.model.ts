export class Promotion {
}

export interface Promotion {
  id: string;
  name: string;
  description: string;
  discountPercentage: number;
  startDate: Date;
  endDate: Date;
  productIds: string[];
  isActive: boolean;
  createdAt: Date;
}

export interface CreatePromotionDto {
  name: string;
  description: string;
  discountPercentage: number;
  startDate: Date;
  endDate: Date;
  productIds: string[];
}