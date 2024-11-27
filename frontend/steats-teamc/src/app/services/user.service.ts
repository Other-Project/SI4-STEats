import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject, Observable} from 'rxjs';
import {tap} from 'rxjs/operators';
import {User} from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:5002/api/users';
  private user: User | undefined;
  private isLoggedInSubject = new BehaviorSubject<boolean>(false);

  constructor(private http: HttpClient) {
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
    console.log("ebhfoinefp")
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
