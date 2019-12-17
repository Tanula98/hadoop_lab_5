import akka.actor.AbstractActor;
import akka.actor.Props;

import java.util.HashMap;
import java.util.Map;

public class CacheActor extends AbstractActor {


    private Map<String, Long> store = new HashMap<>();

    static Props props() {
        return Props.create(CacheActor.class);
    }

    @Override
    public Receive createReceive() {
        return null;
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
