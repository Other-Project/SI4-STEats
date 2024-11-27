import {Component} from '@angular/core';
import {FormControl, Validators} from '@angular/forms';
import {OrderService} from '../../../services/order.service';
import {Router} from '@angular/router';
import {UserService} from '../../../services/user.service';
import {RestaurantService} from '../../../services/restaurant.service';
import {PopupService} from '../../../services/popup.service';

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
  public groupCode: string | undefined;

  constructor(
    private orderService: OrderService,
    private userService: UserService,
    private router: Router,
    private restaurantService: RestaurantService,
    private popupService: PopupService
  ) {
  }

  ngOnInit() {
    this.groupCode = this.orderService.getGroupCode();
  }

  public async createGroupOrder() {
    const userId = this.userService.getUserId();
    if (!userId) {
      // TODO: Show error message saying user is not logged in
      return;
    }
    const restaurantId = this.restaurantService.getRestaurantId() ?? '';
    const groupOrderData = {
      deliveryTime: this.deliveryTime.value,
      addressId: this.addressId.value,
      restaurantId: restaurantId
    };
    try {
      const groupOrder = await this.orderService.createGroupOrder(groupOrderData.restaurantId, groupOrderData.addressId, groupOrderData.deliveryTime);
      const singleOrder = await this.orderService.joinGroupOrder(groupOrder.groupCode, userId);
      this.groupCode = groupOrder.groupCode ?? undefined;
      await this.router.navigate(['/restaurant', groupOrderData.restaurantId]);
    } catch (error) {
      console.error('Failed to create or join group order:', error);
    }
  }
}
