import {Injectable} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {GroupOrderComponent} from '../components/popup/group-order.component';
import {CreateOrderComponent} from '../components/popup/create-order/create-order.component';
import {LoginComponent} from '../components/popup/login/login.component';

@Injectable({
  providedIn: 'root',
})
export class PopupService {
  constructor(private dialog: MatDialog) {
  }

  openGroupPopup() {
    this.dialog.open(GroupOrderComponent);
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
