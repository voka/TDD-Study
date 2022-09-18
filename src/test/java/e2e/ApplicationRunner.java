package e2e;

import Sniper.Main;

import static e2e.FakeAuctionServer.REDIS_HOSTNAME;

public class ApplicationRunner {
    public static final String SNIPER_ID = "sniper";
    public static final String SNIPER_PASSWORD = "sniper";

    private AuctionSniperDriver driver;

    public void startBindingIn(final FakeAuctionServer auction){
        Thread thread = new Thread("Test Application"){
            @Override
            public void run(){
                try {
                    Main.main(REDIS_HOSTNAME, auction.getItemId());
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
    public void shopsSniperHasLostAuction(){
        driver.showsSniperStatus(STATUS_LOST);
    }

    public void showsSniperHasLostAuction() {
    }

    public void stop() {

    }
}
