import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;

import java.io.IOException;
import java.util.concurrent.CompletionStage;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Dsl;

public class AkkaStreamsApp {

    public static void main(String[] args) throws IOException {

        System.out.println(AkkaStreamsAppConstants.START_MESSAGE);
        //а. Инициализация http сервера в akka
        ActorSystem system = ActorSystem.create(AkkaStreamsAppConstants.ACTOR_SYSTEM_NAME);

        final Http http = Http.get(system);
        final ActorMaterializer materializer =
                ActorMaterializer.create(system);

        AsyncHttpClient asyncHttpClient = Dsl.asyncHttpClient();

        TestHttpPing testHttpPing = new TestHttpPing(system);

        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow =  testHttpPing.createRouteFlow(asyncHttpClient, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                routeFlow,
                ConnectHttp.toHost(AkkaStreamsAppConstants.HOST, AkkaStreamsAppConstants.PORT),
                materializer
        );
        System.out.println(AkkaStreamsAppConstants.START_SERVER_MESSAGE);
        System.in.read();
        binding
                .thenCompose(ServerBinding::unbind)
                .thenAccept(unbound -> {
                    system.terminate();
                    try {
                        asyncHttpClient.close();
                    } catch(IOException e){
                        e.printStackTrace();
                    }
                        }
                );
        // and shutdown when done


    }
}
