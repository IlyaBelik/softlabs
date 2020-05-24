import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { IPayment, Payment } from 'app/shared/model/payment.model';
import { PaymentService } from './payment.service';
import { ICustomer } from 'app/shared/model/customer.model';
import { CustomerService } from 'app/entities/customer/customer.service';

@Component({
  selector: 'jhi-payment-update',
  templateUrl: './payment-update.component.html'
})
export class PaymentUpdateComponent implements OnInit {
  isSaving = false;
  customers: ICustomer[] = [];

  editForm = this.fb.group({
    id: [],
    name: [],
    cardType: [],
    cardNumber: [],
    dateTime: [],
    successful: [],
    customer: []
  });

  constructor(
    protected paymentService: PaymentService,
    protected customerService: CustomerService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ payment }) => {
      if (!payment.id) {
        const today = moment().startOf('day');
        payment.dateTime = today;
      }

      this.updateForm(payment);

      this.customerService.query().subscribe((res: HttpResponse<ICustomer[]>) => (this.customers = res.body || []));
    });
  }

  updateForm(payment: IPayment): void {
    this.editForm.patchValue({
      id: payment.id,
      name: payment.name,
      cardType: payment.cardType,
      cardNumber: payment.cardNumber,
      dateTime: payment.dateTime ? payment.dateTime.format(DATE_TIME_FORMAT) : null,
      successful: payment.successful,
      customer: payment.customer
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const payment = this.createFromForm();
    if (payment.id !== undefined) {
      this.subscribeToSaveResponse(this.paymentService.update(payment));
    } else {
      this.subscribeToSaveResponse(this.paymentService.create(payment));
    }
  }

  private createFromForm(): IPayment {
    return {
      ...new Payment(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      cardType: this.editForm.get(['cardType'])!.value,
      cardNumber: this.editForm.get(['cardNumber'])!.value,
      dateTime: this.editForm.get(['dateTime'])!.value ? moment(this.editForm.get(['dateTime'])!.value, DATE_TIME_FORMAT) : undefined,
      successful: this.editForm.get(['successful'])!.value,
      customer: this.editForm.get(['customer'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPayment>>): void {
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

  trackById(index: number, item: ICustomer): any {
    return item.id;
  }
}
