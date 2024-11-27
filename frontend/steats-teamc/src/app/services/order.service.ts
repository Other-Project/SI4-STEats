import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject, lastValueFrom, Observable} from 'rxjs';
import {GroupOrder} from '../models/groupOrder.model';
import {SingleOrder} from '../models/singleOrder.model';

@Injectable({
  providedIn: 'root'
})
export class OrderService {

  private groupOrder: GroupOrder | undefined;
  private singleOrder: SingleOrder | undefined;

  public singleOrder$: BehaviorSubject<SingleOrder | null> = new BehaviorSubject<SingleOrder | null>(null);

  constructor(private http: HttpClient) {
    const singleOrderString = localStorage.getItem("single-order")
    const groupOrderString = localStorage.getItem("group-order")
    if (singleOrderString)
      this.singleOrder = JSON.parse(singleOrderString);
    if (groupOrderString)
      this.groupOrder = JSON.parse(groupOrderString);
  }

  groupApiUrl: string = 'http://localhost:5005/api/orders/groups';
  singleApiUrl: string = 'http://localhost:5004/api/orders/singles';

  async joinGroupOrder(groupCode: string, userId: string): Promise<SingleOrder> {
    this.singleOrder = await lastValueFrom(this.http.post<SingleOrder>(`${this.singleApiUrl}`, {userId, groupCode}));
    localStorage.setItem("single-order", JSON.stringify(this.singleOrder));
    return this.singleOrder;
  }

  async getGroupOrder(groupCode: string): Promise<GroupOrder> {
    this.groupOrder = await lastValueFrom(this.http.get<GroupOrder>(`${this.groupApiUrl}/${groupCode}`))
    return this.groupOrder;
  }

  async getSingleOrder(groupCode: string): Promise<SingleOrder> {
    this.singleOrder = await lastValueFrom(this.http.get<SingleOrder>(`${this.singleApiUrl}?groupCode=${groupCode}`));
    this.singleOrder$.next(this.singleOrder);
    return this.singleOrder;
  }

  getSingleOrderLocal(): SingleOrder | undefined {
    return this.singleOrder;
  }

  async createGroupOrder(restaurantId: string, addressId: string, deliveryTime: string): Promise<GroupOrder> {
    this.groupOrder = await lastValueFrom(this.http.post<GroupOrder>(`${this.groupApiUrl}`, {
      restaurantId,
      addressId,
      deliveryTime
    }));
    localStorage.setItem("group-order", JSON.stringify(this.groupOrder));
    return this.groupOrder;
  }

  // maybe this should be in a different service
  // add quantity after merging discount branch

  addMenuItemToOrder(menuItemId: string, quantity: number) {
    this.http.post<void>(`${this.singleApiUrl}/${this.getOrderId()}/addMenuItem`, {menuItemId}).subscribe({
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

  getOrderId(): string {
    return this.singleOrder?.id ?? ''
  }

  getGroupCode(): string {
    return this.groupOrder?.groupCode ?? ''
  }
}
