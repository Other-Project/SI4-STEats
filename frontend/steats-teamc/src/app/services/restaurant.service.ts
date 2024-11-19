import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {map, Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RestaurantService {
  private apiUrl: string = 'http://localhost:5006/api/restaurants';
  private defaultImageUrl: string = 'https://imageproxy.wolt.com/venue/65649f38d750e4643c883f83/b638f152-d345-11ee-b62a-6ec26134f011_fd436250_982e_11ee_9ac5_6ed35a7a8561_bk_hero__1_.jpg';
  private defaultImageUrl2: string = 'https://www.hachette.fr/sites/default/files/burger-verrecchia.jpg';


  constructor(private http: HttpClient) {}

  getRestaurants(): Observable<{ name: string, imageUrl: string, id: string }[]> {
    return this.http.get<{ name: string, id : string }[]>(this.apiUrl).pipe(
      map(restaurants => restaurants.map(restaurant => ({
        name: restaurant.name,
        imageUrl: this.defaultImageUrl,
        id: restaurant.id
      })))
    );
  }

  getMenu(restaurantId: string) {
    return this.http.get(`${this.apiUrl}/${restaurantId}/menu`).pipe(
      map((menu: any) => menu.map((menuItem: any) => ({
        imageUrl: this.defaultImageUrl2,
        id: menuItem.id,
        name: menuItem.name,
        price: menuItem.price,
        preparationTime: menuItem.preparationTime,
        restaurantId: menuItem.restaurantId,
      })))
    );
  }
}
