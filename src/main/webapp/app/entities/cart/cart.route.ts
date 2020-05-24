import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { ICart, Cart } from 'app/shared/model/cart.model';
import { CartService } from './cart.service';
import { CartComponent } from './cart.component';
import { CartDetailComponent } from './cart-detail.component';
import { CartUpdateComponent } from './cart-update.component';

@Injectable({ providedIn: 'root' })
export class CartResolve implements Resolve<ICart> {
  constructor(private service: CartService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICart> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((cart: HttpResponse<Cart>) => {
          if (cart.body) {
            return of(cart.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Cart());
  }
}

export const cartRoute: Routes = [
  {
    path: '',
    component: CartComponent,
    data: {
      authorities: [Authority.USER],
      pageTitle: 'Carts'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: CartDetailComponent,
    resolve: {
      cart: CartResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'Carts'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: CartUpdateComponent,
    resolve: {
      cart: CartResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'Carts'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: CartUpdateComponent,
    resolve: {
      cart: CartResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'Carts'
    },
    canActivate: [UserRouteAccessService]
  }
];
