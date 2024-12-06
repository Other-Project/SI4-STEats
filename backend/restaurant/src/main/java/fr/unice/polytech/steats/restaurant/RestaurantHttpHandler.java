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

    @ApiRoute(method = HttpUtils.GET, path = "", description = "Get all restaurants")
    public List<Restaurant> getAll() {
        return getManager().getAll();
    }

    @ApiRoute(method = HttpUtils.PUT, path = "", description = "Add a restaurant")
    public Restaurant add(@ApiBodyParam Restaurant restaurant) {
        getManager().add(restaurant);
        return restaurant;
    }

    @ApiRoute(method = HttpUtils.GET, path = "/{id}", description = "Get a restaurant")
    public Restaurant get(@ApiPathParam(name = "id", description = "ID of the restaurant to get") String id) throws NotFoundException {
        return getManager().get(id);
    }

    @ApiRoute(method = HttpUtils.DELETE, path = "/{id}", description = "Remove a restaurant")
    public void remove(@ApiPathParam(name = "id", description = "ID of the restaurant to delete") String id) throws NotFoundException {
        getManager().remove(id);
    }

    @ApiRoute(method = HttpUtils.GET, path = "/{id}/menu", description = "Get the menu of a restaurant")
    public List<MenuItem> getMenu(
            @ApiPathParam(name = "id", description = "ID of the restaurant") String id,
            @ApiQueryParam(name = "deliveryTime", description = "Only gets the menu available for the given date-time") LocalDateTime deliveryTime
    ) throws IOException, NotFoundException {
        Restaurant restaurant = getManager().get(id);
        if (deliveryTime != null) return restaurant.getAvailableMenu(deliveryTime);
        return restaurant.getFullMenu();
    }

    @ApiRoute(method = HttpUtils.POST, path = "/{id}/canHandle", description = "Check if a restaurant can handle an order")
    public boolean canHandle(
            @ApiPathParam(name = "id", description = "ID of the restaurant") String id,
            @ApiBodyParam(name = "preparationTime", description = "The time it takes to prepare the order") Duration preparationTime,
            @ApiBodyParam(name = "deliveryTime", description = "The time of delivery") LocalDateTime deliveryTime
    ) throws IOException, NotFoundException {
        Restaurant restaurant = getManager().get(id);
        return restaurant.canHandle(preparationTime, deliveryTime);
    }

    @ApiRoute(method = HttpUtils.GET, path = "/{id}/opening-times/{dayOfWeek}", description = "Get the opening times of a restaurant for a given day of the week")
    public List<OpeningTime> getOpeningTimes(
            @ApiPathParam(name = "id", description = "ID of the restaurant") String id,
            @ApiPathParam(name = "dayOfWeek", description = "Day of the week") DayOfWeek dayOfWeek
    ) throws IOException, NotFoundException {
        return getManager().get(id).getOpeningTimes(dayOfWeek);
    }
}
