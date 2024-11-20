import {Component, Input} from '@angular/core';
import {MenuItem} from '../../../../models/menuItem.model';

@Component({
  selector: 'app-menu-item',
  standalone: true,
  imports: [],
  templateUrl: './menu-item.component.html',
  styleUrl: './menu-item.component.scss'
})
export class MenuItemComponent {
  @Input() menuItem!: MenuItem;
}
