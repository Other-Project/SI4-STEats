import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core'
import {NavbarComponent} from './components/navbar/navbar.component'
import {SearchbarComponent} from './components/navbar/searchbar/searchbar.component';
import {NgForOf, NgIf, NgOptimizedImage} from '@angular/common';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {FaIconComponent} from '@fortawesome/angular-fontawesome';
import {RestaurantCardComponent} from './components/restaurant/restaurant-card/restaurant-card.component';
import {
  RestaurantContainerComponent
} from './components/restaurant/restaurant-container/restaurant-container.component';
import {CreateGroupOrderComponent} from './components/group-order/create-group-order/create-group-order.component';
import {JoinGroupOrderComponent} from './components/group-order/join-group-order/join-group-order.component';
import {MatFormField, MatFormFieldModule, MatLabel} from '@angular/material/form-field';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatInput, MatInputModule} from '@angular/material/input';
import {GroupOrderComponent} from './components/popup/group-order.component';
import {MatTab, MatTabGroup} from '@angular/material/tabs';
import {MatDialogActions, MatDialogContent, MatDialogTitle} from '@angular/material/dialog';
import {MenuItemDialogComponent} from './components/menuItem/menu-item-dialog/menu-item-dialog.component';
import {MatOption, MatSelect} from "@angular/material/select";
import {
  MatDatepicker,
  MatDatepickerInput,
  MatDatepickerModule,
  MatDatepickerToggle
} from '@angular/material/datepicker';
import {MAT_DATE_LOCALE, MatNativeDateModule} from '@angular/material/core';
import {GrouperOrderLobbyComponent} from './components/group-order/grouper-order-lobby/grouper-order-lobby.component';

@NgModule({
  imports: [
    NgIf,
    NgOptimizedImage,
    MatButtonModule,
    FaIconComponent,
    MatIconModule,
    NgForOf,
    MatFormField,
    FormsModule,
    MatInput,
    MatLabel,
    MatTabGroup,
    MatTab,
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelect,
    MatOption,
    MatDatepickerToggle,
    MatDatepicker,
    MatDatepickerInput,
    MatNativeDateModule,
    MatDatepickerModule,
    GrouperOrderLobbyComponent,
  ],
  providers: [{provide: MAT_DATE_LOCALE, useValue: 'fr'}],
  declarations: [
    NavbarComponent,
    SearchbarComponent,
    RestaurantCardComponent,
    RestaurantContainerComponent,
    CreateGroupOrderComponent,
    JoinGroupOrderComponent,
    GroupOrderComponent,
    MenuItemDialogComponent,
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  exports: [NavbarComponent, SearchbarComponent, RestaurantContainerComponent, CreateGroupOrderComponent, JoinGroupOrderComponent],
})
export class AppModule {

}
