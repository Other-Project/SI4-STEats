import {Component} from '@angular/core';
import {PopupService} from '../../services/popup.service';
import {UserService} from '../../services/user.service';
import {CartPopupComponent} from '../popup/cart-popup/cart-popup.component';
import {OrderService} from '../../services/order.service';
import {CreateGroupOrderComponent} from '../group-order/create-group-order/create-group-order.component';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent {
  isLoggedIn = false;
  hasOrder = false;

  constructor(private readonly popupService: PopupService, private readonly userService: UserService, private readonly orderService: OrderService) {
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
    this.popupService.open(CreateGroupOrderComponent, {
      maxHeight: 'none',
      maxWidth: '10%',
    }, {})
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
