import {Component, OnInit} from '@angular/core';
import {OrderService} from '../../../services/order.service';
import {NgClass, NgForOf, NgIf} from '@angular/common';
import {MatButton} from '@angular/material/button';
import {MatList, MatListItem} from '@angular/material/list';
import {SingleOrder} from '../../../models/singleOrder.model';

@Component({
  selector: 'app-grouper-order-lobby',
  standalone: true,
  imports: [
    NgForOf,
    MatButton,
    MatListItem,
    NgClass,
    MatList,
    NgIf
  ],
  templateUrl: './grouper-order-lobby.component.html',
  styleUrls: ['./grouper-order-lobby.component.scss']
})
export class GrouperOrderLobbyComponent implements OnInit {
  singleOrders: SingleOrder[] = [];

  constructor(private readonly orderService: OrderService) {
    this.orderService.singleOrders$.subscribe(singleOrders => {
      this.singleOrders = singleOrders;
    });
  }

  async ngOnInit() {
    await this.orderService.getSingleOrders();
  }

  async closeOrder() {
    await this.orderService.closeGroupOrder();
  }
}
