import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { ISeller, Seller } from 'app/shared/model/seller.model';
import { SellerService } from './seller.service';
import { SellerComponent } from './seller.component';
import { SellerDetailComponent } from './seller-detail.component';
import { SellerUpdateComponent } from './seller-update.component';

@Injectable({ providedIn: 'root' })
export class SellerResolve implements Resolve<ISeller> {
  constructor(private service: SellerService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISeller> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((seller: HttpResponse<Seller>) => {
          if (seller.body) {
            return of(seller.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Seller());
  }
}

export const sellerRoute: Routes = [
  {
    path: '',
    component: SellerComponent,
    data: {
      authorities: [Authority.USER],
      pageTitle: 'Sellers'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: SellerDetailComponent,
    resolve: {
      seller: SellerResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'Sellers'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: SellerUpdateComponent,
    resolve: {
      seller: SellerResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'Sellers'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: SellerUpdateComponent,
    resolve: {
      seller: SellerResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'Sellers'
    },
    canActivate: [UserRouteAccessService]
  }
];
