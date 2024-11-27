import {Component, OnInit} from '@angular/core';
import {MenuItemContainerComponent} from '../../menuItem/menu-item-container/menu-item-container.component';
import {ActivatedRoute, Router} from '@angular/router';
import {OrderService} from '../../../services/order.service';


@Component({
  selector: 'app-single-order',
  standalone: true,
  imports: [MenuItemContainerComponent],
  templateUrl: './single-order.component.html',
  styleUrls: ['./single-order.component.scss']
})
export class SingleOrderComponent implements OnInit {
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private orderService: OrderService
  ) {
  }

  ngOnInit(): void {
    const singleOrderId = this.route.snapshot.paramMap.get('single-orderId');
    if (this.orderService.getOrderId() !== singleOrderId) {
      this.router.navigate(['/404']);
    }
  }
}
