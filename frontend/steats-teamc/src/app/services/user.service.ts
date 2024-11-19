import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { User } from '../../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:5002/api/users';
  private user: User | null = null;
  private isLoggedInSubject = new BehaviorSubject<boolean>(false);

  constructor(private http: HttpClient) {}

  login(username: string): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/?name=${username}`).pipe(
      tap(user => {
        this.user = user;
        this.isLoggedInSubject.next(true);
      })
    );
  }

  logout(): void {
    this.user = null;
    this.isLoggedInSubject.next(false);
  }

  getUser(): User | null {
    return this.user;
  }

  get isLoggedIn$(): Observable<boolean> {
    return this.isLoggedInSubject.asObservable();
  }
}
