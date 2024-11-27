package fr.unice.polytech.steats.payments;

import fr.unice.polytech.steats.models.Payment;
import fr.unice.polytech.steats.utils.*;
import fr.unice.polytech.steats.utils.openapi.*;

import java.io.IOException;
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
    public HttpResponse get(@ApiPathParam(name = "id") String id) throws IOException {
        try {
            return new JsonResponse<>(getManager().get(id));
        } catch (NotFoundException e) {
            return new HttpResponse(HttpUtils.NOT_FOUND_CODE, "Payment not found");
        }
    }

    @ApiRoute(method = HttpUtils.GET, path = "", summary = "Get all payments")
    public HttpResponse getAll(@ApiQueryParam(name = "orderId") String orderId, @ApiQueryParam(name = "userId") String userId) throws IOException {
        if (orderId != null)
            return new JsonResponse<>(getManager().getPaymentsForOrder(orderId));
        if (userId != null)
            return new JsonResponse<>(getManager().getPaymentsOfUser(userId));
        return new JsonResponse<>(getManager().getAll());
    }

    @ApiRoute(method = HttpUtils.POST, path = "/pay", summary = "Pay an order")
    public HttpResponse pay(@ApiBodyParam(name = "orderId") String orderId) throws IOException {
        if (orderId == null)
            return new HttpResponse(HttpUtils.BAD_REQUEST_CODE, "Missing order ID");

        Optional<Payment> result = PaymentSystem.pay(orderId);
        if (result.isEmpty())
            return new HttpResponse(HttpUtils.INTERNAL_SERVER_ERROR_CODE, "Payment failed");
        return new JsonResponse<>(HttpUtils.CREATED_CODE, result.get());
    }
}
