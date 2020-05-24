import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { ICart, Cart } from 'app/shared/model/cart.model';
import { CartService } from './cart.service';
import { IProduct } from 'app/shared/model/product.model';
import { ProductService } from 'app/entities/product/product.service';

@Component({
  selector: 'jhi-cart-update',
  templateUrl: './cart-update.component.html'
})
export class CartUpdateComponent implements OnInit {
  isSaving = false;
  products: IProduct[] = [];

  editForm = this.fb.group({
    id: [],
    productsNumber: [],
    totalPrice: [],
    productsIns: []
  });

  constructor(
    protected cartService: CartService,
    protected productService: ProductService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ cart }) => {
      this.updateForm(cart);

      this.productService.query().subscribe((res: HttpResponse<IProduct[]>) => (this.products = res.body || []));
    });
  }

  updateForm(cart: ICart): void {
    this.editForm.patchValue({
      id: cart.id,
      productsNumber: cart.productsNumber,
      totalPrice: cart.totalPrice,
      productsIns: cart.productsIns
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const cart = this.createFromForm();
    if (cart.id !== undefined) {
      this.subscribeToSaveResponse(this.cartService.update(cart));
    } else {
      this.subscribeToSaveResponse(this.cartService.create(cart));
    }
  }

  private createFromForm(): ICart {
    return {
      ...new Cart(),
      id: this.editForm.get(['id'])!.value,
      productsNumber: this.editForm.get(['productsNumber'])!.value,
      totalPrice: this.editForm.get(['totalPrice'])!.value,
      productsIns: this.editForm.get(['productsIns'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICart>>): void {
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

  trackById(index: number, item: IProduct): any {
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
