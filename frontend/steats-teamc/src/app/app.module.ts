import {NgModule} from '@angular/core'
import {NavbarComponent} from './components/navbar/navbar.component'
import {SearchbarComponent} from './components/navbar/searchbar/searchbar.component';
import {NgForOf, NgIf, NgOptimizedImage} from '@angular/common';
import {MatButtonModule} from '@angular/material/button';
import {FaIconComponent} from '@fortawesome/angular-fontawesome';
import {RestaurantCardComponent} from './components/restaurant/restaurant-card/restaurant-card.component';
import {RestaurantContainerComponent} from './components/restaurant/restaurant-container/restaurant-container.component';

@NgModule({
  imports: [
    NgIf,
    NgOptimizedImage,
    MatButtonModule,
    FaIconComponent,
    NgForOf,
  ],
  providers: [],
  declarations: [
    NavbarComponent,
    SearchbarComponent,
    RestaurantCardComponent,
    RestaurantContainerComponent,
  ],
  exports: [NavbarComponent, SearchbarComponent, RestaurantContainerComponent],
})
export class AppModule {

}
