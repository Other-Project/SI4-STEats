package fr.unice.polytech.steats.restaurant;

import fr.unice.polytech.steats.models.MenuItem;
import fr.unice.polytech.steats.utils.AbstractHandler;
import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.NotFoundException;
import fr.unice.polytech.steats.utils.openapi.*;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@ApiMasterRoute(name = "Restaurants", path = "/api/restaurants")
public class RestaurantHttpHandler extends AbstractHandler {
    public RestaurantHttpHandler(String subPath, Logger logger) {
        super(subPath, logger);
    }

    private RestaurantManager getManager() {
        return RestaurantManager.getInstance();
    }

    @ApiRoute(method = HttpUtils.GET, path = "", summary = "Get all restaurants")
    public List<Restaurant> getAll() {
        return getManager().getAll();
    }

    @ApiRoute(method = HttpUtils.PUT, path = "", summary = "Add a restaurant")
    public Restaurant add(@ApiBodyParam Restaurant restaurant) {
        getManager().add(restaurant);
        return restaurant;
    }

    @ApiRoute(method = HttpUtils.GET, path = "/{id}", summary = "Get a restaurant")
    public Restaurant get(@ApiPathParam(name = "id", description = "ID of the restaurant to get") String id) throws NotFoundException {
        return getManager().get(id);
    }

    @ApiRoute(method = HttpUtils.DELETE, path = "/{id}", summary = "Remove a restaurant")
    public void remove(@ApiPathParam(name = "id", description = "ID of the restaurant to delete") String id) throws NotFoundException {
        getManager().remove(id);
    }

    @ApiRoute(method = HttpUtils.GET, path = "/{id}/menu", summary = "Get the menu of a restaurant")
    public List<MenuItem> getMenu(
            @ApiPathParam(name = "id", description = "ID of the restaurant") String id,
            @ApiQueryParam(name = "deliveryTime", description = "Only gets the menu available for the given date-time") LocalDateTime deliveryTime,
            @ApiQueryParam(name = "preparationTime", description = "Current order preparation time") Duration preparationTime
    ) throws IOException, NotFoundException {
        Restaurant restaurant = getManager().get(id);
        if (deliveryTime != null) return restaurant.getAvailableMenu(deliveryTime, preparationTime == null ? Duration.ZERO : preparationTime);
        return restaurant.getFullMenu();
    }

    @ApiRoute(method = HttpUtils.POST, path = "/{id}/canHandlePreparationTime", summary = "Can the restaurant handle an order of a given preparation time for a given delivery date-time")
    public boolean canHandlePreparationTime(
            @ApiPathParam(name = "id", description = "ID of the restaurant") String id,
            @ApiBodyParam(name = "deliveryTime", description = "Time of delivery of the order") LocalDateTime deliveryTime,
            @ApiBodyParam(name = "preparationTime", description = "Order preparation time") Duration preparationTime
    ) throws IOException, NotFoundException {
        Restaurant restaurant = getManager().get(id);
        return restaurant.canHandlePreparationTime(preparationTime, deliveryTime);
    }

    @ApiRoute(method = HttpUtils.POST, path = "/{id}/canAddOrder", summary = "Can the restaurant add an order for a given delivery date-time")
    public boolean canAddOrder(
            @ApiPathParam(name = "id", description = "ID of the restaurant") String id,
            @ApiBodyParam(name = "deliveryTime", description = "Wanted delivery time") LocalDateTime deliveryTime
    ) throws IOException, NotFoundException {
        Restaurant restaurant = getManager().get(id);
        return restaurant.canAddOrder(deliveryTime);
    }

    @ApiRoute(method = HttpUtils.POST, path = "/{id}/canHandle", summary = "Check if a restaurant can handle an order")
    public boolean canHandle(
            @ApiPathParam(name = "id", description = "ID of the restaurant") String id,
            @ApiBodyParam(name = "preparationTime", description = "The time it takes to prepare the order") Duration preparationTime,
            @ApiBodyParam(name = "deliveryTime", description = "The time of delivery") LocalDateTime deliveryTime
    ) throws IOException, NotFoundException {
        Restaurant restaurant = getManager().get(id);
        return restaurant.canHandle(preparationTime, deliveryTime);
    }

    @ApiRoute(method = HttpUtils.GET, path = "/{id}/opening-times/{dayOfWeek}", summary = "Get the opening times of a restaurant for a given day of the week")
    public List<OpeningTime> getOpeningTimes(
            @ApiPathParam(name = "id", description = "ID of the restaurant") String id,
            @ApiPathParam(name = "dayOfWeek", description = "Day of the week") DayOfWeek dayOfWeek
    ) throws IOException, NotFoundException {
        return getManager().get(id).getOpeningTimes(dayOfWeek);
    }
}
