package e2e;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisException;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class FakeAuctionServer {
    private SingleMessageListener messageListener = new SingleMessageListener();

    public static final String ITEM_ID_AS_LOGIN = "auction-%s";
    public static final String AUCTION_RESOURCE = "Auction";
    public static final String REDIS_HOSTNAME = "localhost";
    public static final String AUCTION_PASSWORD = "auction";

    private final String ItemId;
    private final Jedis jedis;
    private String chat;
    public FakeAuctionServer(String itemId) {
        ItemId = itemId;
        this.jedis = new Jedis(REDIS_HOSTNAME);
    }

    public void startSellingItem() throws Exception{
        jedis.connect();
        jedis.subscribe(new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                super.onMessage(channel, message);
                chat = message;
            }
        });
    }
    public void hasReceivedJoinRequestFromSniper() throws InterruptedException{
        messageListener.receivesAMessage();
    }
    public void announceClosed() throws Exception{
        jedis.publish(String.format(ITEM_ID_AS_LOGIN, this.ItemId), "close");
    }

    public void stop(){
        jedis.close();
    }
    public String getItemId() {
        return ItemId;
    }
}
