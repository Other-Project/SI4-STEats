import {Status} from './status.model';

export interface SingleOrder {
  id: string;
  userId: string;
  groupCode: string;
  deliveryTime: Date;
  addressId: string;
  restaurantId: string;
  status: Status;
  orderedItems: { [key: string]: number };
  items: { [key: string]: number };
  discounts: string[];
  preparationTime: string;
  orderTime: Date;
  subPrice: number;
  price: number;
}
