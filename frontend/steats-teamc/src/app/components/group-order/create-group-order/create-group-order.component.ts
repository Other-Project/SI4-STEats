import {Component} from '@angular/core';
import {FormControl, Validators} from '@angular/forms';
import {OrderService} from '../../../services/order.service';
import {Router} from '@angular/router';
import {UserService} from '../../../services/user.service';

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

  constructor(private orderService: OrderService, private userService: UserService, private router: Router) {
  }

  public async createGroupOrder() {
    const userId = this.userService.getUserId();
    if (!userId) {
      // TODO: Show error message saying user is not logged in
      return;
    }
    const groupOrderData = {
      deliveryTime: this.deliveryTime.value,
      addressId: this.addressId.value,
      restaurantId: this.restaurantId
    };
    try {
      const groupOrder = await this.orderService.createGroupOrder(groupOrderData.restaurantId, groupOrderData.addressId, groupOrderData.deliveryTime);
      const singleOrder = await this.orderService.joinGroupOrder(groupOrder.groupCode, userId);
      this.orderService.setOrderId(singleOrder.id);
      this.orderService.setGroupCode(singleOrder.groupCode);
      await this.router.navigate(['/order', singleOrder.id]);
    } catch (error) {
      console.error('Failed to create or join group order:', error);
    }
  }
}
