import {Component} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {AppModule} from '../../app.module';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, AppModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'steats-teamc';
}
