package co.selim.vertx_sns_handler.handler;

import co.selim.vertx_sns_handler.model.SubscriptionConfirmation;

@FunctionalInterface
public interface SubscriptionConfirmationHandler {

  SubscriptionConfirmationHandler.Result handle(SubscriptionConfirmation notification);

  enum Result {
    ACKNOWLEDGE, IGNORE
  }

  static SubscriptionConfirmationHandler create() {
    return subscriptionConfirmation -> SubscriptionConfirmationHandler.Result.ACKNOWLEDGE;
  }
}
