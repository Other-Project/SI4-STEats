import {Component} from '@angular/core';
import {PopupService} from '../../services/popup.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent {

  constructor(private popupService: PopupService) {
  }

  public openGroupOrderPopup() {
    this.popupService.openPopup();
  }
}
