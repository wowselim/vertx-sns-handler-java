package co.selim.vertx_sns_handler;

import co.selim.vertx_sns_handler.handler.NotificationHandler;
import co.selim.vertx_sns_handler.handler.SubscriptionConfirmationHandler;
import co.selim.vertx_sns_handler.handler.UnsubscribeConfirmationHandler;
import co.selim.vertx_sns_handler.model.SNSMessage;
import io.reactiverse.junit5.web.WebClientOptionsInject;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.function.Consumer;

import static io.reactiverse.junit5.web.TestRequest.*;
import static io.vertx.core.http.HttpMethod.POST;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({VertxExtension.class})
public class IntegrationTest {

  private static final String HANDLER_PATH = "/sns";
  private static final String VISIT_PATH = "/ping";
  private static final String HOST = "localhost";
  private static final int PORT = 8331;
  private static final String MESSAGE_TYPE_HEADER = "x-amz-sns-message-type";

  private final Stubs stubs = new Stubs(HOST, PORT, VISIT_PATH);
  private final Consumer<HttpRequest<Buffer>> NOTIFICATION_HEADER = requestHeader(
    MESSAGE_TYPE_HEADER, SNSMessage.Type.NOTIFICATION.textForm
  );
  private final Consumer<HttpRequest<Buffer>> SUBSCRIPTION_CONFIRMATION_HEADER = requestHeader(
    MESSAGE_TYPE_HEADER, SNSMessage.Type.SUBSCRIPTION_CONFIRMATION.textForm
  );
  private final Consumer<HttpRequest<Buffer>> UNSUBSCRIBE_CONFIRMATION_HEADER = requestHeader(
    MESSAGE_TYPE_HEADER, SNSMessage.Type.UNSUBSCRIBE_CONFIRMATION.textForm
  );

  private NotificationHandler notificationHandler = NotificationHandler.create();
  private SubscriptionConfirmationHandler subscriptionConfirmationHandler = SubscriptionConfirmationHandler.create();
  private UnsubscribeConfirmationHandler unsubscribeConfirmationHandler = UnsubscribeConfirmationHandler.create();
  private boolean urlVisited = false;

  @WebClientOptionsInject
  public static final WebClientOptions options = new WebClientOptions()
    .setDefaultHost("localhost")
    .setDefaultPort(PORT);

  @BeforeEach
  public void setUp(Vertx vertx, VertxTestContext testContext) {
    Router router = Router.router(vertx);

    router.post(HANDLER_PATH)
      .handler(BodyHandler.create(false))
      .handler(
        SNSHandler.create(vertx.createHttpClient())
          .setOnNotification(n -> notificationHandler.handle(n))
          .setOnSubscriptionConfirmation(sc -> subscriptionConfirmationHandler.handle(sc))
          .setOnUnsubscribeConfirmation(usc -> unsubscribeConfirmationHandler.handle(usc))
      );

    router.get(VISIT_PATH)
      .handler(ctx -> {
        urlVisited = true;
        ctx.end();
      });

    vertx.createHttpServer()
      .requestHandler(router)
      .listen(PORT, testContext.succeedingThenComplete());
  }

  @AfterEach
  public void tearDown(Vertx vertx, VertxTestContext testContext) {
    vertx.close(testContext.succeedingThenComplete());
  }

  @Test
  void notificationsAreHandledByDefault(WebClient webClient, VertxTestContext testContext) {
    testRequest(webClient, POST, HANDLER_PATH)
      .with(NOTIFICATION_HEADER)
      .expect(statusCode(200))
      .expect(ignore -> assertFalse(urlVisited))
      .sendBuffer(stubs.notificationBuffer(), testContext);
  }

  @Test
  void unsubscribeOnNotificationWorks(WebClient webClient, VertxTestContext testContext) {
    notificationHandler = n -> NotificationHandler.Result.UNSUBSCRIBE;

    testRequest(webClient, POST, HANDLER_PATH)
      .with(NOTIFICATION_HEADER)
      .expect(statusCode(200))
      .expect(ignore -> assertTrue(urlVisited))
      .sendBuffer(stubs.notificationBuffer(), testContext);
  }

  @Test
  void subscriptionsAreConfirmedByDefault(WebClient webClient, VertxTestContext testContext) {
    testRequest(webClient, POST, HANDLER_PATH)
      .with(SUBSCRIPTION_CONFIRMATION_HEADER)
      .expect(statusCode(200))
      .expect(ignore -> assertTrue(urlVisited))
      .sendBuffer(stubs.subscriptionConfirmationBuffer(), testContext);
  }

  @Test
  void subscriptionConfirmationsCanBeIgnored(WebClient webClient, VertxTestContext testContext) {
    subscriptionConfirmationHandler = sc -> SubscriptionConfirmationHandler.Result.IGNORE;

    testRequest(webClient, POST, HANDLER_PATH)
      .with(SUBSCRIPTION_CONFIRMATION_HEADER)
      .expect(statusCode(200))
      .expect(ignore -> assertFalse(urlVisited))
      .sendBuffer(stubs.subscriptionConfirmationBuffer(), testContext);
  }

  @Test
  void unsubscribeConfirmationsAreAcknowledgedByDefault(WebClient webClient, VertxTestContext testContext) {
    testRequest(webClient, POST, HANDLER_PATH)
      .with(UNSUBSCRIBE_CONFIRMATION_HEADER)
      .expect(statusCode(200))
      .expect(ignore -> assertFalse(urlVisited))
      .sendBuffer(stubs.unsubscribeConfirmationBuffer(), testContext);
  }

  @Test
  void unsubscribeConfirmationsCanBeUndone(WebClient webClient, VertxTestContext testContext) {
    unsubscribeConfirmationHandler = usc -> UnsubscribeConfirmationHandler.Result.RESUBSCRIBE;

    testRequest(webClient, POST, HANDLER_PATH)
      .with(UNSUBSCRIBE_CONFIRMATION_HEADER)
      .expect(statusCode(200))
      .expect(ignore -> assertTrue(urlVisited))
      .sendBuffer(stubs.unsubscribeConfirmationBuffer(), testContext);
  }
}
