import {Component, Input} from '@angular/core';
import {MenuItem} from '../../../models/menuItem.model';
import {PopupService} from '../../../services/popup.service';
import {OrderService} from '../../../services/order.service';

@Component({
  selector: 'app-menu-item',
  standalone: true,
  imports: [],
  templateUrl: './menu-item.component.html',
  styleUrls: ['./menu-item.component.scss']
})
export class MenuItemComponent {
  @Input() menuItem!: MenuItem;

  constructor(private popupService: PopupService, private orderService: OrderService) {
  }

  openDialog(): void {
    if (!this.orderService.getSingleOrderLocal()) {
      alert("You must be logged in");
      return;
    }
    const dialogRef = this.popupService.openMenuItemDialog(this.menuItem);
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.orderService.changeMenuItemOfOrder(this.menuItem.id, result.quantity);
      }
    });
  }
}
