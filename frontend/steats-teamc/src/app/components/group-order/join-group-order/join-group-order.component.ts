import {Component, Input} from '@angular/core';
import {FormControl, Validators} from '@angular/forms';
import {OrderService} from '../../../services/order.service';
import {PopupService} from '../../../services/popup.service';
import {UserService} from '../../../services/user.service';
import {Router} from '@angular/router';
import {addWarning} from "@angular-devkit/build-angular/src/utils/webpack-diagnostics";
import {faWarning} from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-join-group-order',
  templateUrl: './join-group-order.component.html',
  styleUrls: ['./join-group-order.component.scss']
})
export class JoinGroupOrderComponent {
  @Input() public userId: string | null;
  public groupCode: FormControl<string> = new FormControl('', {validators: [Validators.required], nonNullable: true});

  constructor(private orderService: OrderService, private popupService: PopupService, private userService: UserService, private router: Router) {
    this.userId = this.userService.getUserId();
  }

  public async joinGroupOrder() {
    try {
      let singleOrder = await this.orderService.joinGroupOrder(this.groupCode.value, this.userId!);
      this.orderService.setOrderId(singleOrder.id);
      this.orderService.setGroupCode(singleOrder.groupCode);
      this.router.navigate(['/order', singleOrder.id]).then(r => console.log(r));
      this.popupService.closePopup();
    } catch (error) {
      console.error('Failed to join group order:', error);
    }
  }

    protected readonly addWarning = addWarning;
  protected readonly faWarning = faWarning;
}
