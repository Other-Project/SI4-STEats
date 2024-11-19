import {Injectable} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {PopupComponent} from '../components/popup/popup.component';
import {CreateOrderComponent} from '../components/popup/create-order/create-order.component';
import {LoginComponent} from '../components/popup/login/login.component';

@Injectable({
  providedIn: 'root',
})
export class PopupService {
  constructor(private dialog: MatDialog) {
  }

  openGroupPopup() {
    this.dialog.open(PopupComponent);
  }

  closePopup() {
    this.dialog.closeAll();
  }

  createOrderPopup() {
    this.dialog.open(CreateOrderComponent);
  }

  openLoginPopup() {
    this.dialog.open(LoginComponent);
  }
}
