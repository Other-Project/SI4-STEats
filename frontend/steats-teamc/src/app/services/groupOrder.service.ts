import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {GroupOrder} from '../models/groupOrder.model';

@Injectable({
  providedIn: 'root'
})
export class GroupOrderService {
  private groupOrder: GroupOrder | null = null;

  constructor(private http: HttpClient) {}

  groupApiUrl: string = 'http://localhost:5008/api/orders/groups';
  singleApiUrl: string = 'http://localhost:5004/api/orders/singles';

  joinGroupOrder(groupCode: string, userId: string): Observable<void> {
    return new Observable<void>((observer) => {
      this.http.post<void>(`${this.singleApiUrl}`, { userId, groupCode }).subscribe({
        next: () => {
          observer.next();
          observer.complete();
        },
        error: (error) => {
          observer.error(error);
        }
      });
    });
  }

  createGroupOrder(): Observable<void> {
    return this.http.post<void>(`${this.groupApiUrl}`, {});
  }
}
