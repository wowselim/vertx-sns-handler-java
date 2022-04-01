package co.selim.vertx_sns_handler.model;

import co.selim.vertx_sns_handler.annotation.Nullable;

import java.time.Instant;
import java.util.UUID;

public record Notification(
  String message,
  UUID messageId,
  String signature,
  String signatureVersion,
  String signingCertURL,
  @Nullable
  String subject,
  Instant timestamp,
  String topicArn,
  SNSMessage.Type type,
  String unsubscribeURL
) implements SNSMessage {
}
