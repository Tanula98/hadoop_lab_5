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
}
