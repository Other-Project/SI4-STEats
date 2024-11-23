import {Component} from '@angular/core';
import {FormControl, Validators} from '@angular/forms';
import {GroupOrderService} from '../../../services/groupOrder.service';
import {PopupService} from '../../../services/popup.service';

@Component({
  selector: 'app-join-group-order',
  templateUrl: './join-group-order.component.html',
  styleUrls: ['./join-group-order.component.scss']
})
export class JoinGroupOrderComponent {
  public groupCode: FormControl<string> = new FormControl('', {validators: [Validators.required], nonNullable: true});

  constructor(private groupOrderService: GroupOrderService, private popupService: PopupService) {
  }

  public joinGroupOrder() {
    this.groupOrderService.joinGroupOrder(this.groupCode.value, "1").subscribe({
      next: () => console.log('Joined group order'),
      error: (error) => console.error('Failed to join group order:', error)
    });
    this.popupService.closePopup();
  }
}
