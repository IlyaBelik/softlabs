import { IProduct } from 'app/shared/model/product.model';

export interface ISeller {
  id?: number;
  name?: string;
  phone?: string;
  address?: string;
  products?: IProduct[];
}

export class Seller implements ISeller {
  constructor(public id?: number, public name?: string, public phone?: string, public address?: string, public products?: IProduct[]) {}
}
