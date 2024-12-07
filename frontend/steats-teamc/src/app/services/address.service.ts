import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Address} from '../models/address.model';
import {lastValueFrom} from 'rxjs';
import {apiUrl} from '../app.config';

@Injectable({
  providedIn: 'root'
})
export class AddressService {
  private readonly apiUrl = `${apiUrl}/api/address`;

  constructor(private readonly http: HttpClient) {
  }

  getAddressById(id: string) {
    return this.http.get<Address>(`${this.apiUrl}/${id}`);
  }

  async getAllAddresses(): Promise<Address[]> {
    return lastValueFrom(this.http.get<Address[]>(this.apiUrl));
  }
}
