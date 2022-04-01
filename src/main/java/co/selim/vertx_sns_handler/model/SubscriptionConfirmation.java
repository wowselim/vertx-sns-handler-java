package co.selim.vertx_sns_handler.model;

import java.time.Instant;
import java.util.UUID;

public record SubscriptionConfirmation(
  String message,
  UUID messageId,
  String signature,
  String signatureVersion,
  String signingCertURL,
  String subscribeURL,
  Instant timestamp,
  String token,
  String topicArn,
  SNSMessage.Type type
) implements SNSMessage {
}
