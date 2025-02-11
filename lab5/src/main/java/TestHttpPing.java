import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.Query;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import org.asynchttpclient.AsyncHttpClient;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
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
                    Integer count = Integer.parseInt(requestQuery.getOrElse(AkkaStreamsAppConstants.COUNT_KEY, "-1"));
                    return new TestPing(url, count);
                })
                //→ mapAsync,
                .mapAsync(AkkaStreamsAppConstants.PARALLELISM, testPing ->
            //С помощью Patterns.ask посылаем запрос в кеширующий актор —
                    Patterns.ask(cacheActor, new CacheActor.GetMessage(testPing.getUrl()), AkkaStreamsAppConstants.TIMEOUT)
                            //есть ли результат. Обрабатываем ответ с помощью метода thenCompose
                            .thenCompose(req ->{
                                ResultPing res = (ResultPing) req;
                                //если результат уже посчитан, то возвращаем его как completedFuture
                                if (res.getPing() != null){
                                    return CompletableFuture.completedFuture(res);
                                } else {
                                    //если нет, то создаем на лету flow из данных запроса, выполняем его и
                                    //возвращаем СompletionStage<Long> :
                                    //Source.from(Collections.singletonList(r))
                                    //.toMat(testSink, Keep.right()).run(materializer);
                                    return Source.from(Collections.singletonList(testPing))
                                            .toMat(testSink, Keep.right())
                                            .run(materializer)
                                            .thenApply(time -> new ResultPing(testPing.getUrl(),
                                                    time/testPing.getCount()/AkkaStreamsAppConstants.ONE_SECOND_IN_NANO_SECONDS));
                                }
                            }))
                //→ map в HttpResponse с результатом а также посылка результата в
                //кеширующий актор.
                .map( res -> {
                    cacheActor.tell(res, ActorRef.noSender());
                    return HttpResponse.create()
                            .withEntity(res.getUrl() + " " + res.getPing());
                });
    }

//Общая логика создания внутреннего sink - testSink
    public Sink<TestPing, CompletionStage<Long>> createSink(AsyncHttpClient asyncHttpClient) {
        Sink<Long, CompletionStage<Long>> fold = Sink.fold(0L, Long::sum);

        //C помощью метода create создаем Flow
        return  Flow.<TestPing>create()
                //→ mapConcat размножаем сообщения до нужного количества копий
                .mapConcat(testPing -> Collections.nCopies(testPing.getCount(), testPing.getUrl()))
                //→ mapAsync — засекаем время, вызываем async http client и с помощью
                //метода thenCompose вычисляем время и возвращаем future с временем
                //выполнения запроса
                .mapAsync(AkkaStreamsAppConstants.PARALLELISM, url -> {
                    long startTime = System.nanoTime();
                    return asyncHttpClient
                            .prepareGet(url)
                            .execute()
                            .toCompletableFuture()
                            .thenApply(response -> System.nanoTime() - startTime);
                })
                //→ завершаем flow : .toMat(fold, Keep.right() ) ;
                .toMat(fold, Keep.right() ) ;
        //в данном случае fold — это агрегатор который подсчитывает сумму всех
        //времен созаем его с помощью Sink.fold

    }

}