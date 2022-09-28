package actors;

import akka.actor.AbstractActor;
import play.libs.Json;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ChatActor extends AbstractActor {
    private final Map<String, ChatMessage> stocksMap = new HashMap<>();

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ChatMessage.WatchMessages.class, watchStocks -> {

                    Set<ChatMessage> chatMessageSet = watchStocks.messages.stream()
                            .map(value -> {
                                return stocksMap.compute(value, (k, v) -> new ChatMessage(k,k, Json.newObject()));
                            }).collect(Collectors.toSet());

                    sender().forward(new ChatMessage.ChatMessages(chatMessageSet), context());
                }).build();
    }
}
