import {Component, OnInit} from '@angular/core';
import {MatDialogRef, MatDialogTitle} from '@angular/material/dialog';
import {OrderService} from '../../../services/order.service';
import {SingleOrder} from '../../../models/singleOrder.model';
import {MatButton} from '@angular/material/button';
import {CurrencyPipe, KeyValuePipe, NgForOf, NgIf} from '@angular/common';
import {MenuItem} from '../../../models/menuItem.model';
import {RestaurantService} from '../../../services/restaurant.service';
import {MatIcon} from '@angular/material/icon';
import {MatProgressSpinner} from '@angular/material/progress-spinner';

@Component({
  selector: 'app-cart-popup',
  templateUrl: './cart-popup.component.html',
  standalone: true,
  imports: [
    MatButton,
    NgForOf,
    NgIf,
    KeyValuePipe,
    CurrencyPipe,
    MatDialogTitle,
    MatIcon,
    MatProgressSpinner
  ],
  styleUrls: ['./cart-popup.component.scss']
})
export class CartPopupComponent implements OnInit {
  order: SingleOrder | null | undefined;
  hasConfirmedOrder = false;
  isLoading = false;
  isPaymentSuccessful = false;

  constructor(
    private readonly dialogRef: MatDialogRef<CartPopupComponent>,
    private readonly orderService: OrderService,
    private readonly restaurantService: RestaurantService
  ) {
  }

  ngOnInit(): void {
    this.orderService.singleOrder$.subscribe(order => {
      this.order = order;
    });
  }

  getMenuItemById(id: string): MenuItem | undefined {
    return this.restaurantService.getMenuItemById(id);
  }

  increaseQuantity(id: string): void {
    if (!this.order) return;
    const currentQuantity = this.order.items[id];
    if (currentQuantity)
      this.order.items[id] = currentQuantity + 1;
  }

  decreaseQuantity(id: string): void {
    if (!this.order) return;
    const currentQuantity = this.order.items[id];
    if (currentQuantity)
      this.order.items[id] = currentQuantity - 1;
  }

  async confirmOrder(): Promise<void> {
    if (!this.order) return;
    const menuItemMap = this.order ? Object.entries(this.order.items) : [];
    if (!menuItemMap) return;
    for (const [id, quantity] of menuItemMap) {
      const currentQuantity = this.orderService.getSingleOrderLocalStorage()?.items[id];
      if (currentQuantity !== undefined && currentQuantity !== quantity) {
        const success = await this.orderService.changeMenuItemOfOrder(id, quantity);
        if (!success) {
          // show error message
          this.order.items[id] = currentQuantity;
          return;
        }
      }
    }
    this.hasConfirmedOrder = true;
  }

  payOrder(id: string | undefined): void {
    if (!id) return;
    this.isLoading = true;
    this.orderService.payForOrder(id).subscribe({
      next: () => {
        this.isLoading = false;
        this.isPaymentSuccessful = true;
      },
      error: (err) => {
        this.isLoading = false;
        console.error('Payment failed', err);
      }
    });
  }

  close(): void {
    this.dialogRef.close();
  }
}
