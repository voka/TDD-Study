package e2e;

import Sniper.Main;
import Sniper.MainWindow;
import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JLabelDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;

import static org.hamcrest.Matchers.equalTo;

public class AuctionSniperDriver extends JFrameDriver {
    public AuctionSniperDriver(int timeoutMillis){
        super(new GesturePerformer(), JFrameDriver.topLevelFrame(named(Main.MAIN_WINDOW),showingOnScreen()), new AWTEventQueueProber(timeoutMillis, 100));
    }
    public void showsSniperStatus(String statusText){ //화면상의 어떤 텍스트가 보여지고 있는지 테스트 하는 부분
        new JLabelDriver(
                this, named(MainWindow.SNIPER_STATUS_NAME)).hasText(equalTo(statusText));
    }
}
