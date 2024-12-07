import {Component} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {RestaurantService} from '../../../services/restaurant.service';
import {MenuItem} from '../../../models/menuItem.model';
import {OrderService} from '../../../services/order.service';
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

  constructor(private readonly route: ActivatedRoute,
              private readonly restaurantService: RestaurantService,
              private readonly orderService: OrderService) {
  }

  async ngOnInit(): Promise<void> {
    this.restaurantService.availableMenu$.subscribe((menu) => {
      if (menu)
        this.menuItems = menu;
    });
    const restaurantId = this.route.snapshot.paramMap.get('restaurantId');
    if (!restaurantId) return;
    this.menuItems = this.orderService.getOrderId()
      ? await this.restaurantService.getAvailableMenu(this.orderService.getSingleOrderLocal()?.deliveryTime)
      : await this.restaurantService.getMenu(restaurantId);
  }
}
