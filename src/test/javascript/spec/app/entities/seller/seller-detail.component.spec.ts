import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AmazonTestModule } from '../../../test.module';
import { SellerDetailComponent } from 'app/entities/seller/seller-detail.component';
import { Seller } from 'app/shared/model/seller.model';

describe('Component Tests', () => {
  describe('Seller Management Detail Component', () => {
    let comp: SellerDetailComponent;
    let fixture: ComponentFixture<SellerDetailComponent>;
    const route = ({ data: of({ seller: new Seller(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AmazonTestModule],
        declarations: [SellerDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(SellerDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(SellerDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load seller on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.seller).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
