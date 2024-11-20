import {Component} from '@angular/core';
import {PopupService} from '../../services/popup.service';
import {GroupOrderService} from '../../services/groupOrder.service';

@Component({
  selector: 'app-popup',
  templateUrl: './group-order.component.html',
  styleUrl: './group-order.component.scss'
})
export class GroupOrderComponent {
  groupCode: string = '';

  constructor(
    private popupService: PopupService,
    private groupOrderService: GroupOrderService) {
  }

  async closeDialog(selectedOption: string, groupCode: string) {
    if (selectedOption === 'create') {
      this.groupOrderService.createGroupOrder().subscribe({
        next: () => console.log('Created group order'),
        error: (error) => console.error('Failed to create group order:', error)
      });
    } else if (selectedOption === 'join') {
      this.groupOrderService.joinGroupOrder(groupCode, "1").subscribe({
        next: () => console.log('Joined group order'),
        error: (error) => console.error('Failed to join group order:', error)
      });
    }
    this.popupService.closePopup();
  }
}
