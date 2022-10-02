package Sniper;

import Sniper.AuctionEventListener.PriceSource;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

// 메세지를 받으면 Lintener 를 통해 함수를 실행한다.
public class AuctionMessageTranslator implements RedisPubSubListener<String,String> {

  private AuctionEventListener listener;
  private String sniperId;
  public AuctionMessageTranslator(AuctionEventListener listener, final String sniperId) {
    this.listener = listener;
    this.sniperId = sniperId;
  }
  private static class AuctionEvent{
    private final Map<String,String> fields = new HashMap<>();
    public String type(){
      return get("EVENT");
    }
    private String get(String name){
      return fields.get(name);
    }
    private int getInt(String name){
      return Integer.parseInt(get(name));
    }
    public int currentPrice(){
      return getInt("CURRENT");
    }
    public int increment(){
      return getInt("INCREMENT");
    }

    private String bidder(){
      return get("BIDDER");
    }
    public PriceSource isFrom(String sniperId){
      return sniperId.equals(bidder()) ? PriceSource.FromSniper : PriceSource.FromOtherBidder;
    }
    public void put(String field){
      String [] pair = field.split(":");
      if(pair.length != 2){
        throw new RuntimeException("message format is wrong");
      }
      fields.put(pair[0].trim(),pair[1].trim());
    }
    public static AuctionEvent from(String message){
      AuctionEvent auctionEvent = new AuctionEvent();
      for(String s : message.split(";")){
        auctionEvent.put(s);
      }
      return auctionEvent;
    }

  }

  @Override
  public void message(String channel, String message) {
    AuctionEvent event = AuctionEvent.from(message);
    String type = event.type();
    if("close".equals(type)){ // 이렇게 사용하면 type 이 null 이여도 NullPointer Exception 오류가 발생 X
      listener.auctionClosed();
    } else if ("price".equals(type)) {
      listener.currentPrice(event.currentPrice(),event.increment(),event.isFrom(sniperId));

    }
  }

  @Override
  public void message(String pattern, String channel, String message) {

  }

  @Override
  public void subscribed(String channel, long count) {

  }

  @Override
  public void psubscribed(String pattern, long count) {

  }

  @Override
  public void unsubscribed(String channel, long count) {

  }

  @Override
  public void punsubscribed(String pattern, long count) {

  }
}
