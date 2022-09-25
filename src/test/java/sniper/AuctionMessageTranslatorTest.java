package sniper;

import Sniper.AuctionEventListener;
import Sniper.AuctionMessageTranslator;
import java.util.HashMap;
import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import static org.junit.jupiter.api.Assertions.*;

public class AuctionMessageTranslatorTest {
  @RegisterExtension
  private final JUnit5Mockery context = new JUnit5Mockery();

  private final AuctionEventListener listener = context.mock(AuctionEventListener.class);

  private final AuctionMessageTranslator translator = new AuctionMessageTranslator(listener);
  @Test
  //close 메시지를 받았을때 close
  public void notifiesAuctionCloseWhenCloseMessageReceived(){
    context.checking(new Expectations(){{
      oneOf(listener).auctionClosed();
    }});
    translator.message(null,"EVENT : close;");

  }
  @Test //가격정보 데이터가 주어졌을때 입찰 정보를 호출하는지 알아보는 테스트
  public void notifiesBidDetailsWhenCurrentPriceMessageReceived(){
    context.checking(new Expectations(){{
      oneOf(listener).currentPrice(925,5);
    }});
    translator.message(null, "EVENT : price;CURRENT : 925;INCREMENT : 5;BIDDER : js;");

  }
//  @Test // 테스트를 짜는데 어려움을 겪는 가장 대표적인 사례 (private test)
//  public void 이벤트클로즈인경우(){
//    HashMap<String,String> event =  translator.messageFormatFrom("EVENT : close;");
//    assertEquals(event.get("EVENT"),"close");
//  }

}
