import {Status} from './status.model';

export interface GroupOrder {
  groupCode: string;
  deliveryTime: Date;
  addressId: string;
  restaurantId: string;
  status: Status;
  ordersId: string[];
  items: string[];
  discounts: string[];
  preparationTime: string;
  orderTime: Date;
  price: number;
}
