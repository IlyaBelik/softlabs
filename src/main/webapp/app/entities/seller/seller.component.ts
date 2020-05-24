import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager, JhiParseLinks } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ISeller } from 'app/shared/model/seller.model';

import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { SellerService } from './seller.service';
import { SellerDeleteDialogComponent } from './seller-delete-dialog.component';

@Component({
  selector: 'jhi-seller',
  templateUrl: './seller.component.html'
})
export class SellerComponent implements OnInit, OnDestroy {
  sellers: ISeller[];
  eventSubscriber?: Subscription;
  itemsPerPage: number;
  links: any;
  page: number;
  predicate: string;
  ascending: boolean;

  constructor(
    protected sellerService: SellerService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal,
    protected parseLinks: JhiParseLinks
  ) {
    this.sellers = [];
    this.itemsPerPage = ITEMS_PER_PAGE;
    this.page = 0;
    this.links = {
      last: 0
    };
    this.predicate = 'id';
    this.ascending = true;
  }

  loadAll(): void {
    this.sellerService
      .query({
        page: this.page,
        size: this.itemsPerPage,
        sort: this.sort()
      })
      .subscribe((res: HttpResponse<ISeller[]>) => this.paginateSellers(res.body, res.headers));
  }

  reset(): void {
    this.page = 0;
    this.sellers = [];
    this.loadAll();
  }

  loadPage(page: number): void {
    this.page = page;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInSellers();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: ISeller): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInSellers(): void {
    this.eventSubscriber = this.eventManager.subscribe('sellerListModification', () => this.reset());
  }

  delete(seller: ISeller): void {
    const modalRef = this.modalService.open(SellerDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.seller = seller;
  }

  sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected paginateSellers(data: ISeller[] | null, headers: HttpHeaders): void {
    const headersLink = headers.get('link');
    this.links = this.parseLinks.parse(headersLink ? headersLink : '');
    if (data) {
      for (let i = 0; i < data.length; i++) {
        this.sellers.push(data[i]);
      }
    }
  }
}
