import {Injectable, Injector} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject, lastValueFrom, Observable} from 'rxjs';
import {GroupOrder} from '../models/groupOrder.model';
import {SingleOrder} from '../models/singleOrder.model';
import {RestaurantService} from './restaurant.service';
import {apiUrl} from '../app.config';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private readonly groupApiUrl: string = `${apiUrl}/api/orders/groups`;
  private readonly singleApiUrl: string = `${apiUrl}/api/orders/singles`;
  private groupOrder: GroupOrder | undefined;
  private singleOrder: SingleOrder | undefined;

  public singleOrder$: BehaviorSubject<SingleOrder | null> = new BehaviorSubject<SingleOrder | null>(null);

  constructor(private readonly http: HttpClient, private readonly injector: Injector) {
    const singleOrderString = localStorage.getItem("single-order")
    const groupOrderString = localStorage.getItem("group-order")
    if (singleOrderString) {
      this.singleOrder = JSON.parse(singleOrderString);
      if (this.singleOrder)
        this.singleOrder$.next(this.singleOrder);
    }
    if (groupOrderString)
      this.groupOrder = JSON.parse(groupOrderString);
  }

  async joinGroupOrder(groupCode: string, userId: string): Promise<SingleOrder> {
    this.singleOrder = await lastValueFrom(this.http.post<SingleOrder>(`${this.singleApiUrl}`, {userId, groupCode}));
    if (this.singleOrder && this.groupOrder) {
      localStorage.setItem("single-order", JSON.stringify(this.singleOrder));
      localStorage.setItem("group-order", JSON.stringify(this.groupOrder));
      this.singleOrder$.next(this.singleOrder);
    }
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

  getSingleOrderLocalStorage(): SingleOrder | undefined {
    const singleOrderString = localStorage.getItem("single-order")
    if (singleOrderString) {
      return JSON.parse(singleOrderString);
    }
    return undefined;
  }

  async createGroupOrder(restaurantId: string, addressId: string, deliveryTime: string | undefined): Promise<GroupOrder> {
    this.groupOrder = await lastValueFrom(this.http.post<GroupOrder>(`${this.groupApiUrl}`, {
      restaurantId,
      addressId,
      deliveryTime
    }));
    return this.groupOrder;
  }

  changeMenuItemOfOrder(menuItemId: string, quantity: number): Promise<boolean> {
    return new Promise((resolve) => {
      this.http.post<SingleOrder>(`${this.singleApiUrl}/${this.getOrderId()}/modifyCartItem`, {
        menuItemId,
        quantity
      }).subscribe({
        next: (order) => {
          this.singleOrder = order;
          localStorage.setItem("single-order", JSON.stringify(order));
          this.singleOrder$.next(order);
          this.getRestaurantService().getAvailableMenu(order.deliveryTime);
          resolve(true);
        },
        error: () => {
          resolve(false);
        }
      });
    });
  }

  payForOrder(orderId: string): Observable<void> {
    return this.http.post<void>(`${this.singleApiUrl}/${orderId}/pay`, null);
  }

  getOrderId(): string {
    return this.singleOrder?.id ?? ''
  }

  getGroupCode(): string {
    return this.groupOrder?.groupCode ?? ''
  }

  getRestaurantService() {
    return this.injector.get(RestaurantService);
  }
}
