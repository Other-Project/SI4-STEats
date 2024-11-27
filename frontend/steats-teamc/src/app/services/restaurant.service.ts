import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Restaurant} from '../models/restaurant.model';
import {MenuItem} from '../models/menuItem.model';
import {OrderService} from './order.service';

@Injectable({
  providedIn: 'root'
})
export class RestaurantService {
  private apiUrl: string = 'http://localhost:5006/api/restaurants';
  private defaultImageUrl: string = 'https://imageproxy.wolt.com/venue/65649f38d750e4643c883f83/b638f152-d345-11ee-b62a-6ec26134f011_fd436250_982e_11ee_9ac5_6ed35a7a8561_bk_hero__1_.jpg';
  private defaultImageUrl2: string = 'https://www.hachette.fr/sites/default/files/burger-verrecchia.jpg';

  private restaurantId: string | null = null;

  constructor(private http: HttpClient, private orderService: OrderService) {
  }

  getRestaurants(): Observable<Restaurant[]> {
    return this.http.get<Restaurant[]>(this.apiUrl);
  }

  getMenu(restaurantId: string) : Observable<MenuItem[]> {
    this.restaurantId = restaurantId;
    return this.http.get<MenuItem[]>(`${this.apiUrl}/${restaurantId}/menu`);
  }

  getRestaurantId() {
    return this.restaurantId;
  }
}
