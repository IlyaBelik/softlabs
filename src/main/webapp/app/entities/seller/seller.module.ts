import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AmazonSharedModule } from 'app/shared/shared.module';
import { SellerComponent } from './seller.component';
import { SellerDetailComponent } from './seller-detail.component';
import { SellerUpdateComponent } from './seller-update.component';
import { SellerDeleteDialogComponent } from './seller-delete-dialog.component';
import { sellerRoute } from './seller.route';

@NgModule({
  imports: [AmazonSharedModule, RouterModule.forChild(sellerRoute)],
  declarations: [SellerComponent, SellerDetailComponent, SellerUpdateComponent, SellerDeleteDialogComponent],
  entryComponents: [SellerDeleteDialogComponent]
})
export class AmazonSellerModule {}
