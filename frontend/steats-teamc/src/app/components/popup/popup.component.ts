import {Component} from '@angular/core';
import {PopupService} from '../../services/popup.service';
import {MatDialogActions, MatDialogContent, MatDialogTitle} from '@angular/material/dialog';
import {MatButton} from '@angular/material/button';

@Component({
  selector: 'app-popup',
  standalone: true,
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatButton
  ],
  templateUrl: './popup.component.html',
  styleUrl: './popup.component.scss'
})
export class PopupComponent {

  constructor(private popupService: PopupService) {
  }

  closeDialog() {
    this.popupService.closePopup()
  }
}
