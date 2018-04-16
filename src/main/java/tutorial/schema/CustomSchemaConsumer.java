package tutorial.schema;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.SubscriptionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomSchemaConsumer {
    private static final Logger log = LoggerFactory.getLogger(CustomSchemaConsumer.class);
    private static final String SERVICE_URL = "pulsar://localhost:6650";
    private static final String TOPIC_NAME = "persistent://sample/standalone/ns1/tutorial-topic";
    private static final String SUBSCRIPTION_NAME = "tutorial-subscription";

    public static void main(String[] args) {
        try {
            PulsarClient client = PulsarClient.builder()
                    .serviceUrl(SERVICE_URL)
                    .build();

            log.info("Created a client for the Pulsar cluster at {}", SERVICE_URL);

            Consumer<Tweet> tweetConsumer = client.newConsumer(new TweetSchema())
                    .topic(TOPIC_NAME)
                    .subscriptionName(SUBSCRIPTION_NAME)
                    .subscriptionType(SubscriptionType.Shared)
                    .subscribe();

            log.info("Created a tweet consumer for the topic {}", TOPIC_NAME);

            do {
                Message<Tweet> tweetMsg = tweetConsumer.receive();
                Tweet tweet = tweetMsg.getValue();
                String username = tweet.getUsername();
                String content = tweet.getContent();
                log.info("The user {} just tweeted: \"{}\"", username, content);
                tweetConsumer.acknowledge(tweetMsg);
            } while (true);
        } catch (PulsarClientException e) {
            e.printStackTrace();
        }
    }
}
