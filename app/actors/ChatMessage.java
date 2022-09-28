package actors;

import akka.NotUsed;
import akka.stream.ThrottleMode;
import akka.stream.javadsl.Broadcast;
import akka.stream.javadsl.Source;
import com.fasterxml.jackson.databind.JsonNode;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import stocks.*;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

public final class ChatMessage {
    final String status;
    final String message;
    final JsonNode user;



    private final Source<ChatUpdate, NotUsed> source;

    private static final FiniteDuration duration = Duration.create(75, TimeUnit.MILLISECONDS);

    public ChatMessage(String status,String message,JsonNode user) {
        this.message = message;
        this.status = status;
        this.user = user;


        source = Source.single(new ChatUpdate(message,  status,user));
    }

    public static final class WatchMessages {
        final Set<String> statuses;
        final Set<String> messages;

        public WatchMessages(Set<String> statuses,Set<String> messages) {
            this.statuses = requireNonNull(statuses);
            this.messages = requireNonNull(messages);
        }

        @Override
        public String toString() {
            return "WatchStocks(" + statuses.toString() + ")";
        }
    }

    public static class ChatMessages {
        final Set<ChatMessage> chatMessages;

        public ChatMessages(Set<ChatMessage> chatMessages) {
            this.chatMessages = requireNonNull(chatMessages);
        }
    }

    public Source<ChatUpdate, NotUsed> history(int n) {
        return source.grouped(n)
                .map(quotes -> new ChatUpdate(message, status,user))
                .take(1);
    }

    /**
     * Provides a source that returns a stock quote every 75 milliseconds.
     */
    public Source<ChatUpdate, NotUsed> update() {
        return source.throttle(1, duration, 1, ThrottleMode.shaping())
                .map(sq -> sq);
    }

    @Override
    public String toString() {
        return "Stock(" + message + ")";
    }
}
