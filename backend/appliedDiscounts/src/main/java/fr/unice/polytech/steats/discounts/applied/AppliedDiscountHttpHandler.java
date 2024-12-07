package fr.unice.polytech.steats.discounts.applied;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.unice.polytech.steats.models.AppliedDiscount;
import fr.unice.polytech.steats.utils.AbstractHandler;
import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.JsonResponse;
import fr.unice.polytech.steats.utils.NotFoundException;
import fr.unice.polytech.steats.utils.openapi.*;

import java.util.AbstractMap;
import java.util.List;
import java.util.logging.Logger;

@ApiMasterRoute(name = "Applied discounts", path = "/api/discounts/applied")
public class AppliedDiscountHttpHandler extends AbstractHandler {
    public AppliedDiscountHttpHandler(String subPath, Logger logger) {
        super(subPath, logger);
    }

    private AppliedDiscountManager getManager() {
        return AppliedDiscountManager.getInstance();
    }

    @ApiRoute(method = HttpUtils.GET, path = "/{id}", summary = "Get an applied discount by its ID")
    public AppliedDiscount get(
            @ApiPathParam(name = "id", description = "ID of the applied discount") String id
    ) throws NotFoundException {
        return getManager().get(id);
    }

    @ApiRoute(method = HttpUtils.GET, path = "", summary = "Get all applied discounts")
    public List<AppliedDiscount> getAll(
            @ApiQueryParam(name = "appliedOrderId", description = "ID of the order the discount must have been applied to") String appliedOrderId,
            @ApiQueryParam(name = "userId", description = "ID of the user that must have unlocked the discount") String userId
    ) {
        if (userId == null && appliedOrderId == null)
            return getManager().getAll();
        if (appliedOrderId == null)
            return getManager().getAppliedDiscountsOfUser(userId);

        if (appliedOrderId.isBlank()) appliedOrderId = null;
        if (userId != null)
            return getManager().getAppliedDiscountsOfOrderAndUser(appliedOrderId, userId);
        return getManager().getAppliedDiscountsOfOrder(appliedOrderId);
    }

    @ApiRoute(method = HttpUtils.POST, path = "", summary = "Save a list of applied discounts")
    public JsonResponse<AppliedDiscount[]> create(
            @ApiBodyParam(name = "userId", description = "ID of the user that has unlocked the discounts") String userId,
            @ApiBodyParam(name = "unlockOrderId", description = "ID of the order that has unlocked the discounts") String unlockOrderId,
            @ApiBodyParam(name = "discounts", description = "A list of key-value pairs of the ID of the unlocked discount and the order it have been applied to (can be null)", example = "[{\"ab89e6\": \"1\"}, {\"da56c8\": \"null\"}]")
            List<AbstractMap.SimpleEntry<String, String>> discounts
    ) throws JsonProcessingException {
        AppliedDiscount[] discountList = new AppliedDiscount[discounts.size()];
        int i = 0;
        for (AbstractMap.SimpleEntry<String, String> discountEntry : discounts)
            discountList[i++] = new AppliedDiscount(discountEntry.getKey(), userId, unlockOrderId, discountEntry.getValue());
        getManager().add(discountList);
        return new JsonResponse<>(HttpUtils.CREATED_CODE, discountList);
    }
}
