import {Component} from '@angular/core';
import {FormControl, Validators} from '@angular/forms';
import {OrderService} from '../../../services/order.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-create-group-order',
  templateUrl: './create-group-order.component.html',
  styleUrls: ['./create-group-order.component.scss']
})
export class CreateGroupOrderComponent {
  public deliveryTime: FormControl<string> = new FormControl('', {
    validators: [Validators.required],
    nonNullable: true
  });
  public addressId: FormControl<string> = new FormControl('', {validators: [Validators.required], nonNullable: true});
  public restaurantId: string = '1'; // Retrieve the restaurantId as with restaurant service or url

  constructor(private orderService: OrderService, private router: Router) {
  }

  public createGroupOrder() {
    const userId = 'someUserId'; // Retrieve the userId as needed
    const groupOrderData = {
      deliveryTime: this.deliveryTime.value,
      addressId: this.addressId.value,
      restaurantId: this.restaurantId
    };

    this.orderService.createGroupOrder(groupOrderData.restaurantId, groupOrderData.addressId, groupOrderData.deliveryTime).subscribe({
      next: (groupOrder) => {
        this.orderService.joinGroupOrder(groupOrder.groupCode, userId).subscribe({
          next: (singleOrder) => {
            // Redirect to the order page if needed
            this.orderService.setOrderId(singleOrder.id);
            this.orderService.setGroupCode(singleOrder.groupCode);
            this.router.navigate(['/order', singleOrder.id]).then(r => console.log(r));
          },
          error: (error) => console.error('Failed to join group order:', error)
        });
      },
      error: (error) => console.error('Failed to create group order:', error)
    });
  }
}
