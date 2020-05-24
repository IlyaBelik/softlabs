import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { AmazonTestModule } from '../../../test.module';
import { SellerUpdateComponent } from 'app/entities/seller/seller-update.component';
import { SellerService } from 'app/entities/seller/seller.service';
import { Seller } from 'app/shared/model/seller.model';

describe('Component Tests', () => {
  describe('Seller Management Update Component', () => {
    let comp: SellerUpdateComponent;
    let fixture: ComponentFixture<SellerUpdateComponent>;
    let service: SellerService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AmazonTestModule],
        declarations: [SellerUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(SellerUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(SellerUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(SellerService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Seller(123);
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new Seller();
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
