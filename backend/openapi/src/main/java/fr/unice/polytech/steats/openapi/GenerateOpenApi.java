package fr.unice.polytech.steats.openapi;

import fr.unice.polytech.steats.address.AddressHttpHandler;
import fr.unice.polytech.steats.discounts.applied.AppliedDiscountHttpHandler;
import fr.unice.polytech.steats.discounts.restaurant.RestaurantDiscountHttpHandler;
import fr.unice.polytech.steats.menuitem.MenuItemHttpHandler;
import fr.unice.polytech.steats.order.OrderHttpHandler;
import fr.unice.polytech.steats.order.groups.GroupOrderHttpHandler;
import fr.unice.polytech.steats.order.singles.SingleOrderHttpHandler;
import fr.unice.polytech.steats.payments.PaymentsHttpHandler;
import fr.unice.polytech.steats.restaurant.RestaurantHttpHandler;
import fr.unice.polytech.steats.schedule.ScheduleHttpHandler;
import fr.unice.polytech.steats.users.UserHttpHandler;
import fr.unice.polytech.steats.utils.openapi.OpenAPIGenerator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class GenerateOpenApi {
    public static void main(String[] args) throws IOException {
        String json = OpenAPIGenerator.generate(
                PaymentsHttpHandler.class,
                AddressHttpHandler.class,
                UserHttpHandler.class,
                MenuItemHttpHandler.class,
                RestaurantHttpHandler.class,
                ScheduleHttpHandler.class,
                OrderHttpHandler.class,
                SingleOrderHttpHandler.class,
                GroupOrderHttpHandler.class,
                RestaurantDiscountHttpHandler.class,
                AppliedDiscountHttpHandler.class
        );

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("openapi.json"))) {
            writer.write(json);
        }
    }
}
