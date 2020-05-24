import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ISeller } from 'app/shared/model/seller.model';
import { SellerService } from './seller.service';

@Component({
  templateUrl: './seller-delete-dialog.component.html'
})
export class SellerDeleteDialogComponent {
  seller?: ISeller;

  constructor(protected sellerService: SellerService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.sellerService.delete(id).subscribe(() => {
      this.eventManager.broadcast('sellerListModification');
      this.activeModal.close();
    });
  }
}
