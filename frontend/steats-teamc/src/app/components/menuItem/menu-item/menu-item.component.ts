import {Component, Input} from '@angular/core';
import {MenuItem} from '../../../models/menuItem.model';
import {PopupService} from '../../../services/popup.service';
import {OrderService} from '../../../services/order.service';
import {RestaurantService} from '../../../services/restaurant.service';

@Component({
  selector: 'app-menu-item',
  standalone: true,
  imports: [],
  templateUrl: './menu-item.component.html',
  styleUrls: ['./menu-item.component.scss']
})
export class MenuItemComponent {
  @Input() menuItem!: MenuItem;

  constructor(private popupService: PopupService, private orderService: OrderService,
              private restaurantService: RestaurantService) {
  }

  openDialog(): void {
    if (!this.orderService.getSingleOrderLocal()) {
      alert("You must be logged in");
    }
    const dialogRef = this.popupService.openMenuItemDialog(this.menuItem);
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.orderService.addMenuItemToOrder(this.menuItem.id, result.quantity);
      }
    });
  }
}
