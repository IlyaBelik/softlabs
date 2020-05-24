import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'seller',
        loadChildren: () => import('./seller/seller.module').then(m => m.AmazonSellerModule)
      },
      {
        path: 'product',
        loadChildren: () => import('./product/product.module').then(m => m.AmazonProductModule)
      },
      {
        path: 'cart',
        loadChildren: () => import('./cart/cart.module').then(m => m.AmazonCartModule)
      },
      {
        path: 'customer',
        loadChildren: () => import('./customer/customer.module').then(m => m.AmazonCustomerModule)
      },
      {
        path: 'payment',
        loadChildren: () => import('./payment/payment.module').then(m => m.AmazonPaymentModule)
      }
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ])
  ]
})
export class AmazonEntityModule {}
