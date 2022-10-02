package Sniper;

import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import javax.swing.*;

// Bottom-Up 방식으로 구현 -> 에러가 적게나고 자동완성 도움을 받을 수 있다.
// Top-Down 방식으로 구현 -> 함수 이름부터 동작하는 코드까지 필요한 것만 작성해서 좋다.
public class Main{
    public static final String JOIN_COMMAND_FORMAT = "EVENT : join;ID : %s";
    public static final String BID_COMMAND_FORMAT = "EVENT : bid;PRICE : %d;BIDDER : %s";
    public static final String MAIN_WINDOW = "main_window";

    private AuctionMessageTranslator auctionMessageTranslator;
    private String itemId;
    private MainWindow ui;

    private RedisClient client; // 멤버로 쓸 생각은 없지만 가비지 컬렉션을 방지하기 위해 살려둠

    public Main(String itemId) throws Exception{
        startUserInterface();
        this.itemId = itemId;
        this.client = RedisClient.create("redis://localhost"); //생성자에서 하는걸 추천하심.
        StatefulRedisPubSubConnection<String, String> subscribeConnection = client.connectPubSub();
        RedisAuction redisAuction = new RedisAuction(client.connectPubSub(),"SERVER-" + itemId);
        this.auctionMessageTranslator = new AuctionMessageTranslator(new AuctionSniper(redisAuction,new SniperStateDisplayer())); // 위임~
        redisAuction.join();
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

    public static class RedisAuction implements Auction{
        String channel;
        RedisPubSubCommands<String, String> sync;
        public RedisAuction(StatefulRedisPubSubConnection<String, String> connection, String channel) {
            this.sync = connection.sync();
            this.channel = channel;
        }

        @Override
        public void join() {
            sendMessage(String.format(JOIN_COMMAND_FORMAT, "sniper"));
        }

        @Override
        public void bid(int bidPrice) {
            sendMessage(String.format(BID_COMMAND_FORMAT, bidPrice, "sniper"));
        }
        private void sendMessage(String message){
            sync.publish(channel,message);
        }
    }
    public class SniperStateDisplayer implements SniperListener{

        @Override
        public void sniperLost() {
            showStatus(MainWindow.STATUS_LOST);
        }

        @Override
        public void sniperBidding() {
            showStatus(MainWindow.STATUS_BIDDING);
        }

        private void showStatus(final String status){
            SwingUtilities.invokeLater(()->{
                ui.showsStatus(status);
            });
        }
    }
}


