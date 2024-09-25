package fr.unice.polytech.steats.order;

import java.time.LocalTime;

public interface Order extends Saleable {
    LocalTime getDeliveryTime();
    Address getAddress();
}
