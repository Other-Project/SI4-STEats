import {Component} from '@angular/core';
import {FormControl, Validators} from '@angular/forms';
import {OrderService} from '../../../services/order.service';
import {PopupService} from '../../../services/popup.service';
import {UserService} from '../../../services/user.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-join-group-order',
  templateUrl: './join-group-order.component.html',
  styleUrls: ['./join-group-order.component.scss']
})
export class JoinGroupOrderComponent {
  public groupCode: FormControl<string> = new FormControl('', {validators: [Validators.required], nonNullable: true});

  constructor(private orderService: OrderService, private popupService: PopupService, private userService: UserService, private router: Router) {
  }

  public joinGroupOrder() {
    let userId: string | null = this.userService.getUserId();
    if (!userId) {
      //TODO: Show error message saying user is not logged in
      return;
    }
    this.orderService.joinGroupOrder(this.groupCode.value, userId).subscribe({
      next: (order) => {
        this.router.navigate(['/order', order.id]).then(r => console.log(r));
        this.popupService.closePopup();
      },
      error: (error) => {
        //TODO: Show error message saying group order does not exist
        console.error('Failed to join group order:', error);
      }
    });
  }
}
