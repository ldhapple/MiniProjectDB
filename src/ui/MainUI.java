package ui;

import java.awt.CardLayout;
import java.awt.Graphics;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import ui.panel.CustomerPanel;
import ui.panel.PlanPanel;

public class MainUI extends JFrame {
    private CustomerPanel customerPanel;
    private PlanPanel planPanel;

    public MainUI() {
        defaultSetting();

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("메뉴");
        JMenuItem customerManageMenu = new JMenuItem("고객 관리");
        JMenuItem planMenu = new JMenuItem("요금제 목록");
        menu.add(customerManageMenu);
        menu.add(planMenu);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        createPanel();

        customerManageMenu.addActionListener(e -> switchPanel("customer"));
        planMenu.addActionListener(e -> switchPanel("plan"));
    }

    private void defaultSetting() {
        setTitle("UPlus 고객 관리 시스템");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void createPanel() {
        customerPanel = new CustomerPanel();
        planPanel = new PlanPanel();

        setLayout(new CardLayout());
        add(customerPanel, "customer");
        add(planPanel, "plan");
    }

    private void switchPanel(String panelName) {
        CardLayout cardLayout = (CardLayout) getContentPane().getLayout();
        cardLayout.show(getContentPane(), panelName);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainUI mainFrame = new MainUI();
            mainFrame.setVisible(true);
        });
    }
}
