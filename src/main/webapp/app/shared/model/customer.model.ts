import { ICart } from 'app/shared/model/cart.model';
import { IPayment } from 'app/shared/model/payment.model';
import { IProduct } from 'app/shared/model/product.model';

export interface ICustomer {
  id?: number;
  name?: string;
  phone?: string;
  address?: string;
  cart?: ICart;
  payments?: IPayment[];
  orderedProducts?: IProduct[];
}

export class Customer implements ICustomer {
  constructor(
    public id?: number,
    public name?: string,
    public phone?: string,
    public address?: string,
    public cart?: ICart,
    public payments?: IPayment[],
    public orderedProducts?: IProduct[]
  ) {}
}
