package Sniper;

import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import javax.swing.*;

public class Main {
    public static final String JOIN_COMMAND_FORMAT = "EVENT : join;ID : %s";
    public static final String BID_COMMAND_FORMAT = "EVENT : bid;PRICE : %d;BIDDER : %s";
    public static final String MAIN_WINDOW = "main_window";
    private MainWindow ui;

    private RedisClient client;

    public Main(String itemId) throws Exception{
        startUserInterface();
        this.client = RedisClient.create("redis://localhost"); //생성자에서 하는걸 추천하심.
        StatefulRedisPubSubConnection<String, String> publishConnection = client.connectPubSub();
        StatefulRedisPubSubConnection<String, String> subscribeConnection = client.connectPubSub();
        RedisPubSubCommands<String, String> sync = publishConnection.sync();
        sync.publish("SERVER-" + itemId, String.format(JOIN_COMMAND_FORMAT, "sniper")); // 서버한테 보내는 거

        subscribeConnection.addListener(new RedisPubSubListener<String, String>() {
            @Override
            public void message(String channel, String message) {
                System.out.println("Main : " + channel + ", Message : "+message);
                if(message.startsWith("EVENT : price")){
                    ui.showsStatus(MainWindow.STATUS_BIDDING);
                    sync.publish("SERVER-"+itemId, String.format(BID_COMMAND_FORMAT, 1098,"sniper"));// 1098을 보내는게 말이 안돼서 바꿀 예정
                }else{// Event 가 close 일때만 보내도록 재구성 예정
                    ui.showsStatus(MainWindow.STATUS_LOST);
                }
                // Bottom-Up 방식으로 구현 -> 에러가 적게나고 자동완성 도움을 받을 수 있다.
                // Top-Down 방식으로 구현 -> 함수 이름부터 동작하는 코드까지 필요한 것만 작성해서 좋다. :
            }

            @Override
            public void message(String pattern, String channel, String message) {

            }

            @Override
            public void subscribed(String channel, long count) {

            }// 누군가가 subscribe 할때 발생하는 이벤트

            @Override
            public void psubscribed(String pattern, long count) {

            }// 채널 여러개를 한번에 구독하면 발생하는 이벤트

            @Override
            public void unsubscribed(String channel, long count) {

            }

            @Override
            public void punsubscribed(String pattern, long count) {

            }
        });
        RedisPubSubAsyncCommands<String, String> async = subscribeConnection.async();
        async.subscribe("AUCTION-"+itemId);
    }

    public static void main(String... args) throws Exception{
        Main main = new Main(args[0]);
    }

    private void startUserInterface() throws Exception { // 메인 윈도우 생성
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                ui = new MainWindow();
            }
        });
    }
}


