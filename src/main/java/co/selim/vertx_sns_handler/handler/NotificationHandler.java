package co.selim.vertx_sns_handler.handler;

import co.selim.vertx_sns_handler.model.Notification;

@FunctionalInterface
public interface NotificationHandler {

  Result handle(Notification notification);

  enum Result {
    ACKNOWLEDGE, UNSUBSCRIBE
  }

  static NotificationHandler create() {
    return notification -> Result.ACKNOWLEDGE;
  }
}
