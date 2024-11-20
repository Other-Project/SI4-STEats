import {Component} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {RestaurantService} from '../../../services/restaurant.service';
import {MenuItem} from '../../../models/menuItem.model';
import {MenuItemComponent} from '../menu-item/menu-item.component';
import {NgForOf} from '@angular/common';

@Component({
  selector: 'app-menu-item-container',
  standalone: true,
  imports: [
    MenuItemComponent,
    NgForOf
  ],
  templateUrl: './menu-item-container.component.html',
  styleUrl: './menu-item-container.component.scss'
})
export class MenuItemContainerComponent {
  menuItems: MenuItem[] = [];

  constructor(private route: ActivatedRoute, private restaurantService: RestaurantService) {
  }

  ngOnInit(): void {
    const restaurantId = this.route.snapshot.paramMap.get('id');
    if (restaurantId) {
      this.restaurantService.getMenu(restaurantId).subscribe({
        next: (menu) => {
          this.menuItems = menu;
        },
        error: (error) => {
          console.error('Error fetching menu', error);
        }
      });
    }
  }
}
