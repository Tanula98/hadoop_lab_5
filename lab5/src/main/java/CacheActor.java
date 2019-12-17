import akka.actor.AbstractActor;
import akka.actor.Props;

public class CacheActor extends AbstractActor {


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
