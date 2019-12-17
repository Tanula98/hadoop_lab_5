import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class TestHttpPing {
    ActorRef cacheActor;

    public TestHttpPing(ActorSystem system) {
        cacheActor = system.actorOf(CacheActor.props(), AkkaStreamsAppConstants.CACHE_ACTOR_NAME);
    }

    

}

