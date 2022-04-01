package co.selim.vertx_sns_handler.impl.deserialization;

import co.selim.vertx_sns_handler.model.Notification;
import co.selim.vertx_sns_handler.model.SNSMessage;
import co.selim.vertx_sns_handler.model.SubscriptionConfirmation;
import co.selim.vertx_sns_handler.model.UnsubscribeConfirmation;
import io.vertx.core.json.JsonObject;

import java.time.Instant;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

public final class Mappings {
  private Mappings() {
  }

  public static Notification toNotification(JsonObject source) {
    return new Notification(
      message(source),
      messageId(source),
      signature(source),
      signatureVersion(source),
      signingCertURL(source),
      subject(source),
      timestamp(source),
      topicArn(source),
      type(source),
      unsubscribeURL(source)
    );
  }

  public static SubscriptionConfirmation toSubscriptionConfirmation(JsonObject source) {
    return new SubscriptionConfirmation(
      message(source),
      messageId(source),
      signature(source),
      signatureVersion(source),
      signingCertURL(source),
      subscribeURL(source),
      timestamp(source),
      token(source),
      topicArn(source),
      type(source)
    );
  }

  public static UnsubscribeConfirmation toUnsubscribeConfirmation(JsonObject source) {
    return new UnsubscribeConfirmation(
      message(source),
      messageId(source),
      signature(source),
      signatureVersion(source),
      signingCertURL(source),
      subscribeURL(source),
      timestamp(source),
      token(source),
      topicArn(source),
      type(source)
    );
  }

  private static String message(JsonObject source) {
    return requireString(source, "Message");
  }

  private static UUID messageId(JsonObject source) {
    return UUID.fromString(requireString(source, "MessageId"));
  }

  private static String signature(JsonObject source) {
    return requireString(source, "Signature");
  }

  private static String signatureVersion(JsonObject source) {
    return requireString(source, "SignatureVersion");
  }

  private static String signingCertURL(JsonObject source) {
    return requireString(source, "SigningCertURL");
  }

  private static Instant timestamp(JsonObject source) {
    return Instant.parse(requireString(source, "Timestamp"));
  }

  private static String token(JsonObject source) {
    return requireString(source, "Token");
  }

  private static String topicArn(JsonObject source) {
    return requireString(source, "TopicArn");
  }

  private static SNSMessage.Type type(JsonObject source) {
    return SNSMessage.Type.fromTextForm(requireString(source, "Type"));
  }

  private static String unsubscribeURL(JsonObject source) {
    return requireString(source, "UnsubscribeURL");
  }

  private static String subscribeURL(JsonObject source) {
    return requireString(source, "SubscribeURL");
  }

  private static String subject(JsonObject source) {
    return source.getString("Subject");
  }

  private static String requireString(JsonObject source, String key) {
    return requireNonNull(source.getString(key), "Required field '" + key + "' was missing from json object.");
  }
}
