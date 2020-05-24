import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { ICustomer, Customer } from 'app/shared/model/customer.model';
import { CustomerService } from './customer.service';
import { ICart } from 'app/shared/model/cart.model';
import { CartService } from 'app/entities/cart/cart.service';
import { IProduct } from 'app/shared/model/product.model';
import { ProductService } from 'app/entities/product/product.service';

type SelectableEntity = ICart | IProduct;

@Component({
  selector: 'jhi-customer-update',
  templateUrl: './customer-update.component.html'
})
export class CustomerUpdateComponent implements OnInit {
  isSaving = false;
  carts: ICart[] = [];
  products: IProduct[] = [];

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required, Validators.maxLength(100)]],
    phone: [null, [Validators.pattern('^(\\+\\d{1,3}[- ]?)?\\d{10}$')]],
    address: [null, [Validators.maxLength(100)]],
    cart: [],
    orderedProducts: []
  });

  constructor(
    protected customerService: CustomerService,
    protected cartService: CartService,
    protected productService: ProductService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ customer }) => {
      this.updateForm(customer);

      this.cartService
        .query({ 'customerId.specified': 'false' })
        .pipe(
          map((res: HttpResponse<ICart[]>) => {
            return res.body || [];
          })
        )
        .subscribe((resBody: ICart[]) => {
          if (!customer.cart || !customer.cart.id) {
            this.carts = resBody;
          } else {
            this.cartService
              .find(customer.cart.id)
              .pipe(
                map((subRes: HttpResponse<ICart>) => {
                  return subRes.body ? [subRes.body].concat(resBody) : resBody;
                })
              )
              .subscribe((concatRes: ICart[]) => (this.carts = concatRes));
          }
        });

      this.productService.query().subscribe((res: HttpResponse<IProduct[]>) => (this.products = res.body || []));
    });
  }

  updateForm(customer: ICustomer): void {
    this.editForm.patchValue({
      id: customer.id,
      name: customer.name,
      phone: customer.phone,
      address: customer.address,
      cart: customer.cart,
      orderedProducts: customer.orderedProducts
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const customer = this.createFromForm();
    if (customer.id !== undefined) {
      this.subscribeToSaveResponse(this.customerService.update(customer));
    } else {
      this.subscribeToSaveResponse(this.customerService.create(customer));
    }
  }

  private createFromForm(): ICustomer {
    return {
      ...new Customer(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      phone: this.editForm.get(['phone'])!.value,
      address: this.editForm.get(['address'])!.value,
      cart: this.editForm.get(['cart'])!.value,
      orderedProducts: this.editForm.get(['orderedProducts'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICustomer>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }

  trackById(index: number, item: SelectableEntity): any {
    return item.id;
  }

  getSelected(selectedVals: IProduct[], option: IProduct): IProduct {
    if (selectedVals) {
      for (let i = 0; i < selectedVals.length; i++) {
        if (option.id === selectedVals[i].id) {
          return selectedVals[i];
        }
      }
    }
    return option;
  }
}
