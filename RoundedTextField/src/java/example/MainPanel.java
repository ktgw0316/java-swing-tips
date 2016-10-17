package example;
//-*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
//@homepage@
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

public final class MainPanel extends JPanel {
    private static final TexturePaint TEXTURE = makeCheckerTexture();

    public MainPanel() {
        super(new BorderLayout());

        JTextField textField01 = new JTextField(20) {
            // Unleash Your Creativity with Swing and the Java 2D API!
            // http://java.sun.com/products/jfc/tsc/articles/swing2d/index.html
            // http://web.archive.org/web/20091205092230/http://java.sun.com/products/jfc/tsc/articles/swing2d/index.html
            @Override protected void paintComponent(Graphics g) {
                if (!isOpaque()) {
                    int w = getWidth() - 1;
                    int h = getHeight() - 1;
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setPaint(UIManager.getColor("TextField.background"));
                    g2.fillRoundRect(0, 0, w, h, h, h);
                    g2.setPaint(Color.GRAY);
                    g2.drawRoundRect(0, 0, w, h, h, h);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
            @Override public void updateUI() {
                super.updateUI();
                setOpaque(false);
                //setBackground(new Color(0x0, true));
                setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            }
        };
        textField01.setText("aaaaaaaaaaa");

        JTextField textField02 = new JTextField(20) {
            @Override protected void paintComponent(Graphics g) {
                if (!isOpaque() && getBorder() instanceof RoundedCornerBorder) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setPaint(getBackground());
                    g2.fill(((RoundedCornerBorder) getBorder()).getBorderShape(0, 0, getWidth() - 1, getHeight() - 1));
                    g2.dispose();
                }
                super.paintComponent(g);
            }
            @Override public void updateUI() {
                super.updateUI();
                setOpaque(false);
                setBorder(new RoundedCornerBorder());
            }
        };
        textField02.setText("bbbbbbbbbbbb");

        JRadioButton r1 = new JRadioButton("default", true);
        JRadioButton r2 = new JRadioButton("setOpaque(false) + TexturePaint");
        ActionListener l = e -> {
            setOpaque(e.getSource() == r1);
            repaint();
        };
        ButtonGroup bg = new ButtonGroup();
        Box box = Box.createHorizontalBox();
        box.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        for (AbstractButton b: Arrays.asList(r1, r2)) {
            b.addActionListener(l);
            b.setOpaque(false);
            bg.add(b);
            box.add(b);
        }
        JPanel p = new JPanel(new GridLayout(2, 1, 5, 5));
        p.setOpaque(false);
        p.add(makeTitlePanel(textField01, "Override: JTextField#paintComponent(...)"));
        p.add(makeTitlePanel(textField02, "setBorder(new RoundedCornerBorder())"));
        add(p);
        add(box, BorderLayout.NORTH);
        setBorder(BorderFactory.createEmptyBorder(2, 20, 20, 20));
        setPreferredSize(new Dimension(320, 240));
    }
    private JComponent makeTitlePanel(JComponent cmp, String title) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1d;
        c.fill    = GridBagConstraints.HORIZONTAL;
        c.insets  = new Insets(5, 5, 5, 5);
        p.add(cmp, c);
        p.setBorder(BorderFactory.createTitledBorder(title));
        return p;
    }
    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!isOpaque()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setPaint(TEXTURE);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }
    private static TexturePaint makeCheckerTexture() {
        int cs = 6;
        int sz = cs * cs;
        BufferedImage img = new BufferedImage(sz, sz, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setPaint(new Color(100, 100, 100, 50));
        g2.fillRect(0, 0, sz, sz);
        for (int i = 0; i * cs < sz; i++) {
            for (int j = 0; j * cs < sz; j++) {
                if ((i + j) % 2 == 0) {
                    g2.fillRect(i * cs, j * cs, cs, cs);
                }
            }
        }
        g2.dispose();
        return new TexturePaint(img, new Rectangle(sz, sz));
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
        } catch (ClassNotFoundException | InstantiationException
               | IllegalAccessException | UnsupportedLookAndFeelException ex) {
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

class RoundedCornerBorder extends AbstractBorder {
    private static final Color ALPHA_ZERO = new Color(0x0, true);
    @Override public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Shape border = getBorderShape(x, y, width - 1, height - 1);

//         Container parent = c.getParent();
//         if (Objects.nonNull(parent)) {
//             //g2.setPaint(parent.getBackground());
//             g2.setPaint(new Color(0x0, true));
//             Area corner = new Area(new Rectangle2D.Double(x, y, width, height));
//             corner.subtract(new Area(border));
//             g2.fill(corner);
//         }

        g2.setPaint(ALPHA_ZERO);
        //Area corner = new Area(border.getBounds2D());
        Area corner = new Area(new Rectangle2D.Double(x, y, width, height));
        corner.subtract(new Area(border));
        g2.fill(corner);

        g2.setPaint(Color.GRAY);
        g2.draw(border);
        g2.dispose();
    }
    public Shape getBorderShape(int x, int y, int w, int h) {
        int r = h; //h / 2;
        return new RoundRectangle2D.Double(x, y, w, h, r, r);
    }
    @Override public Insets getBorderInsets(Component c) {
        return new Insets(4, 8, 4, 8);
    }
    @Override public Insets getBorderInsets(Component c, Insets insets) {
        insets.set(4, 8, 4, 8);
        return insets;
    }
}
