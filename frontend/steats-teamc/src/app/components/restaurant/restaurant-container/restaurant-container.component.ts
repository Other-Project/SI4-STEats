import {Component, Input} from '@angular/core';
import {RestaurantService} from '../../../services/restaurant.service';
import {Restaurant} from '../../../../models/restaurant.model';

@Component({
  selector: 'app-restaurant-container',
  templateUrl: './restaurant-container.component.html',
  styleUrl: './restaurant-container.component.scss'
})
export class RestaurantContainerComponent {
  @Input() restaurants: Restaurant[] = [];

  constructor(private restaurantService: RestaurantService) {
  }

  ngOnInit(): void {
    this.restaurantService.getRestaurants().subscribe({
      next: (data) => {
        this.restaurants = data;
      },
      error: (error) => {
        console.error('Error fetching restaurants', error);
      }
    });
  }
}
