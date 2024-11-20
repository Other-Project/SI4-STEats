import {Role} from './role.model';

export interface User {
  username: string;
  role: Role;
  id: string;
}
