import {Component, Input, OnInit} from '@angular/core';
import {FormControl, Validators} from '@angular/forms';
import {OrderService} from '../../../services/order.service';
import {Router} from '@angular/router';
import {UserService} from '../../../services/user.service';
import {RestaurantService} from '../../../services/restaurant.service';
import {PopupService} from '../../../services/popup.service';
import {faWarning} from '@fortawesome/free-solid-svg-icons';
import {AddressService} from '../../../services/address.service';
import {Address} from '../../../models/address.model';
import {DatePipe} from '@angular/common';

@Component({
  selector: 'app-create-group-order',
  templateUrl: './create-group-order.component.html',
  styleUrls: ['./create-group-order.component.scss'],
  providers: [DatePipe]
})
export class CreateGroupOrderComponent implements OnInit {
  @Input() public userId: string | null;
  public deliveryDate: FormControl<Date | null> = new FormControl(null);
  public deliveryTime: FormControl<string | null> = new FormControl(null);
  public addressId: FormControl<string> = new FormControl('', {validators: [Validators.required], nonNullable: true});
  public groupCode: string | undefined;
  public addresses: Address[] = [];

  constructor(
    private readonly orderService: OrderService,
    private readonly userService: UserService,
    private readonly router: Router,
    private readonly restaurantService: RestaurantService,
    private readonly addressService: AddressService,
    private readonly datePipe: DatePipe
  ) {
    this.orderService.groupOrder$.subscribe(groupOrder => {
      this.groupCode = groupOrder?.groupCode;
    });
    this.userId = this.userService.getUserId();
  }

  async ngOnInit() {
    this.addresses = await this.addressService.getAllAddresses();
  }

  public async createGroupOrder() {
    const userId = this.userService.getUserId();
    if (!userId) {
      // TODO: Show error message saying user is not logged in
      return;
    }
    const restaurantId = this.restaurantService.getRestaurantId() ?? '';
    const deliveryDate = this.datePipe.transform(this.deliveryDate.value, 'yyyy-MM-dd');
    const deliveryTime = this.deliveryTime.value;
    const deliveryDateTime = deliveryDate && deliveryTime ? `${deliveryDate}T${deliveryTime}:00` : undefined;

    const groupOrderData = {
      deliveryTime: deliveryDateTime,
      addressId: this.addressId.value,
      restaurantId: restaurantId
    };
    try {
      const groupOrder = await this.orderService.createGroupOrder(groupOrderData.restaurantId, groupOrderData.addressId, groupOrderData.deliveryTime);
      await this.orderService.joinGroupOrder(groupOrder.groupCode, userId);
      this.groupCode = groupOrder.groupCode ?? undefined;
      await this.router.navigate(['/restaurant', groupOrderData.restaurantId]);
    } catch (error) {
      console.error('Failed to create or join group order:', error);
    }
  }

  protected readonly faWarning = faWarning;
}
