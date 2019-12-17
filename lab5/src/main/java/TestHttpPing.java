import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.Query;
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
                .map(req -> {
                    Query requestQuery = req.getUri().query();
                    String url = requestQuery.getOrElse(AkkaStreamsAppConstants.TEST_URL_KEY, "");
                    

                })
                //→ mapAsync,
                //С помощью Patterns.ask посылаем запрос в кеширующий актор — есть ли
                //результат. Обрабатываем ответ с помощью метода thenCompose
                //если результат уже посчитан, то возвращаем его как completedFuture
                //если нет, то создаем на лету flow из данных запроса, выполняем его и
                //возвращаем СompletionStage<Long> :
                //Source.from(Collections.singletonList(r))
                //.toMat(testSink, Keep.right()).run(materializer);
                .mapAsync()
                //→ map в HttpResponse с результатом а также посылка результата в
                //кеширующий актор.
                .map()
    }


    public Sink<TestPing, CompletionStage<Long>> createSink(AsyncHttpClient asyncHttpClient) {
        Sink<Long, CompletionStage<Long>> fold = Sink.fold(0L, Long::sum);


    }

}