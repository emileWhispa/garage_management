package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.util.Timeout;
import com.typesafe.config.Config;
import play.libs.akka.InjectedActorSupport;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import static akka.pattern.PatternsCS.ask;
import static akka.pattern.PatternsCS.pipe;

public class UserParentActor extends AbstractActor implements InjectedActorSupport {

    private final Timeout timeout = new Timeout(2, TimeUnit.SECONDS);
    private final Set<String> defaultStocks;
    private final Set<String> defaultValues;
    private static List<String> stringList = new ArrayList<>();



    public static class Create {
        final String id;

        public Create(String id) {
            this.id = id;
            stringList.add(id);
        }
    }

    private final UserActor.Factory childFactory;

    @Inject
    public UserParentActor(UserActor.Factory childFactory, Config config) {
        this.childFactory = childFactory;
        this.defaultStocks = new HashSet<>(config.getStringList("default.stocks"));
        this.defaultValues = new HashSet<>(config.getStringList("default.values"));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(UserParentActor.Create.class, create -> {
                    ActorRef child = injectedChild(() -> childFactory.create(create.id), "userActor-" + create.id);
                    CompletionStage<Object> future = ask(child, new ChatMessage.WatchMessages(defaultStocks,defaultValues), timeout);
                    pipe(future, context().dispatcher()).to(sender());
                }).build();
    }

}
