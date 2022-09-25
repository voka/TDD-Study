package Sniper;

import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import javax.swing.*;

public class Main implements AuctionEventListener{
    public static final String JOIN_COMMAND_FORMAT = "EVENT : join;ID : %s";
    public static final String BID_COMMAND_FORMAT = "EVENT : bid;PRICE : %d;BIDDER : %s";
    public static final String MAIN_WINDOW = "main_window";

    private RedisPubSubCommands<String, String> sync;
    private AuctionMessageTranslator auctionMessageTranslator;
    private String itemId;
    private MainWindow ui;

    private RedisClient client;

    public Main(String itemId) throws Exception{
        startUserInterface();
        this.itemId = itemId;
        this.client = RedisClient.create("redis://localhost"); //생성자에서 하는걸 추천하심.
        this.auctionMessageTranslator = new AuctionMessageTranslator(this);
        StatefulRedisPubSubConnection<String, String> publishConnection = client.connectPubSub();
        StatefulRedisPubSubConnection<String, String> subscribeConnection = client.connectPubSub();
        sync = publishConnection.sync();
        sync.publish("SERVER-" + itemId, String.format(JOIN_COMMAND_FORMAT, "sniper")); // 서버한테 보내는 거

        // Bottom-Up 방식으로 구현 -> 에러가 적게나고 자동완성 도움을 받을 수 있다.
        // Top-Down 방식으로 구현 -> 함수 이름부터 동작하는 코드까지 필요한 것만 작성해서 좋다.
        subscribeConnection.addListener(auctionMessageTranslator);
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

    @Override
    public void auctionClosed() { // 직접 ui를 변경하면 먹통이 될 수도 있기 때문에 스윙 유틸리티 안에서 함.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ui.showsStatus(MainWindow.STATUS_LOST);
            }
        });
    }

    @Override
    public void currentPrice(int price, int increment) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ui.showsStatus(MainWindow.STATUS_BIDDING);
                sync.publish("SERVER-"+itemId, String.format(BID_COMMAND_FORMAT, price + increment, "sniper"));
            }
        });
    }
}


