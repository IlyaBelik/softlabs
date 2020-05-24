import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { ICart } from 'app/shared/model/cart.model';

type EntityResponseType = HttpResponse<ICart>;
type EntityArrayResponseType = HttpResponse<ICart[]>;

@Injectable({ providedIn: 'root' })
export class CartService {
  public resourceUrl = SERVER_API_URL + 'api/carts';

  constructor(protected http: HttpClient) {}

  create(cart: ICart): Observable<EntityResponseType> {
    return this.http.post<ICart>(this.resourceUrl, cart, { observe: 'response' });
  }

  update(cart: ICart): Observable<EntityResponseType> {
    return this.http.put<ICart>(this.resourceUrl, cart, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICart>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICart[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
