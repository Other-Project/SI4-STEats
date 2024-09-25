package fr.unice.polytech.steats.order;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class GroupOrder implements Order, Saleable{
    private final LocalTime delivery_time;
    private final String group_code;
    private final List<Order> orders = new ArrayList<>();

    public GroupOrder(String group_code, LocalTime delivery_time){
        this.delivery_time = delivery_time;
        this.group_code = group_code;
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

    public LocalTime getDelivery_time() {
        return delivery_time;
    }

    public String getGroup_code() {
        return group_code;
    }

    public Order createOrder(String userId){
        return new SingleOrder(userId, delivery_time);
    }

    public void closeGroupOrder() {

    }
}
