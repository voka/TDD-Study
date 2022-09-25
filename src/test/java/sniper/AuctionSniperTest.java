package sniper;

import Sniper.AuctionSniper;
import Sniper.SniperListener;
import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class AuctionSniperTest {

  @RegisterExtension
  private final JUnit5Mockery context = new JUnit5Mockery();

  private SniperListener listener = context.mock(SniperListener.class);

  private AuctionSniper sniper = new AuctionSniper(listener);

  @Test
  public void reportLostWhenAuctionClose(){
    context.checking(new Expectations(){{
      oneOf(listener).sniperLost();
    }});
    sniper.auctionClosed();
  }

}
