import {Component} from '@angular/core';
import {FormControl, Validators} from '@angular/forms';
import {GroupOrderService} from '../../../services/groupOrder.service';
import {PopupService} from '../../../services/popup.service';
import {UserService} from '../../../services/user.service';

@Component({
  selector: 'app-join-group-order',
  templateUrl: './join-group-order.component.html',
  styleUrls: ['./join-group-order.component.scss']
})
export class JoinGroupOrderComponent {
  public groupCode: FormControl<string> = new FormControl('', {validators: [Validators.required], nonNullable: true});

  constructor(private groupOrderService: GroupOrderService, private popupService: PopupService, private userService: UserService) {
  }

  public joinGroupOrder() {
    let userId: string | null = this.userService.getUserId();
    if (!userId) {
      //TODO: Show error message saying user is not logged in
      return;
    }
    this.groupOrderService.joinGroupOrder(this.groupCode.value, userId).subscribe({
      next: () => console.log('Joined group order'),
      error: (error) => console.error('Failed to join group order:', error)
    });
    this.popupService.closePopup();
  }
}
