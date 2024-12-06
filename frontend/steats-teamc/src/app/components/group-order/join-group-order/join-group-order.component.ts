import {Component} from '@angular/core';
import {FormControl, Validators} from '@angular/forms';
import {OrderService} from '../../../services/order.service';
import {UserService} from '../../../services/user.service';
import {Router} from '@angular/router';
import {RestaurantService} from '../../../services/restaurant.service';

@Component({
  selector: 'app-join-group-order',
  templateUrl: './join-group-order.component.html',
  styleUrls: ['./join-group-order.component.scss']
})
export class JoinGroupOrderComponent {
  public groupForm: FormControl<string> = new FormControl('', {validators: [Validators.required], nonNullable: true});
  public groupCode: string | undefined;

  constructor(private readonly orderService: OrderService, private readonly restaurantService: RestaurantService, private readonly userService: UserService, private readonly router: Router) {
    this.orderService.groupOrder$.subscribe(groupOrder => {
      this.groupCode = groupOrder?.groupCode;
    });
  }

  public async joinGroupOrder() {
    let userId: string | null = this.userService.getUserId();
    if (!userId) {
      //TODO: Show error message saying user is not logged in
      return;
    }
    try {
      let singleOrder = await this.orderService.joinGroupOrder(this.groupForm.value, userId);
      this.restaurantService.setRestaurantId(singleOrder.restaurantId);
      await this.router.navigate(['/restaurant', singleOrder.restaurantId]);
    } catch (error) {
      console.error('Failed to join group order:', error);
    }
  }
}
