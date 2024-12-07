import {Component, Input} from '@angular/core';
import {Router} from '@angular/router';
import {Restaurant} from '../../../models/restaurant.model';

@Component({
  selector: 'app-restaurant-card',
  templateUrl: './restaurant-card.component.html',
  styleUrls: ['./restaurant-card.component.scss']
})
export class RestaurantCardComponent {
  @Input() restaurant!: Restaurant;

  constructor(private readonly router: Router) {
  }

  onCardClick(): void {
    this.router.navigate(['/restaurant', this.restaurant.id]).then(r => console.log(r));
  }
}
