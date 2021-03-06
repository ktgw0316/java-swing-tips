package example;
//-*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
//@homepage@
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.image.*;
import java.beans.*;
import java.io.IOException;
import java.util.*;
import javax.swing.*;

public final class MainPanel extends JPanel {
    private MainPanel() {
        super(new BorderLayout());
        DnDTabbedPane tabbedPane = new DnDTabbedPane();

        DnDTabbedPane sub = new DnDTabbedPane();
        sub.addTab("Title aa", new JLabel("aaa"));
        sub.addTab("Title bb", new JScrollPane(new JTree()));
        sub.addTab("Title cc", new JScrollPane(new JTextArea("JTextArea cc")));

        tabbedPane.addTab("JTree 00", new JScrollPane(new JTree()));
        tabbedPane.addTab("JLabel 01", new JLabel("Test"));
        tabbedPane.addTab("JTable 02", new JScrollPane(new JTable(10, 3)));
        tabbedPane.addTab("JTextArea 03", new JScrollPane(new JTextArea("JTextArea 03")));
        tabbedPane.addTab("JLabel 04", new JLabel("<html>asfasfdasdfasdfsa<br>asfdd13412341234123446745fgh"));
        tabbedPane.addTab("null 05", null);
        tabbedPane.addTab("JTabbedPane 06", sub);
        tabbedPane.addTab("Title 000000000000000007", new JScrollPane(new JTree()));

        // // ButtonTabComponent
        // for (int i = 0; i < tabbedPane.getTabCount(); i++) {
        //     tabbedPane.setTabComponentAt(i, new ButtonTabComponent(tabbedPane));
        //     tabbedPane.setToolTipTextAt(i, "tooltip: " + i);
        // }

        DnDTabbedPane sub2 = new DnDTabbedPane();
        sub2.addTab("Title aaa", new JLabel("aaa"));
        sub2.addTab("Title bbb", new JScrollPane(new JTree()));
        sub2.addTab("Title ccc", new JScrollPane(new JTextArea("JTextArea ccc")));

        tabbedPane.setName("JTabbedPane#main");
        sub.setName("JTabbedPane#sub1");
        sub2.setName("JTabbedPane#sub2");

        DropTargetListener dropTargetListener = new TabDropTargetAdapter();
        TransferHandler handler = new TabTransferHandler();
        Arrays.asList(tabbedPane, sub, sub2).forEach(t -> {
            t.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
            t.setTransferHandler(handler);
            try {
                t.getDropTarget().addDropTargetListener(dropTargetListener);
            } catch (TooManyListenersException ex) {
                ex.printStackTrace();
            }
        });

        JPanel p = new JPanel(new GridLayout(2, 1));
        p.add(tabbedPane);
        p.add(sub2);
        add(p);
        add(makeCheckBoxPanel(tabbedPane), BorderLayout.NORTH);
        setPreferredSize(new Dimension(320, 240));
    }
    private static Component makeCheckBoxPanel(JTabbedPane tabbedPane) {
        JCheckBox tcheck = new JCheckBox("Top", true);
        tcheck.addActionListener(e -> tabbedPane.setTabPlacement(tcheck.isSelected() ? JTabbedPane.TOP : JTabbedPane.RIGHT));
        JCheckBox scheck = new JCheckBox("SCROLL_TAB_LAYOUT", true);
        scheck.addActionListener(e -> tabbedPane.setTabLayoutPolicy(scheck.isSelected() ? JTabbedPane.SCROLL_TAB_LAYOUT : JTabbedPane.WRAP_TAB_LAYOUT));
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.add(tcheck);
        p.add(scheck);
        return p;
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

class DnDTabbedPane extends JTabbedPane {
    private static final int SCROLL_SIZE = 20; // Test
    private static final int BUTTON_SIZE = 30; // XXX 30 is magic number of scroll button size
    private static final int LINE_WIDTH = 3;
    private static final Rectangle RECT_BACKWARD = new Rectangle();
    private static final Rectangle RECT_FORWARD = new Rectangle();
    protected static final Rectangle RECT_LINE = new Rectangle();
    private final DropMode dropMode = DropMode.INSERT;
    public int dragTabIndex = -1;
    private transient DropLocation dropLocation;

    public static final class DropLocation extends TransferHandler.DropLocation {
        private final int index;
        private boolean dropable = true;
        protected DropLocation(Point p, int index) {
            super(p);
            this.index = index;
        }
        public int getIndex() {
            return index;
        }
        public void setDroppable(boolean flag) {
            dropable = flag;
        }
        public boolean isDroppable() {
            return dropable;
        }
//         @Override public String toString() {
//             return getClass().getName()
//                    + "[dropPoint=" + getDropPoint() + ","
//                    + "index=" + index + ","
//                    + "insert=" + isInsert + "]";
//         }
    }
    private void clickArrowButton(String actionKey) {
        JButton scrollForwardButton = null;
        JButton scrollBackwardButton = null;
        for (Component c: getComponents()) {
            if (c instanceof JButton) {
                if (scrollForwardButton == null && scrollBackwardButton == null) {
                    scrollForwardButton = (JButton) c;
                } else if (scrollBackwardButton == null) {
                    scrollBackwardButton = (JButton) c;
                }
            }
        }
        JButton button = "scrollTabsForwardAction".equals(actionKey) ? scrollForwardButton : scrollBackwardButton;
        Optional.ofNullable(button)
            .filter(JButton::isEnabled)
            .ifPresent(JButton::doClick);

//         // ArrayIndexOutOfBoundsException
//         Optional.ofNullable(getActionMap())
//             .map(am -> am.get(actionKey))
//             .filter(Action::isEnabled)
//             .ifPresent(a -> a.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null, 0, 0)));
// //         ActionMap map = getActionMap();
// //         if (Objects.nonNull(map)) {
// //             Action action = map.get(actionKey);
// //             if (Objects.nonNull(action) && action.isEnabled()) {
// //                 action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null, 0, 0));
// //             }
// //         }
    }
    public void autoScrollTest(Point pt) {
        Rectangle r = getTabAreaBounds();
        // int tabPlacement = getTabPlacement();
        // if (tabPlacement == TOP || tabPlacement == BOTTOM) {
        if (isTopBottomTabPlacement(getTabPlacement())) {
            RECT_BACKWARD.setBounds(r.x, r.y, SCROLL_SIZE, r.height);
            RECT_FORWARD.setBounds(r.x + r.width - SCROLL_SIZE - BUTTON_SIZE, r.y, SCROLL_SIZE + BUTTON_SIZE, r.height);
        } else { // if (tabPlacement == LEFT || tabPlacement == RIGHT) {
            RECT_BACKWARD.setBounds(r.x, r.y, r.width, SCROLL_SIZE);
            RECT_FORWARD.setBounds(r.x, r.y + r.height - SCROLL_SIZE - BUTTON_SIZE, r.width, SCROLL_SIZE + BUTTON_SIZE);
        }
        if (RECT_BACKWARD.contains(pt)) {
            clickArrowButton("scrollTabsBackwardAction");
        } else if (RECT_FORWARD.contains(pt)) {
            clickArrowButton("scrollTabsForwardAction");
        }
    }
    protected DnDTabbedPane() {
        super();
        Handler h = new Handler();
        addMouseListener(h);
        addMouseMotionListener(h);
        addPropertyChangeListener(h);
    }
    public DropLocation dropLocationForPoint(Point p) {
        if (dropMode != DropMode.INSERT) {
            assert false : "Unexpected drop mode";
        }
        for (int i = 0; i < getTabCount(); i++) {
            if (getBoundsAt(i).contains(p)) {
                return new DropLocation(p, i);
            }
        }
        if (getTabAreaBounds().contains(p)) {
            return new DropLocation(p, getTabCount());
        }
        return new DropLocation(p, -1);
//         switch (dropMode) {
//             case INSERT:
//                 for (int i = 0; i < getTabCount(); i++) {
//                     if (getBoundsAt(i).contains(p)) {
//                         return new DropLocation(p, i);
//                     }
//                 }
//                 if (getTabAreaBounds().contains(p)) {
//                     return new DropLocation(p, getTabCount());
//                 }
//                 break;
//             case USE_SELECTION:
//             case ON:
//             case ON_OR_INSERT:
//             default:
//                 assert false : "Unexpected drop mode";
//                 break;
//         }
//         return new DropLocation(p, -1);
    }
    public final DropLocation getDropLocation() {
        return dropLocation;
    }
    public Object setDropLocation(TransferHandler.DropLocation location, Object state, boolean forDrop) {
        DropLocation old = dropLocation;
        if (Objects.isNull(location) || !forDrop) {
            dropLocation = new DropLocation(new Point(), -1);
        } else if (location instanceof DropLocation) {
            dropLocation = (DropLocation) location;
        }
        firePropertyChange("dropLocation", old, dropLocation);
        return null;
    }
    public void exportTab(int dragIndex, JTabbedPane target, int targetIndex) {
        System.out.println("exportTab");
        Component cmp = getComponentAt(dragIndex);
        Component tab = getTabComponentAt(dragIndex);
        String title = getTitleAt(dragIndex);
        Icon icon = getIconAt(dragIndex);
        String tip = getToolTipTextAt(dragIndex);
        boolean isEnabled = isEnabledAt(dragIndex);
        remove(dragIndex);
        target.insertTab(title, icon, cmp, tip, targetIndex);
        target.setEnabledAt(targetIndex, isEnabled);

        // // ButtonTabComponent
        // if (tab instanceof ButtonTabComponent) {
        //     tab = new ButtonTabComponent(target);
        // }

        target.setTabComponentAt(targetIndex, tab);
        target.setSelectedIndex(targetIndex);
        if (tab instanceof JComponent) {
            ((JComponent) tab).scrollRectToVisible(tab.getBounds());
        }
    }
    public void convertTab(int prev, int next) {
        System.out.println("convertTab");
//         if (next < 0 || prev == next) {
//             return;
//         }
        Component cmp = getComponentAt(prev);
        Component tab = getTabComponentAt(prev);
        String title = getTitleAt(prev);
        Icon icon = getIconAt(prev);
        String tip = getToolTipTextAt(prev);
        boolean isEnabled = isEnabledAt(prev);
        int tgtindex = prev > next ? next : next - 1;
        remove(prev);
        insertTab(title, icon, cmp, tip, tgtindex);
        setEnabledAt(tgtindex, isEnabled);
        // When you drag'n'drop a disabled tab, it finishes enabled and selected.
        // pointed out by dlorde
        if (isEnabled) {
            setSelectedIndex(tgtindex);
        }
        // I have a component in all tabs (jlabel with an X to close the tab) and when i move a tab the component disappear.
        // pointed out by Daniel Dario Morales Salas
        setTabComponentAt(tgtindex, tab);
    }
    public Optional<Rectangle> getDropLineRect() {
        int index = Optional.ofNullable(getDropLocation())
            .filter(DropLocation::isDroppable)
            .map(DropLocation::getIndex)
            .orElse(-1);
        if (index < 0) {
            RECT_LINE.setBounds(0, 0, 0, 0);
            return Optional.empty();
        }
        int a = Math.min(index, 1); // index == 0 ? 0 : 1;
        Rectangle r = getBoundsAt(a * (index - 1));
        if (isTopBottomTabPlacement(getTabPlacement())) {
            RECT_LINE.setBounds(r.x - LINE_WIDTH / 2 + r.width * a, r.y, LINE_WIDTH, r.height);
        } else {
            RECT_LINE.setBounds(r.x, r.y - LINE_WIDTH / 2 + r.height * a, r.width, LINE_WIDTH);
        }
        return Optional.of(RECT_LINE);
    }

//     public Rectangle getTabAreaBounds() {
//         Rectangle tabbedRect = getBounds();
//         Component c = getSelectedComponent();
//         if (Objects.isNull(c)) {
//             return tabbedRect;
//         }
//         int xx = tabbedRect.x;
//         int yy = tabbedRect.y;
//         Rectangle compRect = getSelectedComponent().getBounds();
//         int tabPlacement = getTabPlacement();
//         if (tabPlacement == TOP) {
//             tabbedRect.height = tabbedRect.height - compRect.height;
//         } else if (tabPlacement == BOTTOM) {
//             tabbedRect.y = tabbedRect.y + compRect.y + compRect.height;
//             tabbedRect.height = tabbedRect.height - compRect.height;
//         } else if (tabPlacement == LEFT) {
//             tabbedRect.width = tabbedRect.width - compRect.width;
//         } else { // if (tabPlacement == RIGHT) {
//             tabbedRect.x = tabbedRect.x + compRect.x + compRect.width;
//             tabbedRect.width = tabbedRect.width - compRect.width;
//         }
//         tabbedRect.translate(-xx, -yy);
//         // tabbedRect.grow(2, 2);
//         return tabbedRect;
//     }
    public Rectangle getTabAreaBounds() {
        Rectangle tabbedRect = getBounds();
        int xx = tabbedRect.x;
        int yy = tabbedRect.y;
        Rectangle compRect = Optional.ofNullable(getSelectedComponent()).map(Component::getBounds).orElseGet(Rectangle::new);
        int tabPlacement = getTabPlacement();
        if (isTopBottomTabPlacement(tabPlacement)) {
            tabbedRect.height = tabbedRect.height - compRect.height;
            if (tabPlacement == BOTTOM) {
                tabbedRect.y += compRect.y + compRect.height;
            }
        } else {
            tabbedRect.width = tabbedRect.width - compRect.width;
            if (tabPlacement == RIGHT) {
                tabbedRect.x += compRect.x + compRect.width;
            }
        }
        tabbedRect.translate(-xx, -yy);
        // tabbedRect.grow(2, 2);
        return tabbedRect;
    }

    public static boolean isTopBottomTabPlacement(int tabPlacement) {
        return tabPlacement == JTabbedPane.TOP || tabPlacement == JTabbedPane.BOTTOM;
    }

    private class Handler extends MouseAdapter implements PropertyChangeListener { // , BeforeDrag
        private Point startPt;
        private final int gestureMotionThreshold = DragSource.getDragThreshold();
        // private final Integer gestureMotionThreshold = (Integer) Toolkit.getDefaultToolkit().getDesktopProperty("DnD.gestureMotionThreshold");

        private void repaintDropLocation() {
            Component c = getRootPane().getGlassPane();
            if (c instanceof GhostGlassPane) {
                GhostGlassPane glassPane = (GhostGlassPane) c;
                glassPane.setTargetTabbedPane(DnDTabbedPane.this);
                glassPane.repaint();
            }
        }
        // PropertyChangeListener
        @Override public void propertyChange(PropertyChangeEvent e) {
            String propertyName = e.getPropertyName();
            if ("dropLocation".equals(propertyName)) {
                // System.out.println("propertyChange: dropLocation");
                repaintDropLocation();
            }
        }
        // MouseListener
        @Override public void mousePressed(MouseEvent e) {
            DnDTabbedPane src = (DnDTabbedPane) e.getComponent();
            boolean isOnlyOneTab = src.getTabCount() <= 1;
            if (isOnlyOneTab) {
                startPt = null;
                return;
            }
            Point tabPt = e.getPoint(); // e.getDragOrigin();
            int idx = src.indexAtLocation(tabPt.x, tabPt.y);
            // disabled tab, null component problem.
            // pointed out by daryl. NullPointerException: i.e. addTab("Tab", null)
            boolean flag = idx < 0 || !src.isEnabledAt(idx) || Objects.isNull(src.getComponentAt(idx));
            startPt = flag ? null : tabPt;
        }
        @Override public void mouseDragged(MouseEvent e) {
            Point tabPt = e.getPoint(); // e.getDragOrigin();
            if (Objects.nonNull(startPt) && startPt.distance(tabPt) > gestureMotionThreshold) {
                DnDTabbedPane src = (DnDTabbedPane) e.getComponent();
                TransferHandler th = src.getTransferHandler();
                dragTabIndex = src.indexAtLocation(tabPt.x, tabPt.y);
                th.exportAsDrag(src, e, TransferHandler.MOVE);
                RECT_LINE.setBounds(0, 0, 0, 0);
                src.getRootPane().getGlassPane().setVisible(true);
                src.setDropLocation(new DropLocation(tabPt, -1), null, true);
                startPt = null;
            }
        }
    }
}

class TabDropTargetAdapter extends DropTargetAdapter {
    private void clearDropLocationPaint(Component c) {
        if (c instanceof DnDTabbedPane) {
            DnDTabbedPane t = (DnDTabbedPane) c;
            t.setDropLocation(null, null, false);
            t.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }
    @Override public void drop(DropTargetDropEvent dtde) {
        Component c = dtde.getDropTargetContext().getComponent();
        System.out.println("DropTargetListener#drop: " + c.getName());
        clearDropLocationPaint(c);
    }
    @Override public void dragExit(DropTargetEvent dte) {
        Component c = dte.getDropTargetContext().getComponent();
        System.out.println("DropTargetListener#dragExit: " + c.getName());
        clearDropLocationPaint(c);
    }
    @Override public void dragEnter(DropTargetDragEvent dtde) {
        Component c = dtde.getDropTargetContext().getComponent();
        System.out.println("DropTargetListener#dragEnter: " + c.getName());
    }
//     @Override public void dragOver(DropTargetDragEvent dtde) {
//         // System.out.println("dragOver");
//     }
//     @Override public void dropActionChanged(DropTargetDragEvent dtde) {
//         System.out.println("dropActionChanged");
//     }
}

class DnDTabData {
    public final DnDTabbedPane tabbedPane;
    protected DnDTabData(DnDTabbedPane tabbedPane) {
        this.tabbedPane = tabbedPane;
    }
}

class TabTransferHandler extends TransferHandler {
    protected final DataFlavor localObjectFlavor;
    protected DnDTabbedPane source;

    protected TabTransferHandler() {
        super();
        System.out.println("TabTransferHandler");
        // localObjectFlavor = new ActivationDataFlavor(DnDTabbedPane.class, DataFlavor.javaJVMLocalObjectMimeType, "DnDTabbedPane");
        localObjectFlavor = new DataFlavor(DnDTabData.class, "DnDTabData");
    }
    @Override protected Transferable createTransferable(JComponent c) {
        System.out.println("createTransferable");
        if (c instanceof DnDTabbedPane) {
            source = (DnDTabbedPane) c;
        }
        // return new DataHandler(c, localObjectFlavor.getMimeType());
        return new Transferable() {
            @Override public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[] {localObjectFlavor};
            }
            @Override public boolean isDataFlavorSupported(DataFlavor flavor) {
                return Objects.equals(localObjectFlavor, flavor);
            }
            @Override public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                if (isDataFlavorSupported(flavor)) {
                    return new DnDTabData(source);
                } else {
                    throw new UnsupportedFlavorException(flavor);
                }
            }
        };
    }
    @Override public boolean canImport(TransferHandler.TransferSupport support) {
        // System.out.println("canImport");
        if (!support.isDrop() || !support.isDataFlavorSupported(localObjectFlavor)) {
            System.out.println("canImport:" + support.isDrop() + " " + support.isDataFlavorSupported(localObjectFlavor));
            return false;
        }
        support.setDropAction(TransferHandler.MOVE);
        DropLocation tdl = support.getDropLocation();
        Point pt = tdl.getDropPoint();
        DnDTabbedPane target = (DnDTabbedPane) support.getComponent();
        target.autoScrollTest(pt);
        DnDTabbedPane.DropLocation dl = (DnDTabbedPane.DropLocation) target.dropLocationForPoint(pt);
        int idx = dl.getIndex();

//         if (!isWebStart()) {
//             // System.out.println("local");
//             try {
//                 source = (DnDTabbedPane) support.getTransferable().getTransferData(localObjectFlavor);
//             } catch (Exception ex) {
//                 ex.printStackTrace();
//             }
//         }

        boolean isDroppable = false;
        boolean isAreaContains = target.getTabAreaBounds().contains(pt) && idx >= 0;
        if (target.equals(source)) {
            // System.out.println("target == source");
            isDroppable = isAreaContains && idx != target.dragTabIndex && idx != target.dragTabIndex + 1;
        } else {
            // System.out.format("target!=source%n  target: %s%n  source: %s", target.getName(), source.getName());
            isDroppable = Optional.ofNullable(source).map(c -> !c.isAncestorOf(target)).orElse(false) && isAreaContains;
        }

        // [JDK-6700748] Cursor flickering during D&D when using CellRendererPane with validation - Java Bug System
        // https://bugs.openjdk.java.net/browse/JDK-6700748
        Cursor cursor = isDroppable ? DragSource.DefaultMoveDrop : DragSource.DefaultMoveNoDrop;
        Component glassPane = target.getRootPane().getGlassPane();
        glassPane.setCursor(cursor);
        target.setCursor(cursor);

        support.setShowDropLocation(isDroppable);
        dl.setDroppable(isDroppable);
        target.setDropLocation(dl, null, isDroppable);
        return isDroppable;
    }
//     private static boolean isWebStart() {
//         try {
//             ServiceManager.lookup("javax.jnlp.BasicService");
//             return true;
//         } catch (UnavailableServiceException ex) {
//             return false;
//         }
//     }
    private BufferedImage makeDragTabImage(DnDTabbedPane tabbedPane) {
        Rectangle rect = tabbedPane.getBoundsAt(tabbedPane.dragTabIndex);
        BufferedImage image = new BufferedImage(tabbedPane.getWidth(), tabbedPane.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g2 = image.createGraphics();
        tabbedPane.paint(g2);
        g2.dispose();
        if (rect.x < 0) {
            rect.translate(-rect.x, 0);
        }
        if (rect.y < 0) {
            rect.translate(0, -rect.y);
        }
        if (rect.x + rect.width > image.getWidth()) {
            rect.width = image.getWidth() - rect.x;
        }
        if (rect.y + rect.height > image.getHeight()) {
            rect.height = image.getHeight() - rect.y;
        }
        return image.getSubimage(rect.x, rect.y, rect.width, rect.height);
    }
    @Override public int getSourceActions(JComponent c) {
        System.out.println("getSourceActions");
        if (c instanceof DnDTabbedPane) {
            DnDTabbedPane src = (DnDTabbedPane) c;
            c.getRootPane().setGlassPane(new GhostGlassPane(src));
            if (src.dragTabIndex < 0) {
                return TransferHandler.NONE;
            }
            setDragImage(makeDragTabImage(src));
            c.getRootPane().getGlassPane().setVisible(true);
            return TransferHandler.MOVE;
        }
        return TransferHandler.NONE;
    }
    @Override public boolean importData(TransferHandler.TransferSupport support) {
        System.out.println("importData");
        if (!canImport(support)) {
            return false;
        }

        DnDTabbedPane target = (DnDTabbedPane) support.getComponent();
        DnDTabbedPane.DropLocation dl = target.getDropLocation();
        try {
            DnDTabData data = (DnDTabData) support.getTransferable().getTransferData(localObjectFlavor);
            DnDTabbedPane src = data.tabbedPane;
            int index = dl.getIndex(); // boolean insert = dl.isInsert();
            if (target.equals(src)) {
                src.convertTab(src.dragTabIndex, index); // getTargetTabIndex(e.getLocation()));
            } else {
                src.exportTab(src.dragTabIndex, target, index);
            }
            return true;
        } catch (UnsupportedFlavorException | IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    @Override protected void exportDone(JComponent c, Transferable data, int action) {
        System.out.println("exportDone");
        DnDTabbedPane src = (DnDTabbedPane) c;
        src.getRootPane().getGlassPane().setVisible(false);
        src.setDropLocation(null, null, false);
        src.repaint();
        src.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
}

class GhostGlassPane extends JComponent {
    private DnDTabbedPane tabbedPane;
    protected GhostGlassPane(DnDTabbedPane tabbedPane) {
        super();
        this.tabbedPane = tabbedPane;
        setOpaque(false);
    }
    public void setTargetTabbedPane(DnDTabbedPane tab) {
        tabbedPane = tab;
    }
    @Override protected void paintComponent(Graphics g) {
        tabbedPane.getDropLineRect().ifPresent(rect -> {
            Graphics2D g2 = (Graphics2D) g.create();
            Rectangle r = SwingUtilities.convertRectangle(tabbedPane, rect, this);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f));
            g2.setPaint(Color.RED);
            g2.fill(r);
            g2.dispose();
        });
    }
}

/* a closeable tab test
// How to Use Tabbed Panes (The Java™ Tutorials > Creating a GUI With JFC/Swing > Using Swing Components)
// https://docs.oracle.com/javase/tutorial/uiswing/components/tabbedpane.html
class ButtonTabComponent extends JPanel {
    protected final JTabbedPane tabbedPane;

    protected ButtonTabComponent(JTabbedPane tabbedPane) {
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.tabbedPane = Optional.ofNullable(tabbedPane).orElseThrow(() -> new IllegalArgumentException("TabbedPane cannot be null"));
        setOpaque(false);
        JLabel label = new JLabel() {
            @Override public String getText() {
                int i = tabbedPane.indexOfTabComponent(ButtonTabComponent.this);
                if (i != -1) {
                    return tabbedPane.getTitleAt(i);
                }
                return null;
            }
        };
        add(label);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        JButton button = new TabButton();
        TabButtonHandler handler = new TabButtonHandler();
        button.addActionListener(handler);
        button.addMouseListener(handler);
        add(button);
        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
    }
    private class TabButtonHandler extends MouseAdapter implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            int i = tabbedPane.indexOfTabComponent(ButtonTabComponent.this);
            if (i != -1) {
                tabbedPane.remove(i);
            }
        }
        @Override public void mouseEntered(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(true);
            }
        }
        @Override public void mouseExited(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(false);
            }
        }
    }
}

class TabButton extends JButton {
    private static final int SIZE = 17;
    private static final int DELTA = 6;

    protected TabButton() {
        super();
        setUI(new BasicButtonUI());
        setToolTipText("close this tab");
        setContentAreaFilled(false);
        setFocusable(false);
        setBorder(BorderFactory.createEtchedBorder());
        setBorderPainted(false);
        setRolloverEnabled(true);
    }
    @Override public Dimension getPreferredSize() {
        return new Dimension(SIZE, SIZE);
    }
    @Override public void updateUI() {
        // we don't want to update UI for this button
    }
    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setStroke(new BasicStroke(2));
        g2.setPaint(Color.BLACK);
        if (getModel().isRollover()) {
            g2.setPaint(Color.ORANGE);
        }
        if (getModel().isPressed()) {
            g2.setPaint(Color.BLUE);
        }
        g2.drawLine(DELTA, DELTA, getWidth() - DELTA - 1, getHeight() - DELTA - 1);
        g2.drawLine(getWidth() - DELTA - 1, DELTA, DELTA, getHeight() - DELTA - 1);
        g2.dispose();
    }
}
*/
