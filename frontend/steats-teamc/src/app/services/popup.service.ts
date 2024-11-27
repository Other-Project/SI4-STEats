import {Injectable} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {GroupOrderComponent} from '../components/popup/group-order.component';
import {CreateOrderComponent} from '../components/popup/create-order/create-order.component';
import {LoginComponent} from '../components/popup/login/login.component';
import {MenuItem} from '../models/menuItem.model';
import {MenuItemDialogComponent} from '../components/menuItem/menu-item-dialog/menu-item-dialog.component';
import {ComponentType} from '@angular/cdk/portal';

@Injectable({
  providedIn: 'root',
})
export class PopupService {
  constructor(private dialog: MatDialog) {
  }

  openGroupPopup() {
    this.dialog.open(GroupOrderComponent);
  }

  createOrderPopup() {
    this.dialog.open(CreateOrderComponent);
  }

  openLoginPopup() {
    this.dialog.open(LoginComponent);
  }

  openMenuItemDialog(menuItem: MenuItem) {
    return this.dialog.open(MenuItemDialogComponent, {
      width: '250px',
      data: menuItem
    });
  }

  // use this open Dialog

  openDialog(component: ComponentType<any>, widh: string, data: any) {
    return this.dialog.open(component, {
        width: widh,
        data: data
      }
    )
  }
}
