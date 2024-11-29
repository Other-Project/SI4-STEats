import {Component} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {RestaurantService} from '../../../services/restaurant.service';
import {MenuItem} from '../../../models/menuItem.model';
import {OrderService} from '../../../services/order.service';

@Component({
  selector: 'app-menu-item-container',
  standalone: true,
  imports: [],
  templateUrl: './menu-item-container.component.html',
  styleUrl: './menu-item-container.component.scss'
})
export class MenuItemContainerComponent {
  menuItems: MenuItem[] = [];

  constructor(private route: ActivatedRoute,
              private restaurantService: RestaurantService,
              private orderService: OrderService) {
  }

  async ngOnInit(): Promise<void> {
    this.orderService.singleOrder$.subscribe((order) => {
      console.log('Order updated:', order);
    });
    this.restaurantService.availableMenu$.subscribe((menu) => {
      if (menu)
        this.menuItems = menu;
    });
    const restaurantId = this.route.snapshot.paramMap.get('restaurantId');
    if (!restaurantId) return;
    if (!this.orderService.getOrderId()) {
      this.menuItems = await this.restaurantService.getMenu(restaurantId);
    } else {
      const order = this.orderService.getSingleOrderLocal();
      if (order) {
        this.menuItems = await this.restaurantService.getAvailableMenu(order.deliveryTime);
      }
    }
  }
}
