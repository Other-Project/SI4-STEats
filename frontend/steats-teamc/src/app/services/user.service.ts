import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject, Observable} from 'rxjs';
import {tap} from 'rxjs/operators';
import {User} from '../models/user.model';
import {apiUrl} from '../app.config';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly apiUrl = `${apiUrl}/api/users`;
  private user: User | undefined;
  private readonly isLoggedInSubject = new BehaviorSubject<boolean>(false);

  constructor(private readonly http: HttpClient) {
    const userString = localStorage.getItem("user");
    if (!userString) return
    this.user = JSON.parse(userString)
    this.isLoggedInSubject.next(true);
  }

  login(username: string): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${username}`).pipe(
      tap(user => {
        this.user = user;
        localStorage.setItem("user", JSON.stringify(user))
        this.isLoggedInSubject.next(true);
      })
    );
  }

  logout(): void {
    localStorage.clear();
    this.user = undefined;
    this.isLoggedInSubject.next(false);
  }

  getUserId(): string | null {
    return this.user?.userId ?? null;
  }

  get isLoggedIn$(): Observable<boolean> {
    return this.isLoggedInSubject.asObservable();
  }
}
