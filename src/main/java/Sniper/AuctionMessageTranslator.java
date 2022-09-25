package Sniper;

import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class AuctionMessageTranslator implements RedisPubSubListener<String,String> {

  private AuctionEventListener listener;
  public AuctionMessageTranslator(AuctionEventListener listener) {
    this.listener = listener;
  }

  private HashMap<String,String> messageFormatFrom(String message){
    HashMap<String,String> event = new HashMap<>();
    for(String s : message.split(";")){
      String [] pair = s.split(":");
      if(pair.length != 2){
        continue;
      }
      event.put(pair[0].trim(),pair[1].trim());
    }
    return event;

    // todo -> for to stream
//    Arrays.stream(message.split(";")).filter(s -> s.contains(":")).map(s -> s.split(":")).collect(
//        Collectors.toMap(e -> e[0].trim(), e -> e[1].trim()))
  }

  @Override
  public void message(String channel, String message) {
    HashMap<String,String> event = messageFormatFrom(message);
    String type = event.get("EVENT");
    if("close".equals(type)){ // 이렇게 사용하면 type 이 null 이여도 NullPointer Exception 오류가 발생 X
      listener.auctionClosed();
    } else if ("price".equals(type)) {
      listener.currentPrice(Integer.parseInt(event.get("CURRENT")),Integer.parseInt(event.get("INCREMENT")));
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
