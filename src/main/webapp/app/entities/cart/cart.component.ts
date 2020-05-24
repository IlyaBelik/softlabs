import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager, JhiParseLinks } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ICart } from 'app/shared/model/cart.model';

import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { CartService } from './cart.service';
import { CartDeleteDialogComponent } from './cart-delete-dialog.component';

@Component({
  selector: 'jhi-cart',
  templateUrl: './cart.component.html'
})
export class CartComponent implements OnInit, OnDestroy {
  carts: ICart[];
  eventSubscriber?: Subscription;
  itemsPerPage: number;
  links: any;
  page: number;
  predicate: string;
  ascending: boolean;

  constructor(
    protected cartService: CartService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal,
    protected parseLinks: JhiParseLinks
  ) {
    this.carts = [];
    this.itemsPerPage = ITEMS_PER_PAGE;
    this.page = 0;
    this.links = {
      last: 0
    };
    this.predicate = 'id';
    this.ascending = true;
  }

  loadAll(): void {
    this.cartService
      .query({
        page: this.page,
        size: this.itemsPerPage,
        sort: this.sort()
      })
      .subscribe((res: HttpResponse<ICart[]>) => this.paginateCarts(res.body, res.headers));
  }

  reset(): void {
    this.page = 0;
    this.carts = [];
    this.loadAll();
  }

  loadPage(page: number): void {
    this.page = page;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInCarts();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: ICart): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInCarts(): void {
    this.eventSubscriber = this.eventManager.subscribe('cartListModification', () => this.reset());
  }

  delete(cart: ICart): void {
    const modalRef = this.modalService.open(CartDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.cart = cart;
  }

  sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected paginateCarts(data: ICart[] | null, headers: HttpHeaders): void {
    const headersLink = headers.get('link');
    this.links = this.parseLinks.parse(headersLink ? headersLink : '');
    if (data) {
      for (let i = 0; i < data.length; i++) {
        this.carts.push(data[i]);
      }
    }
  }
}
