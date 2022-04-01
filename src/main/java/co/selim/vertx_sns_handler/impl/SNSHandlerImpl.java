package co.selim.vertx_sns_handler.impl;

import co.selim.vertx_sns_handler.SNSHandler;
import co.selim.vertx_sns_handler.handler.NotificationHandler;
import co.selim.vertx_sns_handler.handler.SubscriptionConfirmationHandler;
import co.selim.vertx_sns_handler.handler.UnsubscribeConfirmationHandler;
import co.selim.vertx_sns_handler.impl.deserialization.Mappings;
import co.selim.vertx_sns_handler.model.Notification;
import co.selim.vertx_sns_handler.model.SNSMessage;
import co.selim.vertx_sns_handler.model.SubscriptionConfirmation;
import co.selim.vertx_sns_handler.model.UnsubscribeConfirmation;
import io.vertx.core.Future;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class SNSHandlerImpl implements SNSHandler {

  private static final String MESSAGE_TYPE_HEADER = "x-amz-sns-message-type";

  private final HttpClient httpClient;
  private NotificationHandler notificationHandler;
  private SubscriptionConfirmationHandler subscriptionConfirmationHandler;
  private UnsubscribeConfirmationHandler unsubscribeConfirmationHandler;

  public SNSHandlerImpl(HttpClient httpClient) {
    this.httpClient = httpClient;
    this.notificationHandler = NotificationHandler.create();
    this.subscriptionConfirmationHandler = SubscriptionConfirmationHandler.create();
    this.unsubscribeConfirmationHandler = UnsubscribeConfirmationHandler.create();
  }

  @Override
  public SNSHandler setOnNotification(NotificationHandler handler) {
    this.notificationHandler = handler;
    return this;
  }

  @Override
  public SNSHandler setOnSubscriptionConfirmation(SubscriptionConfirmationHandler handler) {
    this.subscriptionConfirmationHandler = handler;
    return this;
  }

  @Override
  public SNSHandler setOnUnsubscribeConfirmation(UnsubscribeConfirmationHandler handler) {
    this.unsubscribeConfirmationHandler = handler;
    return this;
  }

  @Override
  public void handle(RoutingContext ctx) {
    String messageTypeHeader = ctx.request().getHeader(MESSAGE_TYPE_HEADER);
    SNSMessage.Type messageType = SNSMessage.Type.fromTextForm(messageTypeHeader);

    JsonObject body = ctx.getBodyAsJson();
    Future<Void> result = switch (messageType) {
      case NOTIFICATION -> handleNotification(Mappings.toNotification(body));
      case SUBSCRIPTION_CONFIRMATION -> handleSubscriptionConfirmation(Mappings.toSubscriptionConfirmation(body));
      case UNSUBSCRIBE_CONFIRMATION -> handleUnsubscribeConfirmation(Mappings.toUnsubscribeConfirmation(body));
    };

    result.onSuccess(nothing -> ctx.end()).onFailure(ctx::fail);
  }

  private Future<Void> handleNotification(Notification notification) {
    return switch (notificationHandler.handle(notification)) {
      case ACKNOWLEDGE -> Future.succeededFuture();
      case UNSUBSCRIBE -> visitUrl(notification.unsubscribeURL());
    };
  }

  private Future<Void> handleSubscriptionConfirmation(SubscriptionConfirmation subscriptionConfirmation) {
    return switch (subscriptionConfirmationHandler.handle(subscriptionConfirmation)) {
      case ACKNOWLEDGE -> visitUrl(subscriptionConfirmation.subscribeURL());
      case IGNORE -> Future.succeededFuture();
    };
  }

  private Future<Void> handleUnsubscribeConfirmation(UnsubscribeConfirmation unsubscribeConfirmation) {
    return switch (unsubscribeConfirmationHandler.handle(unsubscribeConfirmation)) {
      case ACKNOWLEDGE -> Future.succeededFuture();
      case RESUBSCRIBE -> visitUrl(unsubscribeConfirmation.subscribeURL());
    };
  }

  private Future<Void> visitUrl(String url) {
    RequestOptions requestOptions = new RequestOptions().setMethod(HttpMethod.GET).setAbsoluteURI(url);
    return httpClient.request(requestOptions)
      .flatMap(HttpClientRequest::connect)
      .mapEmpty();
  }
}
