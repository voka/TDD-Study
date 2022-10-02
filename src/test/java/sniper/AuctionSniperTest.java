package sniper;

import Sniper.Auction;
import Sniper.AuctionSniper;
import Sniper.SniperListener;
import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class AuctionSniperTest {

  @RegisterExtension
  private final JUnit5Mockery context = new JUnit5Mockery();

  private Auction auction = context.mock(Auction.class);
  private SniperListener listener = context.mock(SniperListener.class);

  //경매와 관련된 부분을 좀더 옥션에 위임하기 위해
  private AuctionSniper sniper = new AuctionSniper(auction, listener);

  @Test
  public void reportLostWhenAuctionClose(){
    context.checking(new Expectations(){{
      oneOf(listener).sniperLost();
    }});
    sniper.auctionClosed();
  }

  @Test // 새로운 가격이 들어왔을때 더 높은 가격으로 업데이트를 하고 화면에 그 가격을 표시해준다.
  public void bidHigherAndReportBiddingWhenNewPriceArrives(){
    final int price = 1001;
    final int increment = 25;
    context.checking(new Expectations(){{
        oneOf(auction).bid(price + increment); //저 가격을 입찰을 해야한다.
        atLeast(1).of(listener).sniperBidding(); // 적어도 한번은 sniperBidding 함수를 호출해야 한다.
    }});
    // auction 에는 한번만 입찰해야 된다고 말하면서 (엄격한 협력객체)
    // Listener 에게는 한번 이상만 호출하면 된다고 말하고 있음  --> 테스트의 너그러움?  (느슨한 협력객체)

    sniper.currentPrice(price,increment);

  }

}
