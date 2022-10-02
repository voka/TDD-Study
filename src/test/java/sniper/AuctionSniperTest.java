package sniper;

import Sniper.Auction;
import Sniper.AuctionEventListener.PriceSource;
import Sniper.AuctionSniper;
import Sniper.SniperListener;
import org.jmock.Expectations;
import org.jmock.States;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class AuctionSniperTest {

  @RegisterExtension
  private final JUnit5Mockery context = new JUnit5Mockery();

  private Auction auction = context.mock(Auction.class);
  private SniperListener listener = context.mock(SniperListener.class);

  private final States sniperState = context.states("sniper");

  //경매와 관련된 부분을 좀더 옥션에 위임하기 위해
  private AuctionSniper sniper = new AuctionSniper(auction, listener);

  @Test
  public void reportLostWhenAuctionCloseImmediately(){
    context.checking(new Expectations(){{
      oneOf(listener).sniperLost();
    }});
    sniper.auctionClosed();
  }
  @Test
  public void reportLostIfAuctionClosesWhenBidding(){ // 순서를 정해주는 테스트 ~ allowing //// 원래 mock 에서는 Expectations 를 사용할때 순서에 상관없이 테스트가 통과된다.
    context.checking(new Expectations(){{
      ignoring(auction);// auction 객체에 대한 정보를 무시하겠다.
      allowing(listener).sniperBidding(); // sniper 가 bidding 요청을 보내면
      then(sniperState.is("bidding")); // sniper state 가 bidding 으로 바뀐다.
      atLeast(1).of(listener).sniperLost(); // sniper lost 가 실행되는지 확인하는데 ,
      when(sniperState.is("bidding")); // 그때 상태가 bidding 이여야 한다.
    }});
    sniper.currentPrice(123,45,PriceSource.FromOtherBidder);
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

    sniper.currentPrice(price,increment, PriceSource.FromOtherBidder);

  }
  @Test // 현재 가격이 스나이퍼로 부터 온 가격이라면 report 결과는 winning 이여야 한다.
  public void reportIsWinningWhenCurrentPriceComeFromSniper(){
    context.checking(new Expectations(){{
      atLeast(1).of(listener).sniperWinning();
    }});
    sniper.currentPrice(123,45,PriceSource.FromSniper);
  }

  @Test
  public void reportWonIfAuctionCloseWhenWinning(){
    context.checking(new Expectations(){{
      ignoring(auction);// auction 객체에 대한 정보를 무시하겠다.
      allowing(listener).sniperWinning();// sniper 가 winning 요청을 보내면
      then(sniperState.is("winning")); // sniper state 가 winning 으로 바뀐다.
      atLeast(1).of(listener).sniperWon(); // sniper Won 이 실행되는지 확인하는데 ,
      when(sniperState.is("winning")); // 그때 상태가 winning 이여야 한다.
    }});
    sniper.currentPrice(123,45,PriceSource.FromSniper);
    sniper.auctionClosed();
  }


}
