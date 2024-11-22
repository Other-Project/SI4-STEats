import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {GroupOrder} from '../models/groupOrder.model';
import {SingleOrder} from '../models/singleOrder.model';

@Injectable({
  providedIn: 'root'
})
export class OrderService {

  private orderId: string | null = null;
  private groupCode: string | null = null;

  constructor(private http: HttpClient) {
  }

  groupApiUrl: string = 'http://localhost:5008/api/orders/groups';
  singleApiUrl: string = 'http://localhost:5004/api/orders/singles';

  joinGroupOrder(groupCode: string, userId: string): Observable<SingleOrder> {
    return this.http.post<SingleOrder>(`${this.singleApiUrl}`, {userId, groupCode});
  }

  getGroupOrder(groupCode: string): GroupOrder | null {
    this.http.get<GroupOrder>(`${this.groupApiUrl}/${groupCode}`).subscribe({
      next: (groupOrder) => {
        return groupOrder;
      },
      error: (error) => console.error('Failed to fetch group order:', error)
    });
    return null;
  }

  getSingleOrder(groupCode: string): SingleOrder | null {
    this.http.get<SingleOrder>(`${this.singleApiUrl}?groupCode=${groupCode}`).subscribe({
      next: (singleOrder) => {
        return singleOrder;
      },
      error: (error) => console.error('Failed to fetch single order:', error)
    });
    return null;
  }

  createGroupOrder(restaurantId: string, addressId: string, deliveryTime: string): Observable<GroupOrder> {
    return this.http.post<GroupOrder>(`${this.groupApiUrl}`, {restaurantId, addressId, deliveryTime});
  }

  // maybe this should be in a different service
  // no notion of quantity

  addMenuItemToOrder(orderId: string, menuItemId: string) {
    this.http.post<void>(`${this.singleApiUrl}/${orderId}/menuItems`, {menuItemId}).subscribe({
      next: () => console.log('Successfully added menu item to order'),
      error: (error) => console.error('Failed to add menu item to order:', error)
    });
  }

  removeMenuItemFromOrder(orderId: string, menuItemId: string) {
    this.http.delete<void>(`${this.singleApiUrl}/${orderId}/menuItems/${menuItemId}`).subscribe({
      next: () => console.log('Successfully removed menu item from order'),
      error: (error) => console.error('Failed to remove menu item from order:', error)
    });
  }

  payForOrder(orderId: string): Observable<void> {
    return this.http.post<void>(`${this.singleApiUrl}/${orderId}/pay`, {});
  }

  setOrderId(orderId: string) {
    this.orderId = orderId;
  }

  getOrderId(): string | null {
    return this.orderId;
  }

  setGroupCode(groupCode: string) {
    this.groupCode = groupCode;
  }

  getGroupCode(): string | null {
    return this.groupCode;
  }
}
