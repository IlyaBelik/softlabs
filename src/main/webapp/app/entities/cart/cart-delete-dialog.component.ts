import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ICart } from 'app/shared/model/cart.model';
import { CartService } from './cart.service';

@Component({
  templateUrl: './cart-delete-dialog.component.html'
})
export class CartDeleteDialogComponent {
  cart?: ICart;

  constructor(protected cartService: CartService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.cartService.delete(id).subscribe(() => {
      this.eventManager.broadcast('cartListModification');
      this.activeModal.close();
    });
  }
}
