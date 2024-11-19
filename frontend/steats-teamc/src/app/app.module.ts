import {NgModule} from '@angular/core'
import {NavbarComponent} from './components/navbar/navbar.component'
import {SearchbarComponent} from './components/navbar/searchbar/searchbar.component';
import {NgIf, NgOptimizedImage} from '@angular/common';
import {MatButtonModule} from '@angular/material/button';
import {FaIconComponent} from '@fortawesome/angular-fontawesome';


@NgModule({
  imports: [
    NgIf,
    NgOptimizedImage,
    MatButtonModule,
    FaIconComponent,
  ],
  declarations: [NavbarComponent, SearchbarComponent],
  exports: [NavbarComponent, SearchbarComponent]
})
export class AppModule {

}
