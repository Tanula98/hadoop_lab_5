import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import org.asynchttpclient.AsyncHttpClient;

import java.util.concurrent.CompletionStage;

public class TestHttpPing {
    ActorRef cacheActor;

    public TestHttpPing(ActorSystem system) {
        cacheActor = system.actorOf(CacheActor.props(), AkkaStreamsAppConstants.CACHE_ACTOR_NAME);
    }

    public Flow<HttpRequest, HttpResponse, NotUsed> createRouteFlow(AsyncHttpClient asyncHttpClient, ActorMaterializer materializer) {
        Sink<TestPing, CompletionStage<Long>> testSink = createSink(asyncHttpClient);

        return Flow.of(HttpRequest.class)
                //→ map в Pair<url сайта из query параметра, Integer количество запросов>
                .map()
                .mapAsync()
                .map()
    }


    public Sink<TestPing, CompletionStage<Long>> createSink(AsyncHttpClient asyncHttpClient) {
        Sink<Long, CompletionStage<Long>> fold = Sink.fold(0L, Long::sum);


    }

}