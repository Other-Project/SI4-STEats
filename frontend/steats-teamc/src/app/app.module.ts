import {NgModule} from '@angular/core'
import {NavbarComponent} from './components/navbar/navbar.component'
import {SearchbarComponent} from './components/navbar/searchbar/searchbar.component';

@NgModule({
  imports: [],
  declarations: [NavbarComponent, SearchbarComponent],
  exports: [NavbarComponent, SearchbarComponent]
})
export class AppModule {

}
