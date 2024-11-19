import {Component} from '@angular/core';
import {PopupService} from '../../services/popup.service';
import {GroupOrderService} from '../../services/groupOrder.service';
import {MatDialogActions, MatDialogContent, MatDialogTitle} from '@angular/material/dialog';
import {MatButton} from '@angular/material/button';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatButtonToggleModule} from '@angular/material/button-toggle';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-popup',
  standalone: true,
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatButton,
    MatFormFieldModule,
    MatInputModule,
    MatButtonToggleModule,
    FormsModule
  ],
  templateUrl: './popup.component.html',
  styleUrl: './popup.component.scss'
})
export class PopupComponent {
  selectedOption: string = 'join';
  groupCode: string = '';

  constructor(private popupService: PopupService, private groupOrderService: GroupOrderService) {}

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
