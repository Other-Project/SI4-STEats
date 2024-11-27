import {Component, Input} from '@angular/core';
import {FormControl, Validators} from '@angular/forms';
import {OrderService} from '../../../services/order.service';
import {Router} from '@angular/router';
import {UserService} from '../../../services/user.service';
import {RestaurantService} from '../../../services/restaurant.service';
import {PopupService} from '../../../services/popup.service';
import {faWarning} from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-create-group-order',
  templateUrl: './create-group-order.component.html',
  styleUrls: ['./create-group-order.component.scss']
})
export class CreateGroupOrderComponent {
  @Input() public userId: string | null;

  public deliveryTime: FormControl<string> = new FormControl('', {
    validators: [Validators.required],
    nonNullable: true
  });
  public addressId: FormControl<string> = new FormControl('', {validators: [Validators.required], nonNullable: true});
  public groupOrderCode: string | null = null;

  constructor(
    private orderService: OrderService,
    private userService: UserService,
    private router: Router,
    private restaurantService: RestaurantService,
    private popupService: PopupService
  ) {
    this.userId = this.userService.getUserId();
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
      this.orderService.setOrderId(singleOrder.id);
      this.orderService.setGroupCode(singleOrder.groupCode);
      this.groupOrderCode = groupOrder.groupCode;
      await this.router.navigate(['/restaurant', groupOrderData.restaurantId, '/single-order', singleOrder.id]);
    } catch (error) {
      console.error('Failed to create or join group order:', error);
    }
  }

  protected readonly faWarning = faWarning;
}
