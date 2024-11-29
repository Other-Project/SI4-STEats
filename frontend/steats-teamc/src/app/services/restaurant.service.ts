import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject, lastValueFrom, Observable} from 'rxjs';
import {Restaurant} from '../models/restaurant.model';
import {MenuItem} from '../models/menuItem.model';
import {OrderService} from './order.service';

@Injectable({
  providedIn: 'root'
})
export class RestaurantService {
  private apiUrl: string = 'http://localhost:5006/api/restaurants';

  private restaurantId: string | null = null;

  public availableMenu$: BehaviorSubject<MenuItem[] | null> = new BehaviorSubject<MenuItem[] | null>(null);

  constructor(private http: HttpClient, private orderService: OrderService) {
    const restaurantIdString = localStorage.getItem("restaurantId");
    if (restaurantIdString)
      this.restaurantId = JSON.parse(restaurantIdString);
  }

  getRestaurants(): Observable<Restaurant[]> {
    return this.http.get<Restaurant[]>(this.apiUrl);
  }

  async getMenu(restaurantId: string): Promise<MenuItem[]> {
    this.restaurantId = restaurantId;
    localStorage.setItem("restaurantId", restaurantId);
    return lastValueFrom(this.http.get<MenuItem[]>(`${this.apiUrl}/${restaurantId}/menu`));
  }

  getRestaurantId() {
    return this.restaurantId;
  }

  async getAvailableMenu(deliveryTime: Date): Promise<MenuItem[]> {
    const availableMenu = await lastValueFrom(this.http.get<MenuItem[]>(`${this.apiUrl}/${this.restaurantId}/menu?deliveryTime=${deliveryTime}&orderPreparationTime=${this.orderService.getSingleOrderLocal()?.preparationTime}`));
    this.availableMenu$.next(availableMenu);
    return availableMenu;
  }
}
