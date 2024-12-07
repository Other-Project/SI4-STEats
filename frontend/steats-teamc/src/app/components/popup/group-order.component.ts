import {Component} from '@angular/core';
import {PopupService} from '../../services/popup.service';
import {OrderService} from '../../services/order.service';
import {UserService} from '../../services/user.service';

@Component({
  selector: 'app-popup',
  templateUrl: './group-order.component.html',
  styleUrl: './group-order.component.scss'
})
export class GroupOrderComponent {
  public userId: string | null;
  public groupCode: string = '';

  constructor(
    private popupService: PopupService,
    private orderService: OrderService,
    private userService: UserService) {
    this.userId = this.userService.getUserId();
  }
}
