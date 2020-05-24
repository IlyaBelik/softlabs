import { IProduct } from 'app/shared/model/product.model';
import { ICustomer } from 'app/shared/model/customer.model';

export interface ICart {
  id?: number;
  productsNumber?: number;
  totalPrice?: number;
  productsIns?: IProduct[];
  customer?: ICustomer;
}

export class Cart implements ICart {
  constructor(
    public id?: number,
    public productsNumber?: number,
    public totalPrice?: number,
    public productsIns?: IProduct[],
    public customer?: ICustomer
  ) {}
}
