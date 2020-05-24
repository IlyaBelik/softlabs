import { ISeller } from 'app/shared/model/seller.model';
import { ICart } from 'app/shared/model/cart.model';
import { ICustomer } from 'app/shared/model/customer.model';

export interface IProduct {
  id?: number;
  name?: string;
  recommendeAgeGroup?: string;
  category?: string;
  seller?: ISeller;
  cartsIns?: ICart[];
  orderedBies?: ICustomer[];
}

export class Product implements IProduct {
  constructor(
    public id?: number,
    public name?: string,
    public recommendeAgeGroup?: string,
    public category?: string,
    public seller?: ISeller,
    public cartsIns?: ICart[],
    public orderedBies?: ICustomer[]
  ) {}
}
