package Sniper;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class MainWindow extends JFrame {
    public static final String SNIPER_STATUS_NAME = "sniper_status";
    private final JLabel sniperStatus = createLabel(STATUS_JOINING);
    public MainWindow(){
        super("Auction_Sniper");
        setName(MAIN_WINDOW_NAME);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private static JLabel createLabel(String initialText){
        JLabel result = new JLabel((initialText));
        result.setBorder(new LineBorder(Color.BLACK));
        return result;
    }
}
