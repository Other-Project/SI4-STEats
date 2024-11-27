import {Component} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {RestaurantService} from '../../../services/restaurant.service';
import {MenuItem} from '../../../models/menuItem.model';
import {MenuItemComponent} from '../menu-item/menu-item.component';
import {NgForOf} from '@angular/common';
import {OrderService} from '../../../services/order.service';

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

  constructor(private route: ActivatedRoute,
              private restaurantService: RestaurantService,
              private orderService: OrderService) {
  }

  ngOnInit(): void {
    this.orderService.singleOrder$.subscribe((order) => {
      console.log('Order updated:', order);
    });
    const restaurantId = this.route.snapshot.paramMap.get('restaurantId');
    if (!restaurantId) return;
    if (!this.orderService.getOrderId()) {
      this.restaurantService.getMenu(restaurantId).subscribe({
        next: (menu) => {
          this.menuItems = menu;
        },
        error: (error) => {
          console.error('Error fetching menu', error);
        }
      });
    } else {
      const order = this.orderService.getSingleOrderLocal();
      if (order) {
        this.restaurantService.getAvailableMenu(order.deliveryTime);
      }
    }
  }
}
