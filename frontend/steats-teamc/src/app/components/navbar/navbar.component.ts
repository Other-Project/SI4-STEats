import {Component} from '@angular/core';
import {PopupService} from '../../services/popup.service';
import {UserService} from '../../services/user.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent {
  isLoggedIn = false;

  constructor(private popupService: PopupService, private userService: UserService) {
  }

  ngOnInit(): void {
    this.userService.isLoggedIn$.subscribe(isLoggedIn => {
      this.isLoggedIn = isLoggedIn;
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

  logout(): void {
    this.userService.logout();
  }
}
