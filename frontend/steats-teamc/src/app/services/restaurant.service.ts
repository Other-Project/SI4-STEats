import {Injectable, Injector} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject, lastValueFrom, Observable} from 'rxjs';
import {Restaurant} from '../models/restaurant.model';
import {MenuItem} from '../models/menuItem.model';
import {OrderService} from './order.service';
import {apiUrl} from '../app.config';

@Injectable({
  providedIn: 'root'
})
export class RestaurantService {
  private readonly apiUrl: string = `${apiUrl}/api/restaurants`;
  private restaurantId: string | null = null;
  private menu: MenuItem[] = [];

  public availableMenu$: BehaviorSubject<MenuItem[] | null> = new BehaviorSubject<MenuItem[] | null>(null);

  constructor(private readonly http: HttpClient, private readonly injector: Injector) {
    const restaurantIdString = localStorage.getItem("restaurantId");
    if (restaurantIdString) {
      this.restaurantId = JSON.parse(restaurantIdString);
      if (this.restaurantId) this.loadMenu(this.restaurantId);
    }
  }

  getRestaurants(): Observable<Restaurant[]> {
    return this.http.get<Restaurant[]>(this.apiUrl);
  }

  async getMenu(restaurantId: string): Promise<MenuItem[]> {
    this.restaurantId = restaurantId;
    this.menu = await lastValueFrom(this.http.get<MenuItem[]>(`${this.apiUrl}/${restaurantId}/menu`));
    localStorage.setItem("restaurantId", restaurantId);
    return this.menu;
  }

  getRestaurantId() {
    return this.restaurantId;
  }

  async getAvailableMenu(deliveryTime: Date): Promise<MenuItem[]> {
    const availableMenu = await lastValueFrom(this.http.get<MenuItem[]>(`${this.apiUrl}/${this.restaurantId}/menu?deliveryTime=${deliveryTime}&orderPreparationTime=${this.getOrderService().getSingleOrderLocal()?.preparationTime}`));
    this.availableMenu$.next(availableMenu);
    return availableMenu;
  }

  private getOrderService() {
    return this.injector.get(OrderService);
  }

  getMenuItemById(id: string): MenuItem | undefined {
    return this.menu.find(item => item.id === id);
  }

  async loadMenu(restaurantId: string) {
    this.menu = await this.getMenu(restaurantId);
  }
}
