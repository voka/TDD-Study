package e2e;

import static e2e.ApplicationRunner.SNIPER_ID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
public class AuctionSniperEndToEndTest {
    private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
    private final ApplicationRunner application = new ApplicationRunner();
    @Test
    public void sniperJoinsAuctionUntilAuctionCloses() throws Exception{
        auction.startSellingItem();
        application.startBindingIn(auction);
        auction.hasReceivedJoinRequestFromSniper();
        auction.announceClosed();
        application.showsSniperHasLostAuction();
    }
    @AfterEach
    public void stop(){
        auction.stop();
        application.stop();
    }
    @Test
    public void sniperMakesAHigherBidButLoses() throws Exception{
        auction.startSellingItem();
        application.startBindingIn(auction);
        auction.hasReceivedJoinRequestFromSniper();
        auction.reportPrice(1000,98,"other");//기존가격, 올려야하는 가격, 입찰한 사람
        application.hasShownSniperIsBidding(); // 잘 참여하고 있는지 물어보는 부분
        auction.hasReceivedBid(1098,SNIPER_ID);
        auction.announceClosed();
        application.showsSniperHasLostAuction();
    }
}
