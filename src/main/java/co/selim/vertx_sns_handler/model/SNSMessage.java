package co.selim.vertx_sns_handler.model;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

public sealed interface SNSMessage permits Notification, SubscriptionConfirmation, UnsubscribeConfirmation {
  enum Type {
    NOTIFICATION("Notification"),
    SUBSCRIPTION_CONFIRMATION("SubscriptionConfirmation"),
    UNSUBSCRIBE_CONFIRMATION("UnsubscribeConfirmation");

    public final String textForm;

    Type(String textForm) {
      this.textForm = textForm;
    }

    public static Type fromTextForm(String textForm) {
      return stream(values())
        .filter(t -> textForm.equals(t.textForm))
        .findFirst()
        .orElseThrow(() -> buildUnknownMessageTypeException(textForm));
    }

    private static RuntimeException buildUnknownMessageTypeException(String textForm) {
      String possibleValues = stream(values()).map(t -> t.textForm).collect(joining(", "));
      String message = "Unknown message type " + textForm + ". Must be one of [" + possibleValues + "].";
      return new RuntimeException(message);
    }

    @Override
    public String toString() {
      return textForm;
    }
  }

  Type type();
}
