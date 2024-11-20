import {Status} from './status.model';

export interface SingleOrder {
  id: string;
  userId: string;
  groupCode: string;
  deliveryTime: Date;
  addressId: string;
  restaurantId: string;
  status: Status;
  items: string[];
  discounts: string[];
  preparationTime: string;
  orderTime: Date;
  price: number;
}
