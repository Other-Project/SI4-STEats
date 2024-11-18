import {Injectable} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {PopupComponent} from '../components/popup/popup.component';

@Injectable({
  providedIn: 'root',
})
export class PopupService {
  constructor(private dialog: MatDialog) {
  }

  openPopup() {
    this.dialog.open(PopupComponent);
  }

  closePopup() {
    this.dialog.closeAll();
  }
}
