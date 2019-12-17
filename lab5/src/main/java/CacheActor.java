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

    //б. создаем в actorSystem — актор который принимает две команды — поиск
    //уже готового результата тестирования и результат тестрования.
    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(ResultPing.class, req -> {
                    String url = req.getUrl();
                    Long result = req.getPing();
                    store.put(url, result);
                })
                .match(CacheActor.GetMessage.class, msg -> {
                    
                })
                .build();
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
