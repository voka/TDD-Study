package Sniper;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class MainWindow extends JFrame {
    public static final String SNIPER_STATUS_NAME = "sniper_status";
    public static final String STATUS_JOINING = "joining";
    public static final String STATUS_LOST = "lost";
    public static final String STATUS_BIDDING = "bidding";
    private final JLabel sniperStatus = createLabel(STATUS_JOINING);
    public MainWindow(){ // 사용자가 볼수있는 화면 한 개를 생성
        super("Auction_Sniper");
        setName(Main.MAIN_WINDOW);
        add(sniperStatus);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private static JLabel createLabel(String initialText){
        JLabel result = new JLabel((initialText));
        result.setName(SNIPER_STATUS_NAME);
        result.setBorder(new LineBorder(Color.BLACK));
        return result;
    }

    public void showsStatus(String statusLost) {
        sniperStatus.setText(statusLost);
    }
}
