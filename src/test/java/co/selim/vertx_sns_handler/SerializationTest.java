package co.selim.vertx_sns_handler;

import co.selim.vertx_sns_handler.impl.deserialization.Mappings;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class SerializationTest {
  @Test
  public void notificationsCanBeDeserialized() {
    String notificationJson = """
        {
          "Type" : "Notification",
          "MessageId" : "22b80b92-fdea-4c2c-8f9d-bdfb0c7bf324",
          "TopicArn" : "arn:aws:sns:us-west-2:123456789012:MyTopic",
          "Subject" : "My First Message",
          "Message" : "Hello world!",
          "Timestamp" : "2012-05-02T00:54:06.655Z",
          "SignatureVersion" : "1",
          "Signature" : "EXAMPLEw6JRN...",
          "SigningCertURL" : "https://sns.us-west-2.amazonaws.com/SimpleNotificationService-f3ecfb7224c7233fe7bb5f59f96de52f.pem",
          "UnsubscribeURL" : "https://sns.us-west-2.amazonaws.com/?Action=Unsubscribe&SubscriptionArn=arn:aws:sns:us-west-2:123456789012:MyTopic:c9135db0-26c4-47ec-8998-413945fb5a96"
        }
      """;

    assertDoesNotThrow(() -> Mappings.toNotification(new JsonObject(notificationJson)));
  }

  @Test
  public void notificationsWithoutSubjectsCanBeDeserialized() {
    String notificationJson = """
        {
          "Type" : "Notification",
          "MessageId" : "22b80b92-fdea-4c2c-8f9d-bdfb0c7bf324",
          "TopicArn" : "arn:aws:sns:us-west-2:123456789012:MyTopic",
          "Message" : "Hello world!",
          "Timestamp" : "2012-05-02T00:54:06.655Z",
          "SignatureVersion" : "1",
          "Signature" : "EXAMPLEw6JRN...",
          "SigningCertURL" : "https://sns.us-west-2.amazonaws.com/SimpleNotificationService-f3ecfb7224c7233fe7bb5f59f96de52f.pem",
          "UnsubscribeURL" : "https://sns.us-west-2.amazonaws.com/?Action=Unsubscribe&SubscriptionArn=arn:aws:sns:us-west-2:123456789012:MyTopic:c9135db0-26c4-47ec-8998-413945fb5a96"
        }
      """;

    assertDoesNotThrow(() -> Mappings.toNotification(new JsonObject(notificationJson)));
  }

  @Test
  public void unsubscribeConfirmationsCanBeDeserialized() {
    String unsubscribeConfirmationJson = """
        {
          "Type" : "UnsubscribeConfirmation",
          "MessageId" : "47138184-6831-46b8-8f7c-afc488602d7d",
          "Token" : "2336412f37...",
          "TopicArn" : "arn:aws:sns:us-west-2:123456789012:MyTopic",
          "Message" : "You have chosen to deactivate subscription arn:aws:sns:us-west-2:123456789012:MyTopic:2bcfbf39-05c3-41de-beaa-fcfcc21c8f55.\\nTo cancel this operation and restore the subscription, visit the SubscribeURL included in this message.",
          "SubscribeURL" : "https://sns.us-west-2.amazonaws.com/?Action=ConfirmSubscription&TopicArn=arn:aws:sns:us-west-2:123456789012:MyTopic&Token=2336412f37fb6...",
          "Timestamp" : "2012-04-26T20:06:41.581Z",
          "SignatureVersion" : "1",
          "Signature" : "EXAMPLEHXgJm...",
          "SigningCertURL" : "https://sns.us-west-2.amazonaws.com/SimpleNotificationService-f3ecfb7224c7233fe7bb5f59f96de52f.pem"
        }
      """;

    assertDoesNotThrow(() -> Mappings.toUnsubscribeConfirmation(new JsonObject(unsubscribeConfirmationJson)));
  }

  @Test
  public void subscriptionConfirmationsCanBeDeserialized() {
    String subscriptionConfirmationJson = """
        {
          "Type" : "SubscriptionConfirmation",
          "MessageId" : "165545c9-2a5c-472c-8df2-7ff2be2b3b1b",
          "Token" : "2336412f37...",
          "TopicArn" : "arn:aws:sns:us-west-2:123456789012:MyTopic",
          "Message" : "You have chosen to subscribe to the topic arn:aws:sns:us-west-2:123456789012:MyTopic.\\nTo confirm the subscription, visit the SubscribeURL included in this message.",
          "SubscribeURL" : "https://sns.us-west-2.amazonaws.com/?Action=ConfirmSubscription&TopicArn=arn:aws:sns:us-west-2:123456789012:MyTopic&Token=2336412f37...",
          "Timestamp" : "2012-04-26T20:45:04.751Z",
          "SignatureVersion" : "1",
          "Signature" : "EXAMPLEpH+DcEwjAPg8O9mY8dReBSwksfg2S7WKQcikcNKWLQjwu6A4VbeS0QHVCkhRS7fUQvi2egU3N858fiTDN6bkkOxYDVrY0Ad8L10Hs3zH81mtnPk5uvvolIC1CXGu43obcgFxeL3khZl8IKvO61GWB6jI9b5+gLPoBc1Q=",
          "SigningCertURL" : "https://sns.us-west-2.amazonaws.com/SimpleNotificationService-f3ecfb7224c7233fe7bb5f59f96de52f.pem"
        }
      """;

    assertDoesNotThrow(() -> Mappings.toSubscriptionConfirmation(new JsonObject(subscriptionConfirmationJson)));
  }
}
