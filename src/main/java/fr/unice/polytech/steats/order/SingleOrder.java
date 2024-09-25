package fr.unice.polytech.steats.order;

import java.time.LocalTime;

public class SingleOrder implements Order, Saleable{
    private final String user_id;
    private LocalTime delivery_time;

    public SingleOrder(String user_id, LocalTime delivery_time){
        this.user_id = user_id;
        this.delivery_time = delivery_time;
    }

    @Override
    public LocalTime getDeliveryTime() {
        return null;
    }

    @Override
    public Address getAddress() {
        return null;
    }

    @Override
    public double getPrice() {
        return 0;
    }

    public String getUser_id() {
        return user_id;
    }

    public LocalTime getDelivery_time() {
        return delivery_time;
    }
}
