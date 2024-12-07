import {Injectable} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {GroupOrderComponent} from '../components/popup/group-order.component';
import {LoginComponent} from '../components/popup/login/login.component';
import {MenuItem} from '../models/menuItem.model';
import {MenuItemDialogComponent} from '../components/menuItem/menu-item-dialog/menu-item-dialog.component';
import {ComponentType} from '@angular/cdk/portal';

@Injectable({
  providedIn: 'root',
})
export class PopupService {
  constructor(private readonly dialog: MatDialog) {
  }

  openGroupPopup() {
    this.dialog.open(GroupOrderComponent);
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

  open(component: ComponentType<any>, config: any, data: any) {
    return this.dialog.open(component, {
      ...config,
      data
    });
  }
}
