import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { ISeller } from 'app/shared/model/seller.model';

type EntityResponseType = HttpResponse<ISeller>;
type EntityArrayResponseType = HttpResponse<ISeller[]>;

@Injectable({ providedIn: 'root' })
export class SellerService {
  public resourceUrl = SERVER_API_URL + 'api/sellers';

  constructor(protected http: HttpClient) {}

  create(seller: ISeller): Observable<EntityResponseType> {
    return this.http.post<ISeller>(this.resourceUrl, seller, { observe: 'response' });
  }

  update(seller: ISeller): Observable<EntityResponseType> {
    return this.http.put<ISeller>(this.resourceUrl, seller, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISeller>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISeller[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
