import { Moment } from 'moment';
import { ICustomer } from 'app/shared/model/customer.model';

export interface IPayment {
  id?: number;
  name?: string;
  cardType?: string;
  cardNumber?: string;
  dateTime?: Moment;
  successful?: boolean;
  customer?: ICustomer;
}

export class Payment implements IPayment {
  constructor(
    public id?: number,
    public name?: string,
    public cardType?: string,
    public cardNumber?: string,
    public dateTime?: Moment,
    public successful?: boolean,
    public customer?: ICustomer
  ) {
    this.successful = this.successful || false;
  }
}
