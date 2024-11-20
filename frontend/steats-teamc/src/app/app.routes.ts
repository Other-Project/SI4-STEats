import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MenuItemContainerComponent} from './components/menuItem/menu-item-container/menu-item-container.component';
import { RestaurantContainerComponent } from './components/restaurant/restaurant-container/restaurant-container.component';

export const routes: Routes = [
  { path: '', component: RestaurantContainerComponent },
  { path: 'restaurant/:id', component: MenuItemContainerComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
