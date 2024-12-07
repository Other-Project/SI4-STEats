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
  public groupOrder$: BehaviorSubject<GroupOrder | null> = new BehaviorSubject<GroupOrder | null>(null);
  public singleOrders$: BehaviorSubject<SingleOrder[]> = new BehaviorSubject<SingleOrder[]>([]);

  constructor(private readonly http: HttpClient, private readonly injector: Injector) {
    const singleOrderString = localStorage.getItem("single-order")
    const groupOrderString = localStorage.getItem("group-order")
    if (singleOrderString) {
      this.singleOrder = JSON.parse(singleOrderString);
      if (this.singleOrder)
        this.singleOrder$.next(this.singleOrder);
    }
    if (groupOrderString) {
      this.groupOrder = JSON.parse(groupOrderString);
      if (this.groupOrder)
        this.groupOrder$.next(this.groupOrder);
    }
  }

  async joinGroupOrder(groupCode: string, userId: string): Promise<SingleOrder> {
    this.singleOrder = await lastValueFrom(this.http.post<SingleOrder>(`${this.singleApiUrl}`, {userId, groupCode}));
    this.groupOrder = await this.getGroupOrder();
    if (this.singleOrder) {
      localStorage.setItem("single-order", JSON.stringify(this.singleOrder));
      this.singleOrder$.next(this.singleOrder);
    }
    return this.singleOrder;
  }

  async getGroupOrder(): Promise<GroupOrder> {
    if (!this.getGroupCode())
      return {} as GroupOrder;
    this.groupOrder = await lastValueFrom(this.http.get<GroupOrder>(`${this.groupApiUrl}/${this.getGroupCode()}`));
    this.groupOrder$.next(this.groupOrder);
    localStorage.setItem("group-order", JSON.stringify(this.groupOrder));
    return this.groupOrder;
  }

  async getSingleOrders() {
    if (!this.getGroupCode()) return;
    this.singleOrders$.next(await lastValueFrom(this.http.get<SingleOrder[]>(`${this.singleApiUrl}?groupCode=${this.getGroupCode()}`)));
  }

  getSingleOrderLocal(): SingleOrder | undefined {
    return this.singleOrder;
  }

  getSingleOrderLocalStorage() {
    const singleOrderString = localStorage.getItem("single-order")
    if (singleOrderString)
      return JSON.parse(singleOrderString);
  }

  async createGroupOrder(restaurantId: string, addressId: string, deliveryTime: string | undefined): Promise<GroupOrder> {
    this.groupOrder = await lastValueFrom(this.http.put<GroupOrder>(`${this.groupApiUrl}`, {
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
    return this.singleOrder?.id ?? '';
  }

  getGroupCode(): string | undefined {
    return this.groupOrder?.groupCode ?? this.singleOrder?.groupCode;
  }

  getRestaurantService() {
    return this.injector.get(RestaurantService);
  }

  async closeGroupOrder() {
    if (!this.getGroupCode())
      return;
    return lastValueFrom(this.http.post<void>(`${this.groupApiUrl}/${this.getGroupCode()}/close`, null));
  }
}
