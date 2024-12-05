import {Component} from '@angular/core';
import {PopupService} from '../../services/popup.service';
import {UserService} from '../../services/user.service';
import {CartPopupComponent} from '../popup/cart-popup/cart-popup.component';
import {OrderService} from '../../services/order.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent {
  isLoggedIn = false;
  hasOrder = false;

  constructor(private popupService: PopupService, private userService: UserService, private orderService: OrderService) {
  }

  ngOnInit(): void {
    this.userService.isLoggedIn$.subscribe(isLoggedIn => {
      this.isLoggedIn = isLoggedIn;
    });

    this.orderService.singleOrder$.subscribe(order => {
      this.hasOrder = !!order;
    });
  }

  public openGroupOrderPopup() {
    this.popupService.openGroupPopup()
  }

  createOrder() {
    this.popupService.createOrderPopup()
  }

  openLoginPopup() {
    this.popupService.openLoginPopup()
  }

  openCartPopup(): void {
    this.popupService.open(CartPopupComponent,
      {
        width: '70%',
        height: '70%',
        maxWidth: 'none',
      },
      {});
  }

  logout(): void {
    this.userService.logout();
  }
}
