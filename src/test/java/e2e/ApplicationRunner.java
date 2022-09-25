package e2e;

import Sniper.Main;

import static Sniper.MainWindow.STATUS_BIDDING;
import static Sniper.MainWindow.STATUS_JOINING;
import static Sniper.MainWindow.STATUS_LOST;

public class ApplicationRunner {
    public static final String SNIPER_ID = "sniper";
    public static final String SNIPER_PASSWORD = "sniper";

    private AuctionSniperDriver driver;

    public void startBindingIn(final FakeAuctionServer auction){
        Thread thread = new Thread("Test Application"){
            @Override
            public void run(){
                try {
                    Main.main(auction.getItemId());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        thread.setDaemon(true);
        thread.start();

        driver = new AuctionSniperDriver(1000);
        driver.showsSniperStatus(STATUS_JOINING);
    }
    public void showsSniperHasLostAuction() {
        driver.showsSniperStatus(STATUS_LOST);
    }

    public void stop() {
        driver.dispose();
    }

    public void hasShownSniperIsBidding() {
        driver.showsSniperStatus(STATUS_BIDDING);
    }
}
