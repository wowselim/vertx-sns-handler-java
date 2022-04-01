package co.selim.vertx_sns_handler;

import co.selim.vertx_sns_handler.handler.NotificationHandler;
import co.selim.vertx_sns_handler.handler.SubscriptionConfirmationHandler;
import co.selim.vertx_sns_handler.handler.UnsubscribeConfirmationHandler;
import co.selim.vertx_sns_handler.impl.SNSHandlerImpl;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.ext.web.RoutingContext;

public interface SNSHandler extends Handler<RoutingContext> {

  SNSHandler setOnNotification(NotificationHandler handler);

  SNSHandler setOnSubscriptionConfirmation(SubscriptionConfirmationHandler handler);

  SNSHandler setOnUnsubscribeConfirmation(UnsubscribeConfirmationHandler handler);

  static SNSHandler create(HttpClient httpClient) {
    return new SNSHandlerImpl(httpClient);
  }

  static SNSHandler create(Vertx vertx) {
    return new SNSHandlerImpl(vertx.createHttpClient());
  }
}
