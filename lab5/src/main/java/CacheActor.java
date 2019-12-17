import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;

import java.util.HashMap;
import java.util.Map;

public class CacheActor extends AbstractActor {


    private Map<String, Long> store = new HashMap<>();

    static Props props() {
        return Props.create(CacheActor.class);
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create();
    }

    static class GetMessage {
        private final String url;

        GetMessage(String url) {
            this.url = url;
        }

        String getUrl() {
            return url;
        }
    }

}
