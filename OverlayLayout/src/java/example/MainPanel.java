package example;
//-*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
//@homepage@
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public final class MainPanel extends JPanel {
    public MainPanel() {
        super(new GridLayout(2, 1));
        add(new JButton("Dummy"));
        add(makeOverlayLayoutButton());
        setPreferredSize(new Dimension(320, 240));
    }
    private JComponent makeOverlayLayoutButton() {
        JButton b1 = new JButton();
        b1.setLayout(new OverlayLayout(b1));
        Insets i = b1.getBorder().getBorderInsets(b1);
        b1.setBorder(BorderFactory.createEmptyBorder(i.top, i.left, i.bottom, 4));
        b1.setAction(new AbstractAction("OverlayLayoutButton") {
            @Override public void actionPerformed(ActionEvent e) {
                Toolkit.getDefaultToolkit().beep();
            }
        });
        JButton b2 = new JButton() {
            private final Dimension dim = new Dimension(120, 24);
            @Override public Dimension getPreferredSize() {
                return dim;
            }
            @Override public Dimension getMaximumSize() {
                return getPreferredSize();
            }
            @Override public Dimension getMinimumSize() {
                return getPreferredSize();
            }
        };
        b2.setAction(new AbstractAction("\u25BC") {
            @Override public void actionPerformed(ActionEvent e) {
                System.out.println("sub");
            }
        });
        b2.setAlignmentX(Component.RIGHT_ALIGNMENT);
        b2.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        b1.add(b2);
        return b1;
    }
    public static void main(String... args) {
        EventQueue.invokeLater(new Runnable() {
            @Override public void run() {
                createAndShowGUI();
            }
        });
    }
    public static void createAndShowGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException |
                 IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
        JFrame frame = new JFrame("@title@");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(new MainPanel());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
