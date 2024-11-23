import {Component} from '@angular/core';
import {PopupService} from '../../services/popup.service';
import {OrderService} from '../../services/order.service';

@Component({
  selector: 'app-popup',
  templateUrl: './group-order.component.html',
  styleUrl: './group-order.component.scss'
})
export class GroupOrderComponent {
  groupCode: string = '';

  constructor(
    private popupService: PopupService,
    private orderService: OrderService) {
  }
}
