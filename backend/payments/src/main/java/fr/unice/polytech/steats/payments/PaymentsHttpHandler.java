package fr.unice.polytech.steats.payments;

import fr.unice.polytech.steats.models.Payment;
import fr.unice.polytech.steats.utils.AbstractHandler;
import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.JsonResponse;
import fr.unice.polytech.steats.utils.NotFoundException;
import fr.unice.polytech.steats.utils.openapi.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@ApiMasterRoute(name = "Payments", path = "/api/payments")
public class PaymentsHttpHandler extends AbstractHandler {
    public PaymentsHttpHandler(String subPath, Logger logger) {
        super(subPath, logger);
    }

    private PaymentManager getManager() {
        return PaymentManager.getInstance();
    }

    @ApiRoute(method = HttpUtils.GET, path = "/{id}", summary = "Get a payment by its ID")
    public Payment get(
            @ApiPathParam(name = "id", description = "ID of the payment") String id
    ) throws NotFoundException {
        return getManager().get(id);
    }

    @ApiRoute(method = HttpUtils.GET, path = "", summary = "Get all payments")
    public List<Payment> getAll(
            @ApiQueryParam(name = "orderId", description = "ID of the order the payment must be for") String orderId,
            @ApiQueryParam(name = "userId", description = "ID of the user that did the payment") String userId
    ) {
        if (orderId != null)
            return getManager().getPaymentsForOrder(orderId);
        if (userId != null)
            return getManager().getPaymentsOfUser(userId);
        return getManager().getAll();
    }

    @ApiRoute(method = HttpUtils.POST, path = "/pay", summary = "Pay an order")
    public JsonResponse<Payment> pay(
            @ApiBodyParam(name = "orderId", description = "ID of the order to pay") String orderId
    ) throws IOException {
        Optional<Payment> result = PaymentSystem.pay(orderId);
        if (result.isEmpty())
            throw new IllegalAccessError("Payment failed");
        return new JsonResponse<>(HttpUtils.CREATED_CODE, result.get());
    }
}
