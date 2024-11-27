import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {lastValueFrom, Observable} from 'rxjs';
import {Restaurant} from '../models/restaurant.model';
import {MenuItem} from '../models/menuItem.model';
import {OrderService} from './order.service';

@Injectable({
  providedIn: 'root'
})
export class RestaurantService {
  private apiUrl: string = 'http://localhost:5006/api/restaurants';

  private restaurantId: string | null = null;

  constructor(private http: HttpClient, private orderService: OrderService) {

  }

  getRestaurants(): Observable<Restaurant[]> {
    return this.http.get<Restaurant[]>(this.apiUrl);
  }

  getMenu(restaurantId: string): Observable<MenuItem[]> {
    this.restaurantId = restaurantId;
    return this.http.get<MenuItem[]>(`${this.apiUrl}/${restaurantId}/menu`);
  }

  getRestaurantId() {
    return this.restaurantId;
  }

  getAvailableMenu(deliveryTime: Date): Promise<MenuItem[]> {
    return lastValueFrom(this.http.get<MenuItem[]>(`${this.apiUrl}/${this.restaurantId}/menu?deliveryTime=${deliveryTime}`));
  }
}
