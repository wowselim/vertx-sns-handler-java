package co.selim.vertx_sns_handler.handler;

import co.selim.vertx_sns_handler.model.UnsubscribeConfirmation;

@FunctionalInterface
public interface UnsubscribeConfirmationHandler {

  UnsubscribeConfirmationHandler.Result handle(UnsubscribeConfirmation notification);

  enum Result {
    ACKNOWLEDGE, RESUBSCRIBE
  }

  static UnsubscribeConfirmationHandler create() {
    return unsubscribeConfirmation -> UnsubscribeConfirmationHandler.Result.ACKNOWLEDGE;
  }
}
