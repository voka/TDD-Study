package Sniper;

public class AuctionSniper implements AuctionEventListener{

  private SniperListener listener;
  private Auction auction;

  private boolean isWinning = false;

  public AuctionSniper(Auction auction, SniperListener listener) {
    this.auction = auction;
    this.listener = listener;
  }

  @Override
  public void auctionClosed() {
    if(isWinning){
      listener.sniperWon();
    }else{
      listener.sniperLost();
    }
  }

  @Override
  public void currentPrice(int price, int increment, PriceSource source) {
    isWinning = source == PriceSource.FromSniper;
    if(isWinning){
      listener.sniperWinning();
    }
    else{
      auction.bid(price + increment);
      listener.sniperBidding();
    }

  }

}
