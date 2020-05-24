import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { ISeller, Seller } from 'app/shared/model/seller.model';
import { SellerService } from './seller.service';

@Component({
  selector: 'jhi-seller-update',
  templateUrl: './seller-update.component.html'
})
export class SellerUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required, Validators.maxLength(100)]],
    phone: [null, [Validators.pattern('^(\\+\\d{1,3}[- ]?)?\\d{10}$')]],
    address: [null, [Validators.maxLength(100)]]
  });

  constructor(protected sellerService: SellerService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ seller }) => {
      this.updateForm(seller);
    });
  }

  updateForm(seller: ISeller): void {
    this.editForm.patchValue({
      id: seller.id,
      name: seller.name,
      phone: seller.phone,
      address: seller.address
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const seller = this.createFromForm();
    if (seller.id !== undefined) {
      this.subscribeToSaveResponse(this.sellerService.update(seller));
    } else {
      this.subscribeToSaveResponse(this.sellerService.create(seller));
    }
  }

  private createFromForm(): ISeller {
    return {
      ...new Seller(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      phone: this.editForm.get(['phone'])!.value,
      address: this.editForm.get(['address'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISeller>>): void {
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
}
