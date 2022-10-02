package e2e;

import Sniper.Main;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisStringAsyncCommands;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import static Sniper.Main.BID_COMMAND_FORMAT;
import static e2e.ApplicationRunner.SNIPER_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class FakeAuctionServer {
    private SingleMessageListener messageListener = new SingleMessageListener();

    public static final String ITEM_ID_AS_LOGIN = "auction-%s";
    public static final String AUCTION_RESOURCE = "Auction";
    public static final String REDIS_HOSTNAME = "127.0.0.1";
    public static final String AUCTION_PASSWORD = "auction";

    private final String ItemId;
    private final RedisClient client;
    private String chat;
    public FakeAuctionServer(String itemId) {
        ItemId = itemId;
        this.client = RedisClient.create("redis://localhost"); //생성자에서 하는걸 추천하심.
    }

    public void startSellingItem() throws Exception{
        StatefulRedisPubSubConnection<String, String> connection = client.connectPubSub();
        connection.addListener(new RedisPubSubListener<String, String>() {
            @Override
            public void message(String channel, String message) {
                messageListener.processMessage(channel, message);
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
        RedisPubSubAsyncCommands<String, String> async = connection.async();
        async.subscribe("SERVER-"+getItemId());
    }
    public void hasReceivedJoinRequestFromSniper() throws InterruptedException{
       // messageListener.receivesAMessage();
        messageListener.receivesAMessageMatcher(equalTo(String.format(Main.JOIN_COMMAND_FORMAT, SNIPER_ID)));
    }
    public void announceClosed() throws Exception{
        StatefulRedisPubSubConnection<String, String> connection = client.connectPubSub();
        RedisPubSubCommands<String, String> sync = connection.sync();
        sync.publish("AUCTION-"+this.ItemId, "EVENT : close;");
    }

    public void stop(){
        client.close();
        messageListener.clear();
    }
    public String getItemId() {
        return ItemId;
    }

    public void reportPrice(int price, int increment, String bidder) {
        StatefulRedisPubSubConnection<String, String> connection = client.connectPubSub();
        RedisPubSubCommands<String, String> sync = connection.sync();
        sync.publish("AUCTION-"+this.ItemId,
            String.format("EVENT : price;CURRENT : %d;INCREMENT : %d;BIDDER : %s;", price,increment,bidder));
    }

    public void hasReceivedBid(int price, String bidder) throws InterruptedException {
        messageListener.receivesAMessageMatcher(equalTo(
            String.format(BID_COMMAND_FORMAT, price,bidder)));
    }
}
