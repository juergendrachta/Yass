/*
 * Yass - Karaoke Editor
 * Copyright (C) 2009 Saruta
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package yass;

import yass.renderer.YassNote;
import yass.renderer.YassPlayerNote;
import yass.renderer.YassSession;
import yass.renderer.YassTrack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Description of the Class
 *
 * @author Saruta
 */
public class YassSheet extends JPanel implements Scrollable,
        yass.renderer.YassPlaybackRenderer {

    public final static int NORM_HEIGHT = 20;
    private static final long serialVersionUID = 3284920111520989009L;
    private final static int ACTION_CONTROL = 1, ACTION_ALT = 2,
            ACTION_CONTROL_ALT = 4, ACTION_NONE = 0;

    private final static int SKETCH_LENGTH = 30;
    private final static int SKETCH_UP = 1, SKETCH_DOWN = 2, SKETCH_LEFT = 3,
            SKETCH_RIGHT = 4, SKETCH_NONE = 0;
    private final static int SKETCH_HORIZONTAL = -1, SKETCH_VERTICAL = -2;
    private final static int[] GESTURE_UP = new int[]{SKETCH_VERTICAL,
            SKETCH_UP};
    private final static int[] GESTURE_UP_DOWN = new int[]{SKETCH_VERTICAL,
            SKETCH_UP, SKETCH_DOWN};
    private final static int[] GESTURE_DOWN_UP = new int[]{SKETCH_VERTICAL,
            SKETCH_DOWN, SKETCH_UP};
    private final static int[] GESTURE_UP_DOWN_UP = new int[]{
            SKETCH_VERTICAL, SKETCH_UP, SKETCH_DOWN, SKETCH_UP};
    private final static int[] GESTURE_DOWN_UP_DOWN = new int[]{
            SKETCH_VERTICAL, SKETCH_DOWN, SKETCH_UP, SKETCH_DOWN};
    private final static int[] GESTURE_LEFT = new int[]{SKETCH_HORIZONTAL,
            SKETCH_LEFT};
    private final static int[] GESTURE_RIGHT = new int[]{SKETCH_HORIZONTAL,
            SKETCH_RIGHT};
    private final static int[] GESTURE_LEFT_RIGHT = new int[]{
            SKETCH_HORIZONTAL, SKETCH_LEFT, SKETCH_RIGHT};
    private final static int[] GESTURE_RIGHT_LEFT = new int[]{
            SKETCH_HORIZONTAL, SKETCH_RIGHT, SKETCH_LEFT};
    private final static int[] GESTURE_LEFT_RIGHT_LEFT = new int[]{
            SKETCH_HORIZONTAL, SKETCH_LEFT, SKETCH_RIGHT, SKETCH_LEFT};
    private final static int[] GESTURE_RIGHT_LEFT_RIGHT = new int[]{
            SKETCH_HORIZONTAL, SKETCH_RIGHT, SKETCH_LEFT, SKETCH_RIGHT};
    private final static int[] GESTURE_DOWN = new int[]{SKETCH_VERTICAL,
            SKETCH_DOWN};
    private final static int fs = 14;

    private Font font = new Font("SansSerif", Font.BOLD, fs);
    private Font fontv = new Font("SansSerif", Font.PLAIN, fs);
    private Font fonti = new Font("SansSerif", Font.ITALIC, fs);
    private Font fontb = new Font("SansSerif", Font.BOLD, fs + 2);
    private Font big[] = new Font[]{new Font("SansSerif", Font.BOLD, fs - 8),
            new Font("SansSerif", Font.BOLD, fs - 8),
            new Font("SansSerif", Font.BOLD, fs - 8),
            new Font("SansSerif", Font.BOLD, fs - 8),
            new Font("SansSerif", Font.BOLD, fs - 8),
            new Font("SansSerif", Font.BOLD, fs - 8),
            new Font("SansSerif", Font.BOLD, fs - 8),
            new Font("SansSerif", Font.BOLD, fs - 8),
            new Font("SansSerif", Font.BOLD, fs - 8),
            new Font("SansSerif", Font.BOLD, fs - 8),
            new Font("SansSerif", Font.BOLD, fs - 8),
            new Font("SansSerif", Font.BOLD, fs - 7),
            new Font("SansSerif", Font.BOLD, fs - 6),
            new Font("SansSerif", Font.BOLD, fs - 5),
            new Font("SansSerif", Font.BOLD, fs - 4),
            new Font("SansSerif", Font.BOLD, fs - 3),
            new Font("SansSerif", Font.BOLD, fs - 2),
            new Font("SansSerif", Font.BOLD, fs - 1),
            new Font("SansSerif", Font.BOLD, fs),
            new Font("SansSerif", Font.BOLD, fs + 1),
            new Font("SansSerif", Font.BOLD, fs + 2),
            new Font("SansSerif", Font.BOLD, fs + 3),
            new Font("SansSerif", Font.BOLD, fs + 4),
            new Font("SansSerif", Font.BOLD, fs + 5),
            new Font("SansSerif", Font.BOLD, fs + 6),
            new Font("SansSerif", Font.BOLD, fs + 7),
            new Font("SansSerif", Font.BOLD, fs + 8),
            new Font("SansSerif", Font.BOLD, fs + 9),
            new Font("SansSerif", Font.BOLD, fs + 10),
            new Font("SansSerif", Font.BOLD, fs + 11),
            new Font("SansSerif", Font.BOLD, fs + 12),
            new Font("SansSerif", Font.BOLD, fs + 13),
            new Font("SansSerif", Font.BOLD, fs + 14),
            new Font("SansSerif", Font.BOLD, fs + 15),
            new Font("SansSerif", Font.BOLD, fs + 16),
            new Font("SansSerif", Font.BOLD, fs + 17),
            new Font("SansSerif", Font.BOLD, fs + 18),
            new Font("SansSerif", Font.BOLD, fs + 19),
            new Font("SansSerif", Font.BOLD, fs + 20)};
    private final static int UNDEFINED = 0;
    private int dragDir = UNDEFINED;
    private int hiliteCue = UNDEFINED, dragMode = UNDEFINED;

    /*
     * new Color(1f, 1f, 1f, .7f), new Color(.7f, .7f, .7f, .7f), new Color(.4f,
     * .6f, .8f, .8f), new Color(.8f, .8f, .5f, .7f), new Color(.8f, .5f, .8f,
     * .7f), new Color(.9f, .7f, .7f, .7f)
     */
    private final static int VERTICAL = 1;
    private final static int HORIZONTAL = 2;
    private final static int LEFT = 1;
    private final static int RIGHT = 2;
    private final static int CENTER = 3;
    private final static int CUT = 4;
    private final static int JOIN_LEFT = 5;
    private final static int JOIN_RIGHT = 6;
    private final static int SNAPSHOT = 7;
    private final static int MOVE_REMAINDER = 8;
    private final static int PREV_PAGE = 9;
    private final static int NEXT_PAGE = 10;
    private final static int PREV_PAGE_PRESSED = 11;
    private final static int NEXT_PAGE_PRESSED = 12;
    private final static int SLIDE = 14;
    private final static int PLAY_NOTE = 15;
    private final static int PLAY_PAGE = 16;
    private final static int PLAY_NOTE_PRESSED = 18;
    private final static int PLAY_PAGE_PRESSED = 19;
    Color shadow = UIManager.getColor("Button.shadow");
    Color highlight = UIManager.getColor("Button.highlight");
    boolean useSketching = false, useSketchingPlayback = false;
    AffineTransform identity = new AffineTransform();
    String bufferlost = I18.get("sheet_msg_buffer_lost");
    VolatileImage backVolImage = null, plainVolImage = null;
    String hNoteTable[] = new String[]{"C", "C#", "D", "D#", "E", "F", "F#",
            "G", "G#", "A", "B", "H"};
    String bNoteTable[] = new String[]{"C", "C#", "D", "D#", "E", "F", "F#",
            "G", "G#", "A", "A#", "B"};
    String actualNoteTable[] = bNoteTable;
    boolean paintHeights = false;
    boolean live = false;
    String toomuchtext = I18.get("sheet_msg_too_much_text");
    private YassTable table = null;
    private Vector<YassTable> tables = new Vector<>();
    private Vector<Vector<YassRectangle>> rects = new Vector<>();
    private Vector<YassRectangle> rect = null;
    private Vector<Cloneable> snapshot = null, snapshotRect = null;
    private YassActions actions = null;
    private boolean noshade = false;
    private boolean autoTrim = false;
    private boolean temporaryZoomOff = false;
    private long lastMidiTime = -1;
    private long lastDragTime = -1;
    private int lyricsWidth = 400;
    private boolean lyricsVisible = true;
    private boolean messageMemory = false;
    private Color disabledColor = new JPanel().getBackground();
    // gray, blue, golden, freestyle, red
    public static final int COLORSET_COUNT = 7;
    private Color colorSet[] = new Color[COLORSET_COUNT];
    private Color playertextBG = new Color(1f, 1f, 1f, .9f);
    private int keycodes[] = new int[17];
    private long equalsKeyMillis = 0;
    private String layout = "East";
    private Color playBlueHi = new Color(1f, 1f, 1f, 1f);
    private Color playBlue = new Color(.4f, .6f, .8f, 1f);
    private Color gray = new Color(.7f, .7f, .7f, .7f);
    Color darkShadow = gray;
    private Color hiGray = new Color(.6f, .6f, .6f, 1f);
    private Color hiGray2 = new Color(.9f, .9f, .9f, 1f);
    private Color oddpageColor = gray, evenpageColor = hiGray2;
    private Color dkGray = new Color(.4f, .4f, .4f, .7f);
    private Color blue = new Color(.4f, .6f, .8f, .7f);
    private Color blueDrag = new Color(.8f, .9f, 1f, .5f);
    private Color dkRed = new Color(.8f, .4f, .4f, .7f);
    private Color white = new Color(1f, 1f, 1f);
    private Color playerColor = new Color(1f, .1f, .1f, .5f);
    private Color playerColor2 = new Color(1f, .1f, .1f, .3f);
    private Color playerColor3 = new Color(1f, .1f, .1f, .1f);
    private Color inoutColor = new Color(.9f, .9f, 1f, .5f);
    private Color inoutSnapshotBarColor = new Color(.3f, .3f, .5f, .7f);
    private Color inoutBarColor = new Color(.5f, .5f, .7f, .7f);
    private Paint tex = null, bgtex = null;
    private BufferedImage bgImage = null;
    private boolean showVideo = false, showBackground = false;
    private boolean mouseover = true;
    private boolean paintSnapshot = false, showArrows = true,
            showPlayerButtons = true, showText = true;
    private int hiliteAction = 0;

    /*
     * Description of the Method
     *
     * @return Description of the Return Value
     */
    private long lastTime = -1;
    private String lastTimeString = "";
    private int LEFT_BORDER = 36, RIGHT_BORDER = 36;
    private int TOP_BORDER = 20, BOTTOM_BORDER = 56, TOP_LINE,
            TOP_PLAYER_BUTTONS;
    private Point[] sketch = null;
    private int sketchPos = 0, dirPos = 0;
    private long sketchStartTime = 0;
    private int[] sketchDirs = null;
    private boolean sketchStarted = false;
    private BasicStroke thinStroke = new BasicStroke(0.5f),
            stdStroke = new BasicStroke(1f), medStroke = new BasicStroke(1.5f),
            thickStroke = new BasicStroke(2f);
    private Font smallFont = new Font("SansSerif", Font.PLAIN, 10);
    private int minHeight = 0, maxHeight = 18;
    private int hit = -1, hilite = -1, hiliteHeight = 1000, hhPageMin = 0;
    private int heightBoxWidth = 74;
    private Rectangle2D.Double select = new Rectangle2D.Double(0, 0, 0, 0);
    private double selectX, selectY;
    private double wSize = 30, hSize = -1;
    private int dragOffsetX = 0, dragOffsetY = 0, slideX = 0;
    private double dragOffsetXRatio = 0;
    private boolean pan = false, isPlaying = false, isTemporaryStop = false;
    private double bpm = -1, gap = 0, beatgap = 0, duration = -1;
    private int outgap = 0;
    private double cutPercent = .5;
    private BufferedImage image;
    private boolean imageChanged = true;
    private int imageX = -1;
    private int playerPos = -1, inPoint = -1, outPoint = -1;
    private String message = "";
    private long inSelect = -1, outSelect = -1;
    private long inSnapshot = -1, outSnapshot = -1;
    private Cursor cutCursor = null;
    private boolean showNoteLength = false;
    private boolean showNoteHeight = true;
    private Rectangle clip = new Rectangle();
    private boolean refreshing = false;
    private String equalsDigits = "";
    private boolean versionTextPainted = true;
    private Vector<Long> tmpNotes = new Vector<>(1024);
    private Dimension dim = new Dimension(1000, 100);
    private Graphics2D pgb = null;
    private int ppos = 0;
    private Point psheetpos = null;
    private boolean pisinterrupted = false;
    private BufferedImage videoFrame = null;
    private YassSession session = null;

    /**
     * Constructor for the YassSheet object
     */
    public YassSheet() {
        super(false);
        setFocusable(true);

        Image image = new ImageIcon(this.getClass()
                .getResource("/yass/resources/img/cut.gif")).getImage();
        cutCursor = Toolkit.getDefaultToolkit().createCustomCursor(image,
                new Point(0, 10), "cut");

        removeAll();

        BufferedImage bi = new BufferedImage(4, 4, BufferedImage.TYPE_INT_RGB);
        Graphics2D big = bi.createGraphics();
        big.setColor(dkGray);
        big.fillRect(0, 0, 4, 2);
        big.setColor(hiGray2);
        big.fillRect(0, 2, 4, 2);
        Rectangle rec = new Rectangle(0, 0, 4, 4);
        tex = new TexturePaint(bi, rec);

        int w = 16;
        int w2 = w / 2;
        BufferedImage im = new BufferedImage(w, w, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = im.createGraphics();
        g.setColor(white);
        g.fillRect(0, 0, w, w);
        Color panelbg = new JPanel().getBackground();
        g.setColor(panelbg);
        g.fillRect(0, 0, w2, w2);
        g.fillRect(w2, w2, w2, w2);
        bgtex = new TexturePaint(im, new Rectangle(w, w));

        Action keepfocus = new AbstractAction("Keep Focus") {
            private static final long serialVersionUID = 8988045810847674634L;

            public void actionPerformed(ActionEvent e) {
            }
        };
        getInputMap(JComponent.WHEN_FOCUSED).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ALT, 0, true), "keepfocus");
        getInputMap(JComponent.WHEN_FOCUSED).put(
                KeyStroke.getKeyStroke(0, Event.ALT_MASK, true), "keepfocus");
        getActionMap().put("keepfocus", keepfocus);

        addKeyListener(new KeyListener() {
            private long lastDigitMillis;
            private int lastDigit;

            public void keyTyped(KeyEvent e) {
                if (equalsKeyMillis > 0) {
                    e.consume();
                    return;
                }
                dispatch();
            }

            public void keyPressed(KeyEvent e) {
                char c = e.getKeyChar();
                int code = e.getKeyCode();

                if (equalsKeyMillis > 0) {
                    if (code == KeyEvent.VK_BACK_SPACE) {
                        equalsDigits = equalsDigits.substring(0,
                                equalsDigits.length() - 1);
                        repaint();
                    } else if (code == KeyEvent.VK_ESCAPE) {
                        equalsDigits = "";
                        equalsKeyMillis = 0;
                        repaint();
                    } else if (code == KeyEvent.VK_ENTER) {
                        if (equalsDigits.length() > 0)
                            setCurrentLineTo(Integer.valueOf(equalsDigits)
                                    .intValue());
                        equalsDigits = "";
                        equalsKeyMillis = 0;
                        repaint();
                    } else if (c >= '0' && c <= '9') {
                        equalsDigits = equalsDigits + c;
                        repaint();
                    }
                    e.consume();
                    return;
                }

                if (e.isControlDown() && e.isAltDown()
                        && c == KeyEvent.CHAR_UNDEFINED) {
                    hiliteAction = ACTION_CONTROL_ALT;
                    repaint();
                } else if (e.isControlDown() && c == KeyEvent.CHAR_UNDEFINED) {
                    hiliteAction = ACTION_CONTROL;
                    repaint();
                } else if (e.isAltDown() && c == KeyEvent.CHAR_UNDEFINED) {
                    hiliteAction = ACTION_ALT;
                    repaint();
                } else if (e.isShiftDown() && c == KeyEvent.CHAR_UNDEFINED) {
                    hiliteAction = ACTION_CONTROL_ALT;
                    repaint();
                }

                // 0=next_note, 1=prev_note, 2=page_down, 3=page_up
                if (code == keycodes[0] && !e.isControlDown() && !e.isAltDown()) {
                    table.nextBeat(false);
                    e.consume();
                    return;
                }
                if (code == keycodes[1] && !e.isControlDown() && !e.isAltDown()) {
                    table.prevBeat(false);
                    e.consume();
                    return;
                }
                if (code == keycodes[2] && !e.isControlDown() && !e.isAltDown()) {
                    table.gotoPage(1);
                    e.consume();
                    return;
                }
                if (code == keycodes[3] && !e.isControlDown() && !e.isAltDown()) {
                    table.gotoPage(-1);
                    e.consume();
                    return;
                }
                // 12=play, 13=play_page
                if (code == keycodes[12] && !e.isControlDown()
                        && !e.isAltDown()) {
                    if (e.isShiftDown()) {
                        firePropertyChange("play", null, "page");
                    } else {
                        firePropertyChange("play", null, "start");
                    }
                    e.consume();
                    return;
                }
                if (code == keycodes[13] && !e.isControlDown()
                        && !e.isAltDown()) {
                    Integer mode = new Integer(e.isShiftDown() ? 1 : 0);
                    firePropertyChange("play", mode, "page");
                    e.consume();
                    return;
                }

                // 4=init, 5=init_next, 6=right, 7=left, 8=up, 9=down,
                // 10=lengthen, 11=shorten, 12=play, 13=play_page,
                // 14=scroll_left, 15=scroll_right, 16=one_page
                if (code == keycodes[14] && !e.isControlDown()
                        && !e.isAltDown()) {
                    if (YassTable.getZoomMode() == YassTable.ZOOM_ONE) {
                        YassTable.setZoomMode(YassTable.ZOOM_MULTI);
                        enablePan(false);
                        actions.revalidateLyricsArea();
                        update();
                    }
                    int off = 10;
                    if (e.isShiftDown()) {
                        off = 50;
                    }
                    Point vp = getViewPosition();
                    vp.x = vp.x - off;
                    if (vp.x < 0) {
                        vp.x = 0;
                    }
                    setViewPosition(vp);

                    if (playerPos < vp.x || playerPos > vp.x + clip.width) {
                        int next = nextElement(vp.x);
                        if (next >= 0) {
                            YassRow row = table.getRowAt(next);
                            if (!row.isNote() && next + 1 < table.getRowCount()) {
                                next = next + 1;
                                row = table.getRowAt(next);
                            }

                            if (row.isNote()) {
                                table.setRowSelectionInterval(next, next);
                                table.updatePlayerPosition();
                            }
                        }
                    }

                    e.consume();
                    return;
                }
                if (code == keycodes[15] && !e.isControlDown()
                        && !e.isAltDown()) {
                    if (YassTable.getZoomMode() == YassTable.ZOOM_ONE) {
                        YassTable.setZoomMode(YassTable.ZOOM_MULTI);
                        enablePan(false);
                        actions.revalidateLyricsArea();
                        update();
                    }
                    int off = 10;
                    if (e.isShiftDown()) {
                        off = 50;
                    }
                    Point vp = getViewPosition();
                    vp.x = vp.x + off;
                    if (vp.x < 0) {
                        vp.x = 0;
                    }
                    setViewPosition(vp);

                    if (playerPos < vp.x || playerPos > vp.x + clip.width) {
                        int next = nextElement(vp.x);
                        if (next >= 0) {
                            YassRow row = table.getRowAt(next);
                            if (!row.isNote() && next + 1 < table.getRowCount()) {
                                next = next + 1;
                                row = table.getRowAt(next);
                            }

                            if (row.isNote()) {
                                table.setRowSelectionInterval(next, next);
                                table.updatePlayerPosition();
                            }
                        }
                    }

                    e.consume();
                    return;
                }

                if (code == keycodes[16] && !e.isControlDown()
                        && !e.isAltDown()) {
                    firePropertyChange("one", null, null);
                    e.consume();
                    return;
                }

                // 4=init, 5=init_next, 6=right, 7=left, 8=up, 9=down,
                // 10=lengthen, 11=shorten
                if ((code == keycodes[10] || code == keycodes[11])
                        && !e.isControlDown() && !e.isAltDown()) {
                    boolean lengthen = code == keycodes[10];
                    boolean changed = false;
                    int rows[] = table.getSelectedRows();
                    for (int next : rows) {
                        YassRow row = table.getRowAt(next);
                        if (!row.isNote()) {
                            continue;
                        }

                        int length = row.getLengthInt();
                        row.setLength(lengthen ? length + 1 : Math.max(1,
                                length - 1));
                        changed = true;
                    }
                    table.zoomPage();
                    table.updatePlayerPosition();
                    if (changed) {
                        table.addUndo();
                    }

                    e.consume();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            update();
                            repaint();
                            firePropertyChange("play", null, "start");
                        }
                    });
                    return;
                }

                if ((code == keycodes[6] || code == keycodes[7])
                        && !e.isControlDown() && !e.isAltDown()) {
                    boolean right = code == keycodes[6];
                    boolean changed = false;
                    int rows[] = table.getSelectedRows();
                    for (int next : rows) {
                        YassRow row = table.getRowAt(next);
                        if (!row.isNote()) {
                            continue;
                        }

                        int beat = row.getBeatInt();
                        row.setBeat(right ? beat + 1 : beat - 1);
                        changed = true;
                    }
                    table.updatePlayerPosition();
                    if (changed) {
                        table.addUndo();
                    }

                    e.consume();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            update();
                            repaint();
                            firePropertyChange("play", null, "start");
                        }
                    });
                    return;
                }

                // 4=init, 5=init_next, 6=right, 7=left, 8=up, 9=down,
                // 10=lengthen, 11=shorten
                if ((code == keycodes[8] || code == keycodes[9])
                        && !e.isControlDown() && !e.isAltDown()) {
                    boolean up = code == keycodes[8];
                    boolean changed = false;
                    int rows[] = table.getSelectedRows();
                    for (int next : rows) {
                        YassRow row = table.getRowAt(next);
                        if (!row.isNote()) {
                            continue;
                        }

                        int height = row.getHeightInt();
                        row.setHeight(up ? height + 1 : height - 1);
                        changed = true;
                    }
                    table.updatePlayerPosition();
                    if (changed) {
                        table.addUndo();
                    }

                    e.consume();
                    SwingUtilities.invokeLater(new Thread() {
                        public void run() {
                            update();
                            repaint();
                            firePropertyChange("play", new Integer(2), "page");
                        }
                    });
                    return;
                }

                if (Character.isDigit(c) && e.isAltDown() && !e.isControlDown()) {
                    String cstr = c + "";
                    long currentTime = System.currentTimeMillis();
                    if (currentTime < lastTime + 700) {
                        if (lastTimeString.length() < 3) {
                            cstr = lastTimeString + cstr;
                        }
                        lastTimeString = cstr;
                        try {
                            int n = Integer.parseInt(cstr);
                            table.gotoPageNumber(n);
                        } catch (Exception ignored) {
                        }
                    } else {
                        lastTimeString = cstr;
                        try {
                            int n = Integer.parseInt(cstr);
                            table.gotoPageNumber(n);
                        } catch (Exception ignored) {
                        }
                    }
                    lastTime = currentTime;
                    e.consume();
                    return;
                }

                // 4=init, 5=init_next, 6=right, 7=left, 8=up, 9=down,
                // 10=lengthen, 11=shorten, 12=play, 13=play_page,
                // 14=scroll_left, 15=scroll_right, 16=one_page
                if ((code == keycodes[4] || code == keycodes[5])
                        && !e.isControlDown() && !e.isAltDown()) {
                    boolean initCurrent = code == keycodes[4];
                    boolean changed = false;

                    if (initCurrent) {
                        int rows[] = table.getSelectedRows();
                        for (int next : rows) {
                            YassRow row = table.getRowAt(next);
                            if (!row.isNote()) {
                                continue;
                            }

                            YassRow row2 = table.getRowAt(next - 1);
                            if (!row2.isNote()) {
                                continue;
                            }
                            int beat = row2.getBeatInt();
                            int len = row2.getLengthInt();
                            row.setBeat(beat + len + 1);
                            row.setLength(1);
                            changed = true;
                        }
                        table.updatePlayerPosition();
                    } else {
                        int next = table.getSelectionModel()
                                .getMaxSelectionIndex();
                        YassRow row = table.getRowAt(next);
                        if (!row.isNote()) {
                            e.consume();
                            return;
                        }

                        int beat = row.getBeatInt();
                        int len = row.getLengthInt();
                        if (next + 1 >= table.getRowCount()) {
                            e.consume();
                            return;
                        }
                        YassRow row2 = table.getRowAt(next + 1);
                        if (!row2.isNote()) {
                            e.consume();
                            return;
                        }
                        row2.setBeat(beat + len + 1);
                        row2.setLength(1);
                        table.setRowSelectionInterval(next + 1, next + 1);
                        changed = true;
                        table.updatePlayerPosition();
                    }

                    if (changed) {
                        table.addUndo();
                    }

                    e.consume();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            update();
                            repaint();
                            firePropertyChange("play", null, "start");
                        }
                    });
                    return;
                }

                if (Character.isDigit(c) && !e.isControlDown()) {
                    boolean changed = false;
                    String cstr = c + "";
                    int n = -1;
                    try {
                        n = Integer.parseInt(cstr);
                    } catch (Exception ignored) {
                    }

                    long currentMillis = System.currentTimeMillis();
                    if (currentMillis - lastDigitMillis < 500)
                        n = lastDigit * 10 + n;
                    lastDigitMillis = System.currentTimeMillis();
                    lastDigit = n;

                    int rows[] = table.getSelectedRows();
                    for (int next : rows) {
                        YassRow row = table.getRowAt(next);
                        if (!row.isNote()) {
                            continue;
                        }
                        row.setLength(n);
                        changed = true;
                    }

                    table.updatePlayerPosition();
                    if (changed) {
                        table.addUndo();
                    }

                    e.consume();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            update();
                            repaint();
                            firePropertyChange("play", null, "start");
                        }
                    });
                    return;
                }
                dispatch();
            }

            public void keyReleased(KeyEvent e) {
                e.getKeyChar();
                if (!e.isControlDown() && !e.isAltDown() && !e.isShiftDown()) {
                    hiliteAction = ACTION_NONE;
                    repaint();
                } else if (!e.isControlDown() && e.isAltDown()) {
                    hiliteAction = ACTION_ALT;
                    repaint();
                } else if (e.isControlDown() && !e.isAltDown()) {
                    hiliteAction = ACTION_CONTROL;
                    repaint();
                } else if (e.isShiftDown()) {
                    hiliteAction = ACTION_CONTROL_ALT;
                    repaint();
                }

                if (!isPlaying) {
                    table.setPreventAutoCheck(false);
                    if (actions != null) {
                        actions.checkData(table, false, true);
                        actions.showMessage(0);
                    }
                }
                dispatch();
            }

            private void dispatch() {
                // if (table != null)
                // table.dispatchEvent(e);
            }
        });
        addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                hiliteAction = ACTION_NONE;

                if (table == null) {
                    return;
                }
                if (temporaryZoomOff) {
                    temporaryZoomOff = false;
                    YassTable.setZoomMode(YassTable.ZOOM_ONE);
                    table.zoomPage();
                }

                if (table.getPreventUndo()) {
                    table.setPreventUndo(false);
                    table.addUndo();
                }
                if (actions != null) {
                    actions.showMessage(0);
                }

				/*
                 * if (table.getPreventZoom()) { table.setPreventZoom(false);
				 * table.zoomPage(); }
				 */
                if (hiliteCue == MOVE_REMAINDER) {
                    hiliteCue = UNDEFINED;
                }

                if (hiliteCue == PREV_PAGE_PRESSED) {
                    firePropertyChange("page", null, new Integer(-1));
                    hiliteCue = UNDEFINED;
                }
                if (hiliteCue == NEXT_PAGE_PRESSED) {
                    firePropertyChange("page", null, new Integer(+1));
                    hiliteCue = UNDEFINED;
                }
                if (hiliteCue == PLAY_NOTE_PRESSED) {
                    firePropertyChange("play", null, "start");
                    hiliteCue = UNDEFINED;
                }
                if (hiliteCue == PLAY_PAGE_PRESSED) {
                    firePropertyChange("play", null, "page");
                    hiliteCue = UNDEFINED;
                }

                if (sketchStarted()) {
                    // firePropertyChange("play", null, "stop");
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            int ok = executeSketch();
                            cancelSketch();
                            if (useSketchingPlayback) {
                                if (ok == 2) {
                                    firePropertyChange("play", null, "start");
                                } else if (ok == 3) {
                                    int i = table.getSelectionModel()
                                            .getMinSelectionIndex();
                                    if (i >= 0) {
                                        YassRow r = table.getRowAt(i);
                                        if (r.isNote()) {
                                            int h = r.getHeightInt();
                                            firePropertyChange("midi", null,
                                                    new Integer(h));
                                            firePropertyChange("play", null,
                                                    "start");
                                        }
                                    }
                                }
                            }
                            repaint();
                        }
                    });

                } else {
                    repaint();
                }
                selectX = selectY = -1;
                select.x = select.y = select.width = select.height = 0;
                inPoint = outPoint = -1;
            }

            public void mouseClicked(MouseEvent e) {
                if (isPlaying() || isTemporaryStop()) {
                    firePropertyChange("play", null, "stop");
                    e.consume();
                    return;
                }

                int x = e.getX();
                int y = e.getY();

                if (x > playerPos - 32 && x < playerPos + 48
                        && y > TOP_PLAYER_BUTTONS
                        && y < TOP_PLAYER_BUTTONS + 64) {
                    // PLAY_NOTE_PRESSED or PLAY_PAGE_PRESSED
                    return;
                }

                boolean left = SwingUtilities.isLeftMouseButton(e);
                boolean twice = e.getClickCount() > 1;
                boolean one = e.getClickCount() == 1;

                // LYRICS POSITION
                boolean notInLyrics = true;
                if (layout.equals("East")) {
                    notInLyrics = (x - getViewPosition().x) < clip.width
                            - lyricsWidth;
                } else if (layout.equals("West")) {
                    notInLyrics = (x - getViewPosition().x) > lyricsWidth;
                }

                if (y > clip.height - BOTTOM_BORDER + 20
                        || (y > 20 && y < TOP_LINE - 10 && notInLyrics)) {
                    if (!left || twice || one) {
                        firePropertyChange("one", null, null);
                        return;
                    }
                }

                if (!twice) {
                    if (!paintHeights) {
                        return;
                    }
                    if (e.getX() > clip.x + heightBoxWidth) {
                        return;
                    }
                    if (hiliteHeight > 200) {
                        return;
                    }
                    firePropertyChange("midi", null, new Integer(
                            pan ? (hiliteHeight - 2) : hiliteHeight));
                    return;
                }
                table.selectLine();
            }

            public void mousePressed(MouseEvent e) {

                if (equalsKeyMillis > 0) {
                    equalsKeyMillis = 0;
                    equalsDigits = "";
                }

                boolean left = SwingUtilities.isLeftMouseButton(e);

                if (table == null) {
                    return;
                }
                if (YassTable.getZoomMode() == YassTable.ZOOM_ONE
                        && dragMode != SLIDE) {
                    temporaryZoomOff = true;
                    YassTable.setZoomMode(YassTable.ZOOM_MULTI);
                }

                if (! hasFocus()) {
                    requestFocusInWindow();
                    requestFocus();
                }

                if (isPlaying() || isTemporaryStop()) {
                    firePropertyChange("play", null, "stop");
                    e.consume();
                    return;
                }

                setErrorMessage("");

                int x = e.getX();
                int y = e.getY();

                if (paintHeights) {
                    if (x < clip.x + heightBoxWidth && y > TOP_LINE - 10
                            && (y < clip.height - BOTTOM_BORDER)) {
                        if (y < 0) {
                            y = 0;
                        }
                        if (y > dim.height) {
                            y = dim.height;
                        }

                        int dy;
                        if (pan) {
                            dy = (int) Math.round(hhPageMin
                                    + (dim.height - y - BOTTOM_BORDER) / hSize);
                        } else {
                            dy = (int) Math.round(minHeight
                                    + (dim.height - y - BOTTOM_BORDER) / hSize);
                        }
                        hiliteHeight = dy;
                        repaint();
                        return;
                    }
                }

                if (x < clip.x + LEFT_BORDER && y > dim.height - BOTTOM_BORDER) {
                    hiliteCue = PREV_PAGE_PRESSED;
                    repaint();
                    return;
                }
                if (x > clip.x + clip.width - RIGHT_BORDER
                        && y > dim.height - BOTTOM_BORDER) {
                    hiliteCue = NEXT_PAGE_PRESSED;
                    repaint();
                    return;
                }

                if (x > playerPos - 32 && x < playerPos + 48
                        && y > TOP_PLAYER_BUTTONS
                        && y < TOP_PLAYER_BUTTONS + 64) {
                    hiliteCue = x >= playerPos ? PLAY_NOTE_PRESSED
                            : PLAY_PAGE_PRESSED;
                    repaint();
                    return;
                }

                YassRectangle r;
                if (hiliteCue == CUT) {
                    r = rect.elementAt(hilite);
                    table.clearSelection();
                    table.addRowSelectionInterval(hilite, hilite);
                    firePropertyChange("split", null, new Double(
                            (e.getX() - r.x) / r.width));
                    hiliteCue = UNDEFINED;
                } else if (hiliteCue == JOIN_LEFT) {
                    r = rect.elementAt(hilite);
                    table.clearSelection();
                    table.addRowSelectionInterval(hilite, hilite);
                    firePropertyChange("joinLeft", null,
                            new Integer((int) (e.getX() - r.x)));
                    hiliteCue = UNDEFINED;
                } else if (hiliteCue == JOIN_RIGHT) {
                    r = rect.elementAt(hilite);
                    table.clearSelection();
                    table.addRowSelectionInterval(hilite, hilite);
                    firePropertyChange("joinRight", null,
                            new Integer((int) (e.getX() - r.x)));
                    hiliteCue = UNDEFINED;
                } else if (hiliteCue == SNAPSHOT) {
                    hiliteCue = UNDEFINED;
                    createSnapshot();
                    repaint();
                    return;
                } else if (hiliteCue == MOVE_REMAINDER) {
                    hit = nextElement();
                    if (hit < 0) {
                        return;
                    }
                    table.setRowSelectionInterval(hit, hit);
                    table.updatePlayerPosition();

                    dragMode = CENTER;
                    r = rect.elementAt(hit);
                    dragOffsetX = (int) (e.getX() - r.x);
                    dragOffsetY = (int) (e.getY() - r.y);
                    dragOffsetXRatio = dragOffsetX / wSize;
                    return;
                } else if (hiliteCue == SLIDE && left) {
                    YassTable t = getActiveTable();
                    if (t != null && t.getMultiSize() == 1) {
                        YassTable.setZoomMode(YassTable.ZOOM_MULTI);
                        enablePan(false);
                        actions.revalidateLyricsArea();
                        update();
                        repaint();
                    }

                    dragMode = SLIDE;
                    slideX = e.getX();
                    return;
                }
                YassRectangle next = null;

                rect.size();
                hit = -1;
                selectX = selectY = -1;
                dragDir = UNDEFINED;
                dragOffsetX = dragOffsetY = 0;

                int i = 0;
                for (Enumeration<?> en = rect.elements(); en.hasMoreElements(); i++) {
                    if (next != null) {
                        r = next;
                        next = en.hasMoreElements() ? (YassRectangle) en
                                .nextElement() : null;
                    } else {
                        r = (YassRectangle) en.nextElement();
                    }
                    if (next == null) {
                        next = en.hasMoreElements() ? (YassRectangle) en
                                .nextElement() : null;
                    }

                    if (r == null) {
                        break;
                    }

                    if (r.isPageBreak()) {
                        if (x > r.x - 5 && x < r.x + 5) {
                            hit = i;
                            dragOffsetX = (int) (e.getX() - r.x);
                            dragOffsetY = (int) (e.getY() - r.y);
                            dragOffsetXRatio = dragOffsetX / wSize;
                            dragMode = hiliteCue;

                            if (!table.isRowSelected(i)) {
                                if (e.isControlDown()) {
                                    table.addRowSelectionInterval(i, i);
                                } else {
                                    table.setRowSelectionInterval(i, i);
                                }
                            }
                            table.scrollRectToVisible(table.getCellRect(i, 0,
                                    true));
                            repaint();
                            break;
                        }
                    } else if (r.contains(e.getPoint())) {
                        // hiliteAction = ACTION_CONTROL_ALT;

                        hit = i;
                        dragOffsetX = (int) (e.getX() - r.x);
                        dragOffsetY = (int) (e.getY() - r.y);
                        dragOffsetXRatio = dragOffsetX / wSize;
                        dragMode = hiliteCue;

                        if (!table.isRowSelected(i)) {
                            if (e.isShiftDown() || e.isControlDown()) {
                                table.addRowSelectionInterval(i, i);
                            } else {
                                table.setRowSelectionInterval(i, i);
                            }
                        }
                        table.scrollRectToVisible(table.getCellRect(i, 0, true));
                        table.updatePlayerPosition();

                        inPoint = outPoint = playerPos;
                        inSelect = fromTimeline(inPoint);
                        outSelect = fromTimeline(outPoint);

                        if (r.hasType(YassRectangle.GAP)) {
                            temporaryZoomOff = false;
                        }
                        repaint();
                        break;
                    } else if (table.getMultiSize() > 1
                            && r.x < x
                            && (((next == null || next.isPageBreak() || next
                            .hasType(YassRectangle.END)) && x < r.x
                            + r.width) || (next != null
                            && (!next.isPageBreak() && !next
                            .hasType(YassRectangle.END)) && x < next.x))
                            && y > clip.height - BOTTOM_BORDER
                            && y < clip.height - BOTTOM_BORDER + 16) {
                        hiliteAction = ACTION_CONTROL_ALT;

                        hit = i;
                        dragOffsetX = (int) (e.getX() - r.x);
                        dragOffsetY = (int) (e.getY() - r.y);
                        dragOffsetXRatio = dragOffsetX / wSize;
                        dragMode = hiliteCue;

                        table.setRowSelectionInterval(i, i);
                        table.selectLine();
                        table.updatePlayerPosition();

                        inPoint = outPoint = playerPos;
                        inSelect = fromTimeline(inPoint);
                        outSelect = fromTimeline(outPoint);

                        repaint();
                        break;
                    }
                }
                if (hit < 0) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        inPoint = outPoint = e.getX();
                        inSelect = fromTimeline(inPoint);
                        outSelect = fromTimeline(outPoint);

                        if (useSketching) {
                            startSketch();
                            addSketch(e.getX(), e.getY());
                            repaint();
                            return;
                        }

                        boolean any = false;

                        int k = 0;
                        for (Enumeration<?> en = rect.elements(); en
                                .hasMoreElements(); k++) {
                            r = (YassRectangle) en.nextElement();
                            if (r.x <= e.getX() && e.getX() <= r.x + r.width) {
                                if (!any) {
                                    if (!(e.isShiftDown() || e.isControlDown())) {
                                        table.clearSelection();
                                    }
                                }
                                if (!table.isRowSelected(k)) {
                                    table.addRowSelectionInterval(k, k);
                                }
                                any = true;
                            }
                        }
                        if (any) {
                            table.updatePlayerPosition();
                        } else {
                            table.clearSelection();
                            playerPos = Math.min(inPoint, outPoint);
                            table.updatePlayerPosition();
                        }
                    } else {
                        inPoint = outPoint = -1;
                        inSelect = outSelect = -1;

                        Point p = (Point) e.getPoint().clone();
                        SwingUtilities.convertPointToScreen(p, YassSheet.this);
                        selectX = p.getX();
                        selectY = p.getY();
                        select.x = select.y = select.width = select.height = 0;
                    }
                    repaint();
                }
            }

            public void mouseEntered(MouseEvent e) {
                // System.out.println("sheet entered");
            }

            public void mouseExited(MouseEvent e) {
                hilite = -1;
                hiliteHeight = 1000;
                repaint();
            }

        });
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {

                if (equalsKeyMillis > 0) {
                    equalsKeyMillis = 0;
                    equalsDigits = "";
                }

                // System.out.println(e.getX());
                if (isPlaying()) {
                    return;
                }

                if (rect == null) {
                    return;
                }

                rect.size();
                int x = e.getX();
                int y = e.getY();
                if (hilite >= 0) {
                    hilite = -2;
                } else {
                    hilite = -1;
                }
                if (hiliteCue != UNDEFINED) {
                    hilite = -2;
                }
                hiliteCue = UNDEFINED;
                hiliteAction = ACTION_NONE;

                boolean shouldRepaint = false;

                if (paintHeights) {
                    if (x < clip.x + heightBoxWidth && y > TOP_LINE - 10
                            && (y < clip.height - BOTTOM_BORDER)) {
                        setCursor(Cursor
                                .getPredefinedCursor(Cursor.HAND_CURSOR));
                        if (hiliteHeight < 1000) {
                            if (y < 0) {
                                y = 0;
                            }
                            if (y > dim.height) {
                                y = dim.height;
                            }

                            int dy;
                            if (pan) {
                                dy = (int) Math.round(hhPageMin
                                        + (dim.height - y - BOTTOM_BORDER)
                                        / hSize);
                            } else {
                                dy = (int) Math.round(minHeight
                                        + (dim.height - y - BOTTOM_BORDER)
                                        / hSize);
                            }

                            if (hiliteHeight != dy) {
                                hiliteHeight = dy;
                                repaint();
                            }
                        }
                        return;
                    } else {
                        if (hiliteHeight != 1000) {
                            shouldRepaint = true;
                            hiliteHeight = 1000;
                        }
                    }
                }

                if (x < clip.x + LEFT_BORDER && y > clip.height - BOTTOM_BORDER) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    hiliteCue = PREV_PAGE;
                    repaint();
                    return;
                }

                int right = clip.x + clip.width - RIGHT_BORDER;
                if (x > right && y > clip.height - BOTTOM_BORDER) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    hiliteCue = NEXT_PAGE;
                    repaint();
                    return;
                }

                if (x > playerPos - 32 && x < playerPos + 48
                        && y > TOP_PLAYER_BUTTONS
                        && y < TOP_PLAYER_BUTTONS + 64) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    hiliteCue = x >= playerPos ? PLAY_NOTE : PLAY_PAGE;
                    repaint();
                    return;
                }

                if (inSelect >= 0 && inSelect != outSelect && y < TOP_BORDER
                        && x >= toTimeline(Math.min(inSelect, outSelect))
                        && x <= toTimeline(Math.max(inSelect, outSelect))) {
                    hiliteCue = SNAPSHOT;
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    repaint();
                    return;
                }

                // LYRICS POSITION
                boolean notInLyrics = true;
                if (layout.equals("East")) {
                    notInLyrics = (x - getViewPosition().x) < clip.width
                            - lyricsWidth;
                } else if (layout.equals("West")) {
                    notInLyrics = (x - getViewPosition().x) > lyricsWidth;
                }

                if (!notInLyrics && getComponentCount() > 0 && lyricsVisible) {
                    // dirty bugfix for lost bounds
                    YassLyrics lyrics = (YassLyrics) getComponent(0);
                    Point p2 = lyrics.getLocation();
                    if ((layout.equals("East") && (x - p2.x > 500))
                            || (layout.equals("West") && x < lyricsWidth)) {
                        Point p = ((JViewport) getParent()).getViewPosition();
                        Dimension vr = ((JViewport) getParent())
                                .getExtentSize();

                        int newx = (int) p.getX() + vr.width - lyricsWidth;
                        if (layout.equals("East")) {
                            newx = (int) p.getX() + vr.width - lyricsWidth;
                        } else if (layout.equals("West")) {
                            newx = (int) p.getX();
                        }

                        int newy = (int) p.getY() + 20;
                        if (p2.x != newx || p2.y != newy) {
                            lyrics.setLocation(newx, newy);
                            revalidate();
                            update();
                        }
                        // System.out.println("validate: x=" + x + "  lyrics=" +
                        // p2);
                        repaint();
                    }
                }

                YassRectangle next = null;
                YassRectangle r;

                int i = 0;
                for (Enumeration<?> en = rect.elements(); en.hasMoreElements(); i++) {
                    if (next != null) {
                        r = next;
                        next = en.hasMoreElements() ? (YassRectangle) en
                                .nextElement() : null;
                    } else {
                        r = (YassRectangle) en.nextElement();
                    }
                    if (next == null) {
                        next = en.hasMoreElements() ? (YassRectangle) en
                                .nextElement() : null;
                    }

                    if (r != null) {
                        boolean isNote = !r.isType(YassRectangle.GAP)
                                && !r.isType(YassRectangle.START)
                                && !r.isType(YassRectangle.END);

                        if (r.isPageBreak()) {
                            if (x > r.x - 5 && x < r.x + 5 && !autoTrim) {
                                hiliteCue = CENTER;
                                setCursor(Cursor
                                        .getPredefinedCursor(Cursor.MOVE_CURSOR));
                                repaint();
                                return;
                            }
                        } else if (r.contains(x, y)) {
                            hilite = i;

                            if (mouseover) {
                                if (!table.isRowSelected(i)) {
                                    if (!(e.isShiftDown() || e.isControlDown())) {
                                        table.clearSelection();
                                    }
                                    table.addRowSelectionInterval(i, i);
                                    table.updatePlayerPosition();
                                }
                            }

                            int dragw = r.width > Math.max(wSize, 32) * 3 ? (int) Math
                                    .max(wSize, 32) : (r.width > 72 ? 24
                                    : (r.width > 48 ? 16 : 5));
                            if (Math.abs(r.x - x) < dragw && r.width > 20) {
                                hiliteCue = LEFT;
                                hiliteAction = ACTION_CONTROL;
                            } else if (Math.abs(r.x + r.width - x) < dragw
                                    && r.width > 20) {
                                hiliteCue = RIGHT;
                                hiliteAction = ACTION_ALT;
                            } else {
                                hiliteCue = CENTER;
                                hiliteAction = ACTION_CONTROL_ALT;
                            }

                            if (hiliteCue == CENTER) {
                                setCursor(Cursor
                                        .getPredefinedCursor(Cursor.MOVE_CURSOR));
                            } else {
                                setCursor(Cursor
                                        .getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                            }
                            repaint();
                            return;
                        } else if (table.getMultiSize() > 1
                                && r.x < x
                                && (((next == null || next.isPageBreak() || next
                                .hasType(YassRectangle.END)) && x < r.x
                                + r.width) || (next != null
                                && (!next.isPageBreak() && !next
                                .hasType(YassRectangle.END)) && x < next.x))
                                && y > clip.height - BOTTOM_BORDER
                                && y < clip.height - BOTTOM_BORDER + 16) {
                            hiliteCue = CENTER;
                            setCursor(Cursor
                                    .getPredefinedCursor(Cursor.MOVE_CURSOR));
                            repaint();
                            return;
                        } else if (isNote && r.x + wSize / 2 < x
                                && x < r.x + r.width - wSize / 2
                                && Math.abs(r.y - y) < hSize && r.width > 5) {
                            hilite = i;
                            hiliteCue = CUT;
                            cutPercent = (x - r.x) / r.width;
                            setCursor(cutCursor);
                            repaint();
                            return;
                        } else if (isNote && r.x < x && x < r.x + wSize / 2
                                && r.width > 5) {
                            if (Math.abs(r.y - y) < hSize) {
                                hilite = i;
                                hiliteCue = JOIN_LEFT;
                                setCursor(Cursor
                                        .getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                                repaint();
                                return;
                            }
                        } else if (isNote && r.x + r.width - wSize / 2 < x
                                && x < r.x + r.width && r.width > 5) {
                            if (Math.abs(r.y - y) < hSize) {
                                hilite = i;
                                hiliteCue = JOIN_RIGHT;
                                setCursor(Cursor
                                        .getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                                repaint();
                                return;
                            }
                        }
                    }
                }

                if (y > clip.height - BOTTOM_BORDER + 20
                        || (y > 20 && y < TOP_LINE - 10 && notInLyrics)) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    hiliteCue = SLIDE;
                    repaint();
                    return;
                }

                if (x > playerPos - 10 && x < playerPos && y > TOP_LINE
                        && y < dim.height - BOTTOM_BORDER) {
                    hiliteCue = MOVE_REMAINDER;
                    setCursor(Cursor
                            .getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                    repaint();
                    return;
                }

                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                if (hilite == -2) {
                    hilite = -1;
                    repaint();
                    return;
                }

                if (shouldRepaint) {
                    repaint();
                }
            }

            public void mouseDragged(MouseEvent e) {
                boolean left = SwingUtilities.isLeftMouseButton(e);

                Point p = e.getPoint();
                int px = Math.max(clip.x, Math.min(p.x, clip.x + clip.width));
                int py = p.y;

                if (rect == null) {
                    return;
                }

                if (hiliteCue == PREV_PAGE_PRESSED
                        && !(px < clip.x + LEFT_BORDER && py > dim.height
                        - BOTTOM_BORDER)) {
                    hiliteCue = PREV_PAGE;
                    repaint();
                    return;
                }
                if (hiliteCue == PREV_PAGE
                        && (px < clip.x + LEFT_BORDER && py > dim.height
                        - BOTTOM_BORDER)) {
                    hiliteCue = PREV_PAGE_PRESSED;
                    repaint();
                    return;
                }
                if (hiliteCue == NEXT_PAGE_PRESSED
                        && !(px > clip.x + clip.width - RIGHT_BORDER && py > dim.height
                        - BOTTOM_BORDER)) {
                    hiliteCue = NEXT_PAGE;
                    repaint();
                    return;
                }
                if (hiliteCue == NEXT_PAGE
                        && (px > clip.x + clip.width - RIGHT_BORDER && py > dim.height
                        - BOTTOM_BORDER)) {
                    hiliteCue = NEXT_PAGE_PRESSED;
                    repaint();
                    return;
                }
                if (hiliteCue == PLAY_NOTE_PRESSED
                        && !(px >= playerPos && px < playerPos + 48
                        && py > TOP_PLAYER_BUTTONS && py < TOP_PLAYER_BUTTONS + 64)) {
                    hiliteCue = PLAY_NOTE;
                    repaint();
                    return;
                }
                if (hiliteCue == PLAY_NOTE
                        && (px >= playerPos && px < playerPos + 48
                        && py > TOP_PLAYER_BUTTONS && py < TOP_PLAYER_BUTTONS + 64)) {
                    hiliteCue = PLAY_NOTE_PRESSED;
                    repaint();
                    return;
                }
                if (hiliteCue == PLAY_PAGE_PRESSED
                        && !(px > playerPos - 32 && px < playerPos
                        && py > TOP_PLAYER_BUTTONS && py < TOP_PLAYER_BUTTONS + 64)) {
                    hiliteCue = PLAY_PAGE;
                    repaint();
                    return;
                }
                if (hiliteCue == PLAY_PAGE
                        && (px > playerPos - 32 && px < playerPos
                        && py > TOP_PLAYER_BUTTONS && py < TOP_PLAYER_BUTTONS + 64)) {
                    hiliteCue = PLAY_PAGE_PRESSED;
                    repaint();
                    return;
                }
                if (hiliteCue == NEXT_PAGE || hiliteCue == PREV_PAGE
                        || hiliteCue == PLAY_NOTE || hiliteCue == PLAY_PAGE
                        || hiliteCue == NEXT_PAGE_PRESSED
                        || hiliteCue == PREV_PAGE_PRESSED
                        || hiliteCue == PLAY_NOTE_PRESSED
                        || hiliteCue == PLAY_PAGE_PRESSED) {
                    return;
                }

                if (paintHeights) {
                    if (px < clip.x + heightBoxWidth && py > TOP_LINE - 10
                            && (py < clip.height - BOTTOM_BORDER)) {
                        if (py < 0) {
                            py = 0;
                        }
                        if (py > dim.height) {
                            py = dim.height;
                        }

                        int dy;
                        if (pan) {
                            dy = (int) Math
                                    .round(hhPageMin
                                            + (dim.height - py - BOTTOM_BORDER)
                                            / hSize);
                        } else {
                            dy = (int) Math
                                    .round(minHeight
                                            + (dim.height - py - BOTTOM_BORDER)
                                            / hSize);
                        }
                        hiliteHeight = dy;
                        repaint();

                        if (hiliteHeight > 200) {
                            return;
                        }

                        long time = System.currentTimeMillis();
                        if (time - lastMidiTime > 100) {
                            firePropertyChange("midi", null, new Integer(
                                    pan ? (hiliteHeight - 2) : hiliteHeight));
                            lastMidiTime = time;
                        }
                        return;
                    }
                }

                if (hiliteCue == SLIDE && left) {
                    Point vp = getViewPosition();
                    if (slideX == px) {
                        return;
                    }
                    int off = px - slideX;
                    int oldpoff = px - vp.x;

                    vp.x = vp.x - off;
                    if (vp.x < 0) {
                        vp.x = 0;
                    }
                    setViewPosition(vp);
                    slideX = vp.x + oldpoff;

                    if (playerPos < vp.x || playerPos > vp.x + clip.width) {
                        int next = nextElement(vp.x);
                        if (next >= 0) {
                            YassRow row = table.getRowAt(next);
                            if (!row.isNote() && next + 1 < table.getRowCount()) {
                                next = next + 1;
                                row = table.getRowAt(next);
                            }

                            if (row.isNote()) {
                                table.setRowSelectionInterval(next, next);
                                table.updatePlayerPosition();
                            }
                        }
                    }

                    return;
                }

                // if (paintHeights && e.getX() <= clip.x + heightBoxWidth)
                // return;

                int shiftRemainder = InputEvent.BUTTON1_DOWN_MASK
                        | InputEvent.SHIFT_DOWN_MASK
                        | InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK;
                if ((e.getModifiersEx() & shiftRemainder) == shiftRemainder) {
                    shiftRemainder = 1;
                } else {
                    shiftRemainder = 0;
                }
                if (hiliteCue == MOVE_REMAINDER) {
                    shiftRemainder = 1;
                }

                if (useSketching) {
                    if (sketchStarted()) {
                        addSketch(px, py);
                        if (!detectSketch()) {
                            cancelSketch();
                        } else {
                            repaint();
                            return;
                        }
                    }
                }

                if (hit < 0) {
                    if (left) {
                        // playerPos = px;
                        outPoint = px;
                        outSelect = fromTimeline(outPoint);

                        select.x = Math.min(inPoint, outPoint);
                        select.y = 0;
                        select.width = Math.abs(outPoint - inPoint);
                        select.height = clip.height;
                    } else {

                        Point p2 = new Point((int) selectX, (int) selectY);
                        SwingUtilities.convertPointFromScreen(p2,
                                YassSheet.this);

                        select.x = Math.min(p2.getX(), (double) px);
                        select.y = Math.min(p2.getY(), (double) py);
                        select.width = Math.abs(p2.getX() - (double) px);
                        select.height = Math.abs(p2.getY() - (double) py);
                    }

                    int n = rect.size();
                    YassRectangle r;
                    boolean any = false;
                    for (int i = 0; i < n; i++) {
                        r = rect.elementAt(i);
                        if (r.intersects(select)) {
                            if (!any) {
                                if (!(e.isShiftDown() || e.isControlDown())) {
                                    table.clearSelection();
                                }
                            }
                            if (!table.isRowSelected(i)) {
                                table.addRowSelectionInterval(i, i);
                            }
                            any = true;
                        }
                    }
                    if (any) {
                        table.updatePlayerPosition();
                    } else {
                        table.clearSelection();
                        playerPos = Math.min(inPoint, outPoint);
                        table.updatePlayerPosition();
                    }

                    repaint();
                    return;
                }

                long time = System.currentTimeMillis();
                if (time - lastDragTime < 60) {
                    return;
                }
                lastDragTime = time;

                table.setPreventUndo(true);

                YassRectangle rr = rect.elementAt(hit);

                table.getRowAt(hit);
                int pageMin = rr.getPageMin();

                int x;

                int dx;
                int y = py - dragOffsetY;
                if (y < 0) {
                    y = 0;
                }
                if (y > dim.height) {
                    y = dim.height;
                }

                if (y < hSize) {
                    y = (int) -hSize;
                }

                // updateFromRow: rr.y = dim.height - (height - minHeight) *
                // hSize - hSize-4;
                int dy;
                if (pan) {
                    dy = (int) Math.round(pageMin
                            + (dim.height - y - hSize - BOTTOM_BORDER + 1)
                            / hSize) - 2;
                } else {
                    dy = (int) Math.round(minHeight
                            + (dim.height - y - hSize - BOTTOM_BORDER + 1)
                            / hSize);
                }

                YassRow r = table.getRowAt(hit);

                if (rr.isType(YassRectangle.GAP)) {
                    x = (int) ((px - dragOffsetXRatio * wSize));
                    if (paintHeights) {
                        x -= heightBoxWidth;
                    }
                    double gapres = x / wSize;
                    double gap2 = gapres * 60 * 1000 / (4 * bpm);
                    gap2 = Math.round(gap2 / 10) * 10;
                    firePropertyChange("gap", null, new Integer((int) gap2));
                    return;
                }
                if (rr.isType(YassRectangle.START)) {
                    x = (int) ((px - dragOffsetXRatio * wSize));
                    if (paintHeights) {
                        x -= heightBoxWidth;
                    }
                    double valres = x / wSize;
                    double val = valres * 60 * 1000 / (4 * bpm);
                    val = Math.round(val / 10) * 10;
                    firePropertyChange("start", null, new Integer((int) val));
                    return;
                }
                if (rr.isType(YassRectangle.END)) {
                    x = (int) ((px - dragOffsetXRatio * wSize));
                    if (paintHeights) {
                        x -= heightBoxWidth;
                    }
                    double valres = x / wSize;
                    double val = valres * 60 * 1000 / (4 * bpm);
                    val = Math.round(val / 10) * 10;
                    table.clearSelection();
                    // quick hack
                    firePropertyChange("end", null, new Integer((int) val));
                    table.clearSelection();
                    return;
                }

                boolean isPageBreak = r.isPageBreak();
                if (!isPageBreak) {
                    int oldy = r.getHeightInt();
                    if (oldy != dy) {
                        if (dragDir != HORIZONTAL) {
                            dragDir = VERTICAL;
                            firePropertyChange("relHeight", null, new Integer(
                                    dy - oldy));
                            return;
                        }
                    }
                }
                boolean isPageBreakMin = false;
                if (isPageBreak) {
                    isPageBreakMin = r.getBeatInt() == r.getSecondBeatInt();
                }
                if (!isPageBreakMin && dragMode == RIGHT) {
                    x = (int) (px - beatgap * wSize - 2 + wSize / 2);
                    if (paintHeights) {
                        x -= heightBoxWidth;
                    }
                    dx = (int) Math.round(x / wSize);
                } else {
                    x = (int) (px - beatgap * wSize - 2 - dragOffsetXRatio
                            * wSize);
                    if (paintHeights) {
                        x -= heightBoxWidth;
                    }
                    dx = (int) Math.round(x / wSize);
                }

                if (isPageBreakMin || dragMode == CENTER) {
                    int oldx = r.getBeatInt();
                    if (oldx != dx) {
                        if (dragDir != VERTICAL) {
                            dragDir = HORIZONTAL;

                            if (shiftRemainder != 0) {
                                firePropertyChange("relBeatRemainder", null,
                                        new Integer(dx - oldx));
                            } else {
                                firePropertyChange("relBeat", null,
                                        new Integer(dx - oldx));
                            }
                        }
                    }
                } else if (dragMode == LEFT) {
                    int oldx = r.getBeatInt();
                    if (oldx != dx) {
                        if (dragDir != VERTICAL) {

                            dragDir = HORIZONTAL;
                            firePropertyChange("relLeft", null, new Integer(dx
                                    - oldx));
                        }
                    }
                } else {
                    // dragMode==RIGHT
                    int oldx = 0;
                    if (r.isNote()) {
                        oldx = r.getBeatInt() + r.getLengthInt();
                    } else if (r.isPageBreak()) {
                        oldx = r.getSecondBeatInt();
                    }
                    if (oldx != dx) {
                        if (dragDir != VERTICAL) {

                            dragDir = HORIZONTAL;
                            firePropertyChange("relRight", null, new Integer(dx
                                    - oldx));
                        }
                    }
                }
            }
        });
    }

    /**
     * Gets the keyCodes attribute of the YassSheet object
     *
     * @return The keyCodes value
     */
    public int[] getKeyCodes() {
        return keycodes;
    }

    /**
     * Sets the lyricsWidth attribute of the YassSheet object
     *
     * @param w The new lyricsWidth value
     */
    public void setLyricsWidth(int w) {
        lyricsWidth = w;
    }

    /**
     * Sets the lyricsVisible attribute of the YassSheet object
     *
     * @param onoff The new lyricsVisible value
     */
    public void setLyricsVisible(boolean onoff) {
        lyricsVisible = onoff;
    }

    /**
     * Sets the messageMemory attribute of the YassSheet object
     *
     * @param onoff The new messageMemory value
     */
    public void setDebugMemory(boolean onoff) {
        messageMemory = onoff;
    }

    /**
     * Sets the colorSets attribute of the YassSheet object
     *
     * @param c The new colorSets value
     */
    public void setColors(Color[] c) {
        System.arraycopy(c, 0, colorSet, 0, colorSet.length);
    }

    /**
     * Description of the Method
     *
     * @param onoff Description of the Parameter
     */
    public void shadeNotes(boolean onoff) {
        noshade = !onoff;
    }

    /**
     * Description of the Method
     *
     * @param onoff Description of the Parameter
     */
    public void setAutoTrim(boolean onoff) {
        autoTrim = onoff;
    }

    /**
     * Sets the layout attribute of the YassSheet object
     *
     * @param s The new layout value
     */
    public void setLyricsLayout(String s) {
        layout = s;
    }

    /**
     * Gets the topLine attribute of the YassSheet object
     *
     * @return The topLine value
     */
    public int getTopLine() {
        return TOP_LINE;
    }

    /**
     * Description of the Method
     *
     * @param onoff Description of the Parameter
     */
    public void showArrows(boolean onoff) {
        showArrows = onoff;
    }

    /**
     * Description of the Method
     *
     * @param onoff Description of the Parameter
     */
    public void showPlayerButtons(boolean onoff) {
        showPlayerButtons = onoff;
    }

    /**
     * Description of the Method
     *
     * @param onoff Description of the Parameter
     */
    public void showText(boolean onoff) {
        BOTTOM_BORDER = onoff ? 56 : 10;
        showText = onoff;
    }

    /**
     * Description of the Method
     *
     * @return Description of the Return Value
     */
    public boolean showVideo() {
        return showVideo;
    }

    /**
     * Description of the Method
     *
     * @return Description of the Return Value
     */
    public boolean showBackground() {
        return showBackground;
    }

    /**
     * Description of the Method
     *
     * @param onoff Description of the Parameter
     */
    public void showBackground(boolean onoff) {
        showBackground = onoff;
    }

    /**
     * Description of the Method
     *
     * @param onoff Description of the Parameter
     */
    public void showVideo(boolean onoff) {
        showVideo = onoff;
    }

    /**
     * Adds a feature to the Sketch attribute of the YassSheet object
     *
     * @param x The feature to be added to the Sketch attribute
     * @param y The feature to be added to the Sketch attribute
     */
    private void addSketch(int x, int y) {
        if (sketch == null) {
            sketch = new Point[SKETCH_LENGTH];
        }
        if (sketch[sketchPos] == null) {
            sketch[sketchPos] = new Point();
        }

        sketch[sketchPos].setLocation(x, y);
        if (sketchPos < sketch.length - 1) {
            sketchPos++;
        }
    }

    /**
     * Description of the Method
     */
    private void startSketch() {
        sketchPos = dirPos = 0;
        sketchStartTime = System.currentTimeMillis();
        sketchStarted = true;
    }

    /**
     * Description of the Method
     */
    private void cancelSketch() {
        sketchStarted = false;
    }

    /**
     * Description of the Method
     *
     * @return Description of the Return Value
     */
    private boolean sketchStarted() {
        return sketchStarted;
    }

    /**
     * Description of the Method
     *
     * @param onoff     Description of the Parameter
     * @param playonoff Description of the Parameter
     */
    public void enableSketching(boolean onoff, boolean playonoff) {
        useSketching = onoff;
        useSketchingPlayback = playonoff;
    }

    /**
     * Description of the Method
     *
     * @return Description of the Return Value
     */
    private boolean detectSketch() {
        if (sketchPos < 3) {
            return true;
        }

        long sketchEndTime = System.currentTimeMillis();

        long ms = sketchEndTime - sketchStartTime;
        boolean intime = ms < 300;
        if (!intime) {
            return false;
        }

        Rectangle r = new Rectangle(sketch[0]);
        for (int i = 1; i < sketchPos; i++) {
            r.add(sketch[i]);
        }

        boolean vertical = r.height < 36 && r.height > r.width && r.height > 2
                && r.width < 10;
        boolean horizontal = r.width < 36 && r.width > r.height && r.width > 2
                && r.height < 10;

        if (!horizontal && !vertical) {
            return false;
        }

        if (sketchDirs == null) {
            sketchDirs = new int[SKETCH_LENGTH];
        }
        sketchDirs[0] = vertical ? SKETCH_VERTICAL : SKETCH_HORIZONTAL;
        dirPos = 1;
        Point s = sketch[1];
        for (int i = 2; i < sketchPos; i++) {
            Point s1 = s;
            s = sketch[i];

            int dx = s.x - s1.x;

            int dy = s.y - s1.y;

            if (horizontal && Math.abs(dx) > Math.abs(dy)) {
                sketchDirs[dirPos] = dx > 0 ? SKETCH_RIGHT : SKETCH_LEFT;
                if (sketchDirs[dirPos] != sketchDirs[dirPos - 1]) {
                    dirPos++;
                }
            } else if (vertical && Math.abs(dy) > Math.abs(dx)) {
                sketchDirs[dirPos] = dy > 0 ? SKETCH_DOWN : SKETCH_UP;
                if (sketchDirs[dirPos] != sketchDirs[dirPos - 1]) {
                    dirPos++;
                }
            }
        }
        sketchDirs[dirPos] = SKETCH_NONE;

		/*
         * System.out.print("p.x"); for (int i = 0; i < sketchPos; i++) {
		 * System.out.print(" " + sketch[i].x); } System.out.println();
		 * System.out.print("p.y"); for (int i = 0; i < sketchPos; i++) {
		 * System.out.print(" " + sketch[i].y); } System.out.println();
		 * System.out.print("dir"); for (int i = 1; i < dirPos; i++) {
		 * System.out.print(" " + sketchDirs[i]); } System.out.println();
		 */
        return true;
    }

    /**
     * Description of the Method
     *
     * @param g1 Description of the Parameter
     * @param g2 Description of the Parameter
     * @return Description of the Return Value
     */
    public boolean compareWithGesture(int[] g1, int[] g2) {
        if (g1.length < g2.length) {
            return false;
        }

        int n = g2.length;
        for (int i = 0; i < n; i++) {
            if (g1[i] != g2[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Description of the Method
     *
     * @return Description of the Return Value
     */
    private int executeSketch() {
        // System.out.println("execute");

        if (sketchDirs == null) {
            return 0;
        }
        if (dirPos < 2) {
            return 0;
        }
        if (table.getSelectedRows().length < 1) {
            return 0;
        }

        if (compareWithGesture(sketchDirs, GESTURE_RIGHT_LEFT_RIGHT)) {
            firePropertyChange("rollRight", null, new Integer(1));
            // System.out.println("gesture right-left-right");
            return 1;
        } else if (compareWithGesture(sketchDirs, GESTURE_LEFT_RIGHT_LEFT)) {
            firePropertyChange("rollLeft", null, new Integer(-1));
            // System.out.println("gesture left-right-left");
            return 1;
        } else if (compareWithGesture(sketchDirs, GESTURE_UP_DOWN_UP)) {
            firePropertyChange("removePageBreak", null, new Integer(1));
            // System.out.println("gesture up-down-up");
            return 1;
        } else if (compareWithGesture(sketchDirs, GESTURE_DOWN_UP_DOWN)) {
            firePropertyChange("addPageBreak", null, new Integer(1));
            // System.out.println("gesture up-down-up");
            return 1;
        } else if (compareWithGesture(sketchDirs, GESTURE_RIGHT_LEFT)) {
            firePropertyChange("relRight", null, new Integer(1));
            // System.out.println("gesture right-left");
            return 2;
        } else if (compareWithGesture(sketchDirs, GESTURE_LEFT_RIGHT)) {
            firePropertyChange("relRight", null, new Integer(-1));
            // System.out.println("gesture left-right");
            return 2;
        } else if (compareWithGesture(sketchDirs, GESTURE_UP_DOWN)) {
            firePropertyChange("join", null, new Double(0.5));
            // System.out.println("gesture up-down");
            return 1;
        } else if (compareWithGesture(sketchDirs, GESTURE_DOWN_UP)) {
            firePropertyChange("split", null, new Double(0.5));
            // System.out.println("gesture down-up");
            return 1;
        }

        if (compareWithGesture(sketchDirs, GESTURE_LEFT)) {
            firePropertyChange("relBeat", null, new Integer(-1));
            table.updatePlayerPosition();
            // System.out.println("gesture left");
            return 2;
        } else if (compareWithGesture(sketchDirs, GESTURE_RIGHT)) {
            firePropertyChange("relBeat", null, new Integer(1));
            table.updatePlayerPosition();
            // System.out.println("gesture right");
            return 2;
        } else if (compareWithGesture(sketchDirs, GESTURE_UP)) {
            firePropertyChange("relHeight", null, new Integer(1));
            // System.out.println("gesture up");
            return 3;
        } else if (compareWithGesture(sketchDirs, GESTURE_DOWN)) {
            firePropertyChange("relHeight", null, new Integer(-1));
            // System.out.println("gesture down");
            return 3;
        }
        return 0;
    }

    /**
     * Sets the mouseOver attribute of the YassSheet object
     *
     * @param onoff The new mouseOver value
     */
    public void setMouseOver(boolean onoff) {
        mouseover = onoff;
    }

    /**
     * Sets the actions attribute of the YassSheet object
     *
     * @param a The new actions value
     */
    public void setActions(YassActions a) {
        actions = a;
    }

    /**
     * Gets the playing attribute of the YassSheet object
     *
     * @return The playing value
     */
    public boolean isPlaying() {
        return isPlaying;
    }

    /**
     * Sets the playing attribute of the YassSheet object
     *
     * @param onoff The new playing value
     */
    public void setPlaying(boolean onoff) {
        isPlaying = onoff;
    }

    /**
     * Gets the temporaryStop attribute of the YassSheet object
     *
     * @return The temporaryStop value
     */
    public boolean isTemporaryStop() {
        return isTemporaryStop;
    }

    /**
     * Sets the temporaryStop attribute of the YassSheet object
     *
     * @param onoff The new temporaryStop value
     */
    public void setTemporaryStop(boolean onoff) {
        isTemporaryStop = onoff;
    }

    protected void setCurrentLineTo(int line) {
        table.setCurrentLineTo(line);
    }

    /**
     * Gets the snapshotShown attribute of the YassSheet object
     *
     * @return The snapshotShown value
     */
    public boolean isSnapshotShown() {
        return paintSnapshot;
    }

    /**
     * Description of the Method
     *
     * @param onoff Description of the Parameter
     */
    public void showSnapshot(boolean onoff) {
        paintSnapshot = onoff;
    }

    /**
     * Description of the Method
     */
    public void removeSnapshot() {
        snapshot = null;
    }

    /**
     * Description of the Method
     */
    public void makeSnapshot() {
        int i = table.getSelectionModel().getMinSelectionIndex();
        int j = table.getSelectionModel().getMaxSelectionIndex();
        if (i >= 0) {
            YassRow r = table.getRowAt(i);
            while (!r.isNote() && i <= j) {
                r = table.getRowAt(++i);
            }
            inSelect = fromTimeline(beatToTimeline(r.getBeatInt()));

            r = table.getRowAt(j);
            while (!r.isNote() && j > i) {
                r = table.getRowAt(--j);
            }
            outSelect = fromTimeline(beatToTimeline(r.getBeatInt()
                    + r.getLengthInt()));
            createSnapshot();
        } else {
            createSnapshot();
        }
    }

    /**
     * Description of the Method
     */
    public void createSnapshot() {
        inSnapshot = inSelect;
        outSnapshot = outSelect;

        int i = table.getSelectionModel().getMinSelectionIndex();
        int j = table.getSelectionModel().getMaxSelectionIndex();
        if (i < 0) {
            return;
        }

        int n = j - i + 1;
        snapshot = new Vector<>(n);
        snapshotRect = new Vector<>(n);
        int startx = -1;
        for (int k = i; k <= j; k++) {
            YassRow row = table.getRowAt(k);
            snapshot.addElement(row);

            YassRectangle r = rect.elementAt(k);
            r = (YassRectangle) r.clone();
            if (startx < 0) {
                startx = (int) r.x;
            }
            r.x -= startx;
            snapshotRect.addElement(r);
        }
    }

    /**
     * Adds a feature to the Table attribute of the YassSheet object
     *
     * @param t The feature to be added to the Table attribute
     */
    public void addTable(YassTable t) {
        tables.addElement(t);
        rects.addElement(new Vector<YassRectangle>(3000, 1000));
    }

    /**
     * Description of the Method
     *
     * @param t Description of the Parameter
     */
    public void removeTable(YassTable t) {
        int i = tables.indexOf(t);
        if (i >= 0) {
            tables.removeElementAt(i);
            rects.removeElementAt(i);
        }
    }

    /**
     * Sets the activeTable attribute of the YassSheet object
     *
     * @param i The new activeTable value
     */
    public void setActiveTable(int i) {
        YassTable t = tables.elementAt(i);
        setActiveTable(t);
    }

    /**
     * Gets the backgroundImage attribute of the YassSheet object
     *
     * @return The backgroundImage value
     */
    public BufferedImage getBackgroundImage() {
        return bgImage;
    }

    /**
     * Sets the backgroundImage attribute of the YassSheet object
     *
     * @param i The new backgroundImage value
     */
    public void setBackgroundImage(BufferedImage i) {
        bgImage = i;
    }

    /**
     * Gets the activeTable attribute of the YassSheet object
     *
     * @return The activeTable value
     */
    public YassTable getActiveTable() {
        return table;
    }

    /**
     * Sets the activeTable attribute of the YassSheet object
     *
     * @param t The new activeTable value
     */
    public void setActiveTable(YassTable t) {
        table = t;
        int k = tables.indexOf(table);
        rect = rects.elementAt(k);

        init();
    }

    public void removeAll() {
        tables.clear();
        rects.clear();
        snapshot = null;
        rect = null;
        table = null;
        gap = 0;
        beatgap = 0;
        outgap = 0;
        bpm = 120;
        setDuration(-1);
        init();
        setZoom(80 * 60 / bpm);
    }

    /**
     * Sets the noteLengthVisible attribute of the YassSheet object
     *
     * @param onoff The new noteLengthVisible value
     */
    public void setNoteLengthVisible(boolean onoff) {
        showNoteLength = onoff;
    }

    public void setNoteHeightVisible(boolean onoff) {
        showNoteHeight = onoff;
    }

    /**
     * Gets the visible attribute of the YassSheet object
     *
     * @param i Description of the Parameter
     * @return The visible value
     */
    public boolean isVisible(int i) {
        YassRectangle r = rect.elementAt(i);
        if (r == null || r.y < 0) {
            return false;
        }

        return r.x >= getLeftX() && r.x + r.width <= clip.x + clip.width - RIGHT_BORDER;
    }

    /**
     * Description of the Method
     *
     * @param g
     *            Description of the Parameter
     */
    // public void paintBackBuffer(Graphics2D g2) {
    //
    // g2.drawImage(image, clip.x, clip.y, clip.x + clip.width, clip.y +
    // clip.height, 0, 0, clip.width, clip.height, Color.white, this);
    // }

    /**
     * Description of the Method
     *
     * @param i Description of the Parameter
     * @param j Description of the Parameter
     */
    public void scrollRectToVisible(int i, int j) {
        int minx = Integer.MAX_VALUE;
        for (int k = i; k <= j; k++) {
            if (k >= table.getRowCount()) {
                return;
            }
            YassRectangle r = rect.elementAt(k);

            double x = r.x;
            if (r.isType(YassRectangle.HEADER) || r.isType(YassRectangle.START)) {
                x = paintHeights ? heightBoxWidth : 0;
            } else if (r.isType(YassRectangle.END)) {
                x = beatToTimeline(outgap);
            } else if (r.isType(YassRectangle.UNDEFINED)) {
                continue;
            } else if (r.y < 0) {
                continue;
            }

            minx = (int) Math.min(x, minx);
        }
        setLeftX(minx);
    }

    /**
     * Gets the viewPosition attribute of the YassSheet object
     *
     * @return The viewPosition value
     */
    public Point getViewPosition() {
        return ((JViewport) getParent()).getViewPosition();
    }

    /**
     * Sets the viewPosition attribute of the YassSheet object
     *
     * @param p The new viewPosition value
     */
    public void setViewPosition(Point p) {
        ((JViewport) getParent()).setViewPosition(p);
        clip = getClipBounds();
    }

    /**
     * Gets the leftX attribute of the YassSheet object
     *
     * @return The leftX value
     */
    public int getLeftX() {
        int x = getViewPosition().x;
        if (paintHeights) {
            x += heightBoxWidth;
        }

        x += LEFT_BORDER;
        return x;
    }

    /**
     * Sets the leftX attribute of the YassSheet object
     *
     * @param x The new leftX value
     */
    public void setLeftX(int x) {
        if (paintHeights) {
            x -= heightBoxWidth;
        }

        x -= LEFT_BORDER;

        setViewPosition(new Point(x, 0));
    }

    /**
     * Gets the clipBounds attribute of the YassSheet object
     *
     * @return The clipBounds value
     */
    public Rectangle getClipBounds() {
        return ((JViewport) getParent()).getViewRect();
    }

    /**
     * Gets the validateRoot attribute of the YassSheet object
     *
     * @return The validateRoot value
     */
    public boolean isValidateRoot() {
        return true;
    }

    /**
     * Description of the Method
     */
    public void revalidate() {
        super.revalidate();
    }

    /**
     * Description of the Method
     *
     * @param g Description of the Parameter
     */
    public void paint(Graphics g) {
        super.paint(g);
    }

    /**
     * Description of the Method
     *
     * @param g Description of the Parameter
     */
    public void paintChildren(Graphics g) {
        if (table == null) {
            System.out.println("No table");
        } else if (table.getRowCount() < 1) {
            Graphics2D g2d = (Graphics2D) g;
            int dw = getWidth();
            int dh = getHeight();

            g2d.setColor(disabledColor);
            g2d.fillRect(0, 0, dw, dh);
        }
    }

    /**
     * Description of the Method
     */
    public void repaint() {
        if (rect == null || rect.size() < 1) {
            return;
        }
        if (isPlaying()) {
            return;
        }
        if (isLive()) {
            return;
        }
        super.repaint();
    }

    /**
     * Description of the Method
     *
     * @param g Description of the Parameter
     */
    public void paintComponent(Graphics g) {
        if (table == null || rect == null || rect.size() < 1) {
            return;
        }
        if (isPlaying()) {
            return;
        }

        if (hSize < 0 || (beatgap == 0 && gap != 0)) {
            update();
        }

        Graphics2D g2 = (Graphics2D) g;
        if (isPlaying()) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_OFF);
        }

        clip = getClipBounds();
        if (image == null || image.getWidth() != clip.width
                || image.getHeight() != clip.height) {
            // do not use INT_RGB for width>2000 (sun bug_id=5005969)
            // image = new BufferedImage(clip.width, clip.height,
            // BufferedImage.TYPE_INT_ARGB);

            image = g2.getDeviceConfiguration().createCompatibleImage(
                    clip.width, clip.height, Transparency.TRANSLUCENT);
            backVolImage = g2.getDeviceConfiguration()
                    .createCompatibleVolatileImage(clip.width, clip.height,
                            Transparency.OPAQUE);
            plainVolImage = g2.getDeviceConfiguration()
                    .createCompatibleVolatileImage(clip.width, clip.height,
                            Transparency.OPAQUE);
            imageChanged = true;
        }

        refreshImage();

        // http://weblogs.java.net/blog/chet/archive/2005/05/graphics_accele.html
        // http://weblogs.java.net/blog/chet/archive/2004/08/toolkitbuffered.html
        // http://today.java.net/pub/a/today/2004/11/12/graphics2d.html

        Graphics2D gb = getBackBuffer().createGraphics();
        gb.drawImage(getPlainBuffer(), 0, 0, null);
        if (getPlainBuffer().contentsLost()) {
            // setErrorMessage(bufferlost);
            revalidate();
        }

        paintText(gb);
        if (showText) {
            paintPlayerText(gb);
        }
        paintPlayerPosition(gb);
        if (showPlayerButtons && !live) {
            paintPlayerButtons(gb);
        }
        if (!live) {
            paintSketch(gb);
        }
        gb.dispose();

        paintBackBuffer(g2);

        if (!live) {
            paintMessage(g2);
        }
    }

    /**
     * Gets the backBuffer attribute of the YassSheet object
     *
     * @return The backBuffer value
     */
    public VolatileImage getBackBuffer() {
        return backVolImage;
    }

    /**
     * Gets the plainBuffer attribute of the YassSheet object
     *
     * @return The plainBuffer value
     */
    public VolatileImage getPlainBuffer() {
        return plainVolImage;
    }

    /**
     * Gets the refreshing attribute of the YassSheet object
     *
     * @return The refreshing value
     */
    public boolean isRefreshing() {
        return refreshing;
    }

    /**
     * Description of the Method
     */
    public void refreshImage() {
        refreshing = true;

        Graphics2D db = image.createGraphics();
        db.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        db.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        db.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        clip = getClipBounds();
        db.setTransform(identity);
        db.setClip(0, 0, clip.width, clip.height);
        db.translate(-clip.x, -clip.y);
        if (!imageChanged) {
            imageChanged = clip.x != imageX;
        }

        paintEmptySheet(db);

        paintInfoArea(db);

        YassPlayer mp3 = actions != null ? actions.getMP3() : null;
        if (mp3 != null && mp3.createWaveform()) {
            paintWaveform(db);
        }

        if (!showVideo() && !showBackground()) {
            paintBeatLines(db);
        }
        paintLines(db);
        if (!live) {
            paintBeats(db);
            paintInOut(db);
            if (paintSnapshot) {
                paintSnapshot(db);
            }
        }

        paintRectangles(db);
        if (paintHeights) {
            paintHeightBox(db);
        }

        paintVersionsText(db);
        if (showArrows && !live) {
            paintArrows(db);
        }

        if (messageMemory && !live) {
            db.setFont(font);
            int maxHeap = (int) (Runtime.getRuntime().maxMemory() / 1024 / 1024);
            int occHeap = (int) (Runtime.getRuntime().totalMemory() / 1024 / 1024);
            int freeHeap = (int) (Runtime.getRuntime().freeMemory() / 1024 / 1024);
            int usedHeap = occHeap - freeHeap;
            String info = usedHeap + " of " + maxHeap + "Mb in use" + ", "
                    + occHeap + "Mb reserved.";
            if (layout.equals("East")) {
                db.drawString(info, clip.x + 10, 40);
            } else if (layout.equals("West")) {
                db.drawString(info, clip.x + 10 + lyricsWidth, 40);
            }
        }

        // message:
        // LYRICS POSITION
        if (getComponentCount() > 0
                && lyricsVisible) {
            YassLyrics c = (YassLyrics) getComponent(0);
            Rectangle cr = c.getBounds();

            if (layout.equals("East")) {
                db.translate(
                        clip.x + clip.width - cr.width + cr.getX()
                                - c.getX(), cr.getY() - c.getY() + 20);
            } else if (layout.equals("West")) {
                db.translate(clip.x, cr.getY() - c.getY() + 20);
            }

            // System.out.println("refresh print");
            c.print(db);
            // System.out.println("refresh print done");
        }
        // paintText(db);

        imageChanged = false;
        imageX = clip.x;

        db.dispose();

        Graphics2D gc = backVolImage.createGraphics();
        gc.drawImage(image, 0, 0, null);
        gc.dispose();

        gc = plainVolImage.createGraphics();
        gc.drawImage(image, 0, 0, null);
        gc.dispose();

        refreshing = false;
    }

    /**
     * Description of the Method
     *
     * @param g Description of the Parameter
     */
    public synchronized void paintBackBuffer(Graphics2D g) {
        final int MAX_TRIES = 5;
        for (int i = 0; i < MAX_TRIES; i++) {

            // switch (backVolImage.validate(g.getDeviceConfiguration())) {
            switch (backVolImage.validate(getGraphicsConfiguration())) {
                case VolatileImage.IMAGE_INCOMPATIBLE:
                    backVolImage.flush();
                    backVolImage = null;
                    image.flush();
                    image = null;
                    plainVolImage.flush();
                    plainVolImage = null;
                    image = g.getDeviceConfiguration().createCompatibleImage(
                            clip.width, clip.height, Transparency.TRANSLUCENT);
                    backVolImage = g.getDeviceConfiguration()
                            .createCompatibleVolatileImage(clip.width, clip.height,
                                    Transparency.OPAQUE);
                    plainVolImage = g.getDeviceConfiguration()
                            .createCompatibleVolatileImage(clip.width, clip.height,
                                    Transparency.OPAQUE);
                    // backVolImage = createVolatileImage(clip.width, clip.height);
                case VolatileImage.IMAGE_RESTORED:
                    Graphics2D gc = backVolImage.createGraphics();
                    gc.drawImage(image, 0, 0, Color.white, null);
                    gc.dispose();
                    break;
            }

            g.drawImage(backVolImage, clip.x, clip.y, this);
            if (!backVolImage.contentsLost()) {
                return;
            }
            System.out.println("contents lost (" + i + ")");
        }
        g.drawImage(image, clip.x, clip.y, clip.x + clip.width, clip.y
                + clip.height, 0, 0, clip.width, clip.height, Color.white, this);
    }

    /**
     * Description of the Method
     *
     * @param g Description of the Parameter
     * @param x Description of the Parameter
     * @param y Description of the Parameter
     * @param w Description of the Parameter
     * @param h Description of the Parameter
     */
    public synchronized void paintBackBuffer(Graphics2D g, int x, int y, int w,
                                             int h) {
        final int MAX_TRIES = 5;
        for (int i = 0; i < MAX_TRIES; i++) {

            // switch (backVolImage.validate(g.getDeviceConfiguration())) {
            switch (backVolImage.validate(getGraphicsConfiguration())) {
                case VolatileImage.IMAGE_INCOMPATIBLE:
                    backVolImage.flush();
                    backVolImage = null;
                    image.flush();
                    image = null;
                    plainVolImage.flush();
                    plainVolImage = null;
                    image = g.getDeviceConfiguration().createCompatibleImage(
                            clip.width, clip.height, Transparency.TRANSLUCENT);
                    backVolImage = g.getDeviceConfiguration()
                            .createCompatibleVolatileImage(clip.width, clip.height,
                                    Transparency.OPAQUE);
                    plainVolImage = g.getDeviceConfiguration()
                            .createCompatibleVolatileImage(clip.width, clip.height,
                                    Transparency.OPAQUE);
                    // backVolImage = createVolatileImage(clip.width, clip.height);
                case VolatileImage.IMAGE_RESTORED:
                    Graphics2D gc = backVolImage.createGraphics();
                    gc.drawImage(image, 0, 0, Color.white, null);
                    gc.dispose();
                    break;
            }

            g.drawImage(backVolImage, clip.x + x, clip.y + y, clip.x + x + w,
                    clip.y + y + h, x, y, x + w, y + h, this);
            if (!backVolImage.contentsLost()) {
                return;
            }
            System.out.println("contents lost (" + i + ")");
        }
        g.drawImage(image, clip.x, clip.y, clip.x + clip.width, clip.y
                + clip.height, 0, 0, clip.width, clip.height, Color.white, this);
    }

    /**
     * Description of the Method
     *
     * @param onoff Description of the Parameter
     */
    public void previewEdit(boolean onoff) {
        actions.previewEdit(onoff);
    }

    /**
     * Description of the Method
     *
     * @param g2 Description of the Parameter
     */
    public void paintEmptySheet(Graphics2D g2) {
        BufferedImage img = null;
        if (videoFrame != null && showVideo) {
            img = videoFrame;
        }
        if (img == null && showBackground) {
            img = bgImage;
        }

        if (img != null) {
            int w = clip.width;
            int h = (int) (w * 3 / 4.0);
            int yy = clip.height / 2 - h / 2;

            g2.setColor(white);
            g2.fillRect(clip.x, 0, clip.width, yy);
            g2.fillRect(clip.x, yy, clip.width, clip.height);

            g2.drawImage(img, clip.x, clip.y + yy, w, h, null);
        } else {
            g2.setColor(white);
            g2.fillRect(clip.x, clip.y, clip.width, clip.height);

            g2.setPaint(bgtex);

            // LYRICS POSITION

            if (lyricsVisible) {
                if (layout.equals("East")) {
                    g2.fillRect(clip.x, TOP_BORDER, clip.width - lyricsWidth,
                            TOP_LINE - 10 - TOP_BORDER - 1);
                } else if (layout.equals("West")) {
                    g2.fillRect(clip.x + lyricsWidth, TOP_BORDER, clip.width
                            - lyricsWidth, TOP_LINE - 10 - TOP_BORDER - 1);
                }
                if (live) {
                    g2.fillRect(clip.x, dim.height - BOTTOM_BORDER + 16,
                            clip.width, BOTTOM_BORDER + 16);
                } else {
                    g2.fillRect(clip.x + LEFT_BORDER, dim.height
                            - BOTTOM_BORDER + 16, clip.width - LEFT_BORDER
                            - RIGHT_BORDER, BOTTOM_BORDER + 16);
                }
            } else {
                g2.fillRect(clip.x, TOP_BORDER, clip.width, TOP_LINE - 10
                        - TOP_BORDER - 1);
                if (live) {
                    g2.fillRect(clip.x, dim.height - BOTTOM_BORDER + 16,
                            clip.width, BOTTOM_BORDER + 16);
                } else {
                    g2.fillRect(clip.x + LEFT_BORDER, dim.height
                            - BOTTOM_BORDER + 16, clip.width - LEFT_BORDER
                            - RIGHT_BORDER, BOTTOM_BORDER + 16);
                }
            }
            g2.setColor(white);
        }
    }

    public void paintInfoArea(Graphics2D g2) {
        BufferedImage img = null;
        if (videoFrame != null && showVideo) {
            img = videoFrame;
        }
        if (img == null && showBackground) {
            img = bgImage;
        }

        if (img == null && !live) {
            int goldenPoints = table.getGoldenPoints();
            if (goldenPoints > 0) {
                int idealGoldenPoints = table.getIdealGoldenPoints();
                int goldenVariance = table.getGoldenVariance();
                int idealGoldenBeats = table.getIdealGoldenBeats();
                int durationGolden = table.getDurationGolden();
                String goldenDiff = table.getGoldenDiff();
                boolean err = Math.abs(goldenPoints - idealGoldenPoints) > goldenVariance;
                g2.setColor(err ? colorSet[5] : dkGray);

                String goldenString = MessageFormat.format(
                        I18.get("correct_golden_info"), "" + idealGoldenPoints,
                        "" + goldenPoints, "" + idealGoldenBeats, ""
                                + durationGolden, goldenDiff);

                g2.drawString(goldenString, clip.x + 10, TOP_BORDER + 20);
                g2.setColor(white);
            }

            if (equalsKeyMillis > 0) {
                g2.setColor(dkGray);
                Font oldFont = g2.getFont();
                g2.setFont(big[big.length - 1]);
                g2.drawString("= " + equalsDigits, clip.x + 10, TOP_BORDER + 60);
                g2.setFont(oldFont);
                g2.setColor(white);
            }
        }
    }

    /**
     * Description of the Method
     *
     * @param g2 Description of the Parameter
     */
    public void paintWaveform(Graphics2D g2) {
        g2.setColor(Color.green);

        int h = TOP_LINE - 10 + 128;

        YassPlayer mp3 = actions.getMP3();

        int lasty = 0;
        for (int x = clip.x + 1; x < clip.x + clip.width; x++) {
            double ms = fromTimelineExact(x);
            int y = mp3.getWaveFormAtMillis(ms);
            g2.drawLine(x - 1, h - lasty, x, h - y);
            lasty = y;
        }

        g2.setColor(white);
    }

    /**
     * Description of the Method
     *
     * @param g2 Description of the Parameter
     */
    public void paintArrows(Graphics2D g2) {
        // if (!paintArrows) return;

        int x = clip.x;// + (paintHeights ? heightBoxWidth : 0);
        int y = dim.height - BOTTOM_BORDER + 16;
        int w = LEFT_BORDER;
        int h = BOTTOM_BORDER - 16;

        boolean isPressed = hiliteCue == PREV_PAGE_PRESSED;
        g2.setColor(isPressed ? disabledColor : new Color(238, 238, 238, 160));
        g2.fillRect(x, y, w, h);

        if (isPressed) {
            g2.setColor(shadow);
            g2.drawRect(x, y, w - 1, h - 1);
        } else {
            g2.setColor(disabledColor);
            g2.drawLine(x, y, x, y + h - 1);
            g2.drawLine(x + 1, y, x + w - 2, y);

            g2.setColor(highlight);
            g2.drawLine(x + 1, y + 1, x + 1, y + h - 3);
            g2.drawLine(x + 2, y + 1, x + w - 3, y + 1);

            g2.setColor(shadow);
            g2.drawLine(x + 1, y + h - 2, x + w - 2, y + h - 2);
            g2.drawLine(x + w - 2, y + 1, x + w - 2, y + h - 3);

            g2.setColor(darkShadow);
            g2.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
            g2.drawLine(x + w - 1, y + h - 1, x + w - 1, y);
        }

        boolean isEnabled = hiliteCue == PREV_PAGE
                || hiliteCue == PREV_PAGE_PRESSED;
        YassUtils.paintTriangle(g2, x + 10, y + 14, w / 3, YassUtils.WEST,
                isEnabled, darkShadow, shadow, highlight);

        x = clip.x + clip.width - RIGHT_BORDER;
        y = dim.height - BOTTOM_BORDER + 16;
        w = RIGHT_BORDER;
        h = BOTTOM_BORDER - 16;

        isPressed = hiliteCue == NEXT_PAGE_PRESSED;
        g2.setColor(isPressed ? disabledColor : new Color(238, 238, 238, 160));
        g2.fillRect(x, y, w, h);
        if (isPressed) {
            g2.setColor(shadow);
            g2.drawRect(x, y, w - 1, h - 1);
        } else {
            g2.setColor(disabledColor);
            g2.drawLine(x, y, x, y + h - 1);
            g2.drawLine(x + 1, y, x + w - 2, y);

            g2.setColor(highlight);
            g2.drawLine(x + 1, y + 1, x + 1, y + h - 3);
            g2.drawLine(x + 2, y + 1, x + w - 3, y + 1);

            g2.setColor(shadow);
            g2.drawLine(x + 1, y + h - 2, x + w - 2, y + h - 2);
            g2.drawLine(x + w - 2, y + 1, x + w - 2, y + h - 3);

            g2.setColor(darkShadow);
            g2.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
            g2.drawLine(x + w - 1, y + h - 1, x + w - 1, y);
        }

        isEnabled = hiliteCue == NEXT_PAGE || hiliteCue == NEXT_PAGE_PRESSED;
        YassUtils.paintTriangle(g2, x + 10, y + 14, w / 3, YassUtils.EAST,
                isEnabled, darkShadow, shadow, highlight);
    }

    /**
     * Description of the Method
     *
     * @param g2 Description of the Parameter
     */
    public void paintPlayerButtons(Graphics2D g2) {
        // if (!paintArrows) return;

        TOP_PLAYER_BUTTONS = dim.height - BOTTOM_BORDER - 64;

        int next = nextElement(playerPos);
        if (next >= 0) {
            YassRectangle rec = rect.elementAt(next);
            if (rec.hasType(YassRectangle.GAP) && next + 1 < rect.size()) {
                rec = rect.elementAt(next + 1);
            }
            if (rec.y + rec.height > TOP_PLAYER_BUTTONS) {
                TOP_PLAYER_BUTTONS = TOP_LINE - 10;
            }
        }

        int x = playerPos - clip.x + 2;
        int y = TOP_PLAYER_BUTTONS;
        int w = 48;
        int h = 64;

        boolean isPressed = hiliteCue == PLAY_NOTE_PRESSED;
        g2.setColor(isPressed ? disabledColor : new Color(238, 238, 238, 160));
        g2.fillRect(x, y, w, h);

        if (isPressed) {
            g2.setColor(shadow);
            g2.drawRect(x, y, w - 1, h - 1);
        } else {
            g2.setColor(disabledColor);
            g2.drawLine(x, y, x, y + h - 1);
            g2.drawLine(x + 1, y, x + w - 2, y);

            g2.setColor(highlight);
            g2.drawLine(x + 1, y + 1, x + 1, y + h - 3);
            g2.drawLine(x + 2, y + 1, x + w - 3, y + 1);

            g2.setColor(shadow);
            g2.drawLine(x + 1, y + h - 2, x + w - 2, y + h - 2);
            g2.drawLine(x + w - 2, y + 1, x + w - 2, y + h - 3);

            g2.setColor(darkShadow);
            g2.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
            g2.drawLine(x + w - 1, y + h - 1, x + w - 1, y);
        }
        boolean isEnabled = hiliteCue == PLAY_NOTE_PRESSED
                || hiliteCue == PLAY_NOTE;
        YassUtils.paintTriangle(g2, x + 16, y + 24, 18, YassUtils.EAST,
                isEnabled, darkShadow, shadow, highlight);

        x = playerPos - clip.x - 32;
        y = TOP_PLAYER_BUTTONS;
        w = 32;
        h = 64;

        isPressed = hiliteCue == PLAY_PAGE_PRESSED;
        g2.setColor(isPressed ? disabledColor : new Color(238, 238, 238, 160));
        g2.fillRect(x, y, w, h);

        if (isPressed) {
            g2.setColor(shadow);
            g2.drawRect(x, y, w - 1, h - 1);
        } else {
            g2.setColor(disabledColor);
            g2.drawLine(x, y, x, y + h - 1);
            g2.drawLine(x + 1, y, x + w - 2, y);

            g2.setColor(highlight);
            g2.drawLine(x + 1, y + 1, x + 1, y + h - 3);
            g2.drawLine(x + 2, y + 1, x + w - 3, y + 1);

            g2.setColor(shadow);
            g2.drawLine(x + 1, y + h - 2, x + w - 2, y + h - 2);
            g2.drawLine(x + w - 2, y + 1, x + w - 2, y + h - 3);

            g2.setColor(darkShadow);
            g2.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
            g2.drawLine(x + w - 1, y + h - 1, x + w - 1, y);
        }
        isEnabled = hiliteCue == PLAY_PAGE_PRESSED || hiliteCue == PLAY_PAGE;
        g2.setColor(isEnabled ? shadow : darkShadow);
        g2.fillRect(x + 7, y + 21, 3, 23);
        YassUtils.paintTriangle(g2, x + 12, y + 27, 12, YassUtils.EAST,
                isEnabled, darkShadow, shadow, highlight);
    }

    /**
     * Description of the Method
     *
     * @param g2 Description of the Parameter
     */
    public void paintBeats(Graphics2D g2) {
        int off = 2;
        if (paintHeights) {
            off += heightBoxWidth;
        }
        g2.setColor(disabledColor);
        g2.fillRect((int) (beatgap * wSize + off), 0, dim.width, TOP_BORDER);

        int multiplier = 1;
        double wwSize = wSize;
        while (wwSize < 10) {
            wwSize *= 4.0;
            multiplier *= 4;
        }

        g2.setColor(Color.darkGray);
        g2.setFont(smallFont);
        FontMetrics metrics = g2.getFontMetrics();
        String str;
        int strw;
        int ms;
        Line2D.Double timeLine = new Line2D.Double(0, 0, 0, 8);
        double leftx = clip.x;
        double rightx = clip.x + clip.width;

        g2.setStroke(thinStroke);
        int i = 0;
        int j;
        double lastx_ms = -1000;
        while (true) {
            timeLine.x1 = timeLine.x2 = (beatgap + i) * wSize + off;
            if (timeLine.x1 < leftx) {
                i++;
                continue;
            }
            if (timeLine.x1 > rightx) {
                break;
            }
            j = i / multiplier;
            if (multiplier == 1 || i % multiplier == 0) {
                if (j % 4 != 0) {
                    g2.draw(timeLine);
                }

                str = i + "";
                strw = metrics.stringWidth(str);
                if (j % 4 == 0) {
                    g2.drawString(str + "", (float) (timeLine.x1 - strw / 2),
                            8f);
                }

                if (timeLine.x1 - lastx_ms > 100) {
                    ms = (int) (1000 * 60 * i / (4 * bpm) + gap);
                    str = YassUtils.commaTime(ms) + "s";
                    strw = metrics.stringWidth(str);
                    g2.drawString(str, (float) (timeLine.x1 - strw / 2), 18f);
                    lastx_ms = timeLine.x1;
                }
            }
            i++;
        }
    }

    /**
     * Description of the Method
     *
     * @param g2 Description of the Parameter
     */
    public void paintBeatLines(Graphics2D g2) {
        if (minHeight > maxHeight) {
            return;
        }

        g2.setStroke(thinStroke);
        g2.setColor(hiGray);
        double miny = dim.height - BOTTOM_BORDER;
        double maxy;
        if (pan) {
            maxy = dim.height - BOTTOM_BORDER
                    - (2 * (NORM_HEIGHT - 1) / 2) * hSize + 1;
        } else {
            maxy = dim.height - BOTTOM_BORDER
                    - (2 * (maxHeight - 1) / 2 - minHeight) * hSize + 1;
        }

        int multiplier = 1;
        double wwSize = wSize;
        while (wwSize < 10) {
            wwSize *= 4.0;
            multiplier *= 4;
        }

        float firstB = -1;
        double leftx = getLeftX() - LEFT_BORDER;
        double rightx = clip.getX() + clip.getWidth();
        Line2D.Double line = new Line2D.Double(0, maxy, 0, miny);
        int off = 0;
        if (paintHeights) {
            off += heightBoxWidth;
        }
        int i = 0;
        int j;
        while (true) {
            line.x1 = line.x2 = (beatgap + i) * wSize + off;
            if (line.x1 < leftx) {
                i++;
                continue;
            }
            if (line.x1 > rightx) {
                break;
            }
            j = i / multiplier;
            if (multiplier == 1 || i % multiplier == 0) {
                if (firstB < 0 && j % 4 == 0) {
                    firstB = (float) line.x1;
                }
                g2.setStroke(j % 4 == 0 ? (showBackground || showVideo) ? thickStroke
                        : stdStroke
                        : thinStroke);
                g2.setColor(j % 4 == 0 ? hiGray : hiGray2);
                g2.draw(line);
            }
            i++;
        }

        if (!live) {
            if (firstB < 0) {
                return;
            }
            line.x1 = firstB;
            line.x2 = line.x1 + 4 * multiplier * wSize;
            line.y1 = line.y2 = TOP_LINE - 10;
            g2.setStroke(thickStroke);
            g2.setColor(dkGray);
            g2.draw(line);
            String bstr = "B";
            if (multiplier > 1) {
                bstr = bstr + "/" + multiplier;
            }
            g2.drawString(bstr, (float) (line.x1 + line.x2) / 2f,
                    (float) (line.y2 - 2));
            line.x1 = line.x2;
            line.y1 -= 2;
            line.y2 += 2;
            g2.draw(line);
            line.x1 = line.x2 = firstB;
            g2.draw(line);
        }
    }

    /**
     * Description of the Method
     *
     * @param g2 Description of the Parameter
     */
    public void paintLines(Graphics2D g2) {
        Line2D.Double line = new Line2D.Double(getLeftX() - LEFT_BORDER, 0,
                clip.x + clip.width, 0);
        if (pan) {
            g2.setColor(hiGray2);
            g2.setStroke(stdStroke);
            for (int h = 0; h < NORM_HEIGHT; h += 2) {
                line.y1 = line.y2 = dim.height - BOTTOM_BORDER - h * hSize;
                g2.draw(line);
            }
        } else {
            for (int h = minHeight; h < maxHeight; h++) {
                if (h % 2 == 1) {
                    continue;
                }
                g2.setStroke(h % 12 == 0 ? stdStroke : thinStroke);
                g2.setColor(h % 12 == 0 ? hiGray : hiGray2);
                line.y1 = line.y2 = dim.height - BOTTOM_BORDER
                        - (h - minHeight) * hSize;
                g2.draw(line);
            }
        }
    }

    /**
     * Gets the noteName attribute of the YassSheet object
     *
     * @param n Description of the Parameter
     * @return The noteName value
     */
    public String getNoteName(int n) {
        while (n < 0) {
            n += 12;
        }
        return actualNoteTable[n % 12];
    }

    /**
     * Gets the whiteNote attribute of the YassSheet object
     *
     * @param n Description of the Parameter
     * @return The whiteNote value
     */
    public boolean isWhiteNote(int n) {
        n = n % 12;
        return n == 0 || n == 2 || n == 4 || n == 5 || n == 7 || n == 9 || n == 11;
    }

    /**
     * Description of the Method
     *
     * @param g2 Description of the Parameter
     */
    public void paintHeightBox(Graphics2D g2) {
        int x = clip.x;
        int y = TOP_LINE - 10;
        int w = heightBoxWidth - 1;
        int hh = clip.height - TOP_LINE + 10 + -BOTTOM_BORDER - 1;

        g2.setStroke(thinStroke);
        g2.setColor(disabledColor);
        g2.fillRect(x, y, w, hh);

        g2.setColor(Color.gray);
        g2.drawRect(x, y, w, hh);

        int blackw = 24;
        int whitew = 40;

        Line2D.Double line = new Line2D.Double(clip.x + heightBoxWidth,
                TOP_LINE - 10, clip.x + clip.width, clip.height - TOP_LINE + 10
                - BOTTOM_BORDER);
        g2.setFont(smallFont);
        FontMetrics metrics = g2.getFontMetrics();
        String str;
        if (pan) {
            for (int h = 1; h < NORM_HEIGHT - 2; h++) {
                line.y1 = line.y2 = dim.height - BOTTOM_BORDER - h * hSize + 4;
                str = getNoteName(hhPageMin + h - 2 + 60);

                boolean isWhite = isWhiteNote(hhPageMin + h - 2 + 60);
                if (isWhite) {
                    g2.setColor(Color.white);
                    g2.fillRect(x + 1, (int) line.y1 - (int) hSize / 2 - 2,
                            whitew, (int) hSize - 2);
                } else {
                    g2.setColor(Color.black);
                    g2.fillRect(x + 1, (int) line.y1 - (int) hSize / 3 - 2,
                            blackw, (int) (2 * hSize / 3) - 3);

                    // paint black over full width
                    // g2.fillRect(clip.x+heightBoxWidth +1, (int) line.y1 -
                    // (int) hSize / 3 - 2,
                    // clip.width-heightBoxWidth-10, (int) (2 * hSize / 3) - 3);
                }
                if (hhPageMin + h == hiliteHeight) {
                    g2.setColor(Color.black);
                } else {
                    g2.setColor(Color.gray);
                }

                if (isWhite) {
                    metrics.stringWidth(str);
                    g2.drawString(str, (float) (clip.x + 3), (float) (line.y1));
                }

                str = "" + (hhPageMin + h - 2);
                int strw = metrics.stringWidth(str);
                g2.drawString(str,
                        (float) (clip.x + heightBoxWidth - strw - 3),
                        (float) (line.y1));
            }
            if (hiliteHeight < 200) {
                g2.setColor(gray);
                g2.fillRect(clip.x + heightBoxWidth, (int) (dim.height
                        - BOTTOM_BORDER - (hiliteHeight - hhPageMin + 1)
                        * hSize), clip.width, (int) (2 * hSize));
                g2.setColor(blue);
                g2.fillRect(clip.x,
                        (int) (dim.height - BOTTOM_BORDER - (hiliteHeight
                                - hhPageMin + 1)
                                * hSize), heightBoxWidth, (int) (2 * hSize));
            }
        } else {
            for (int h = minHeight + 1; h < maxHeight - 2; h++) {
                line.y1 = line.y2 = dim.height - BOTTOM_BORDER
                        - (h - minHeight) * hSize + 4;
                str = getNoteName(h + 60);

                boolean isWhite = isWhiteNote(h + 60);
                if (isWhite) {
                    g2.setColor(Color.white);
                    g2.fillRect(x + 1, (int) line.y1 - 9, whitew, 10);
                } else {
                    g2.setColor(Color.black);
                    g2.fillRect(x + 1, (int) line.y1 - 7, blackw, 6);
                }

                if (h == hiliteHeight) {
                    g2.setColor(Color.black);
                } else {
                    g2.setColor(Color.gray);
                }

                if (isWhite) {
                    metrics.stringWidth(str);
                    g2.drawString(str, (float) (clip.x + 3), (float) (line.y1));
                }
                str = "" + h;
                int strw = metrics.stringWidth(str);
                g2.drawString(str, (float) (x + w - strw - 5),
                        (float) (line.y1));
            }
            if (hiliteHeight < 200) {
                g2.setColor(gray);
                g2.fillRect(clip.x + heightBoxWidth, (int) (dim.height
                        - BOTTOM_BORDER - (hiliteHeight - minHeight + 1)
                        * hSize), clip.width, (int) (2 * hSize));
                g2.setColor(blue);
                g2.fillRect(clip.x,
                        (int) (dim.height - BOTTOM_BORDER - (hiliteHeight
                                - minHeight + 1)
                                * hSize), heightBoxWidth, (int) (2 * hSize));
            }
        }
    }

    /**
     * Description of the Method
     *
     * @param g2 Description of the Parameter
     */
    public void paintInOut(Graphics2D g2) {
        if (sketchStarted()) {
            return;
        }

        if (select.y > 0) {
            g2.setColor(inoutColor);
            g2.fill(select);
        }

        if (inSnapshot >= 0) {
            if (outSnapshot < 0) {
                outSnapshot = inSnapshot;
            }
            g2.setColor(inoutSnapshotBarColor);
            int inf = toTimeline(inSnapshot);
            int outf = toTimeline(outSnapshot);
            int x = Math.min(inf, outf);
            int xw = Math.abs(outf - inf);
            if (xw > 0) {
                g2.fillRect(x, 0, xw, 10);
            }
        }

        if (inSelect >= 0) {
            if (outSelect < 0) {
                outSelect = inSelect;
            }
            g2.setColor(hiliteCue == SNAPSHOT ? inoutSnapshotBarColor
                    : inoutBarColor);
            g2.setFont(smallFont);
            int inf = toTimeline(inSelect);
            int outf = toTimeline(outSelect);
            int x = Math.min(inf, outf);
            int xw = Math.abs(outf - inf);
            if (xw > 0) {
                g2.fillRect(x, 0, xw, 10);
                long d = paintHeights ? fromTimeline(xw + heightBoxWidth)
                        : fromTimeline(xw);
                String s = YassUtils.commaTime(d) + "s";
                int sw = g2.getFontMetrics().stringWidth(s);

                g2.setColor(disabledColor);
                g2.fillRect(x + xw / 2 - sw / 2 - 5, 11, sw + 10, 9);

                g2.setColor(dkGray);
                g2.drawString(s, x + xw / 2 - sw / 2, 18);
            }
        }

        if (inPoint < 0) {
            return;
        }
        if (outPoint < 0) {
            outPoint = inPoint;
        }

        g2.setColor(inoutColor);
        g2.fillRect(Math.min(inPoint, outPoint), TOP_LINE - 10,
                Math.abs(outPoint - inPoint), clip.height - TOP_LINE + 10
                        - BOTTOM_BORDER);
    }

    /**
     * Description of the Method
     *
     * @param g2     Description of the Parameter
     * @param active Description of the Parameter
     */
    public void paintPlayerPosition(Graphics2D g2, boolean active) {
        int left = paintHeights ? heightBoxWidth : 0;
        if (playerPos < left) {
            return;
        }

        if (!isPlaying && !live) {
            long d = fromTimeline(playerPos);
            String s = YassUtils.commaTime(d) + "s";
            g2.setFont(smallFont);
            int sw = g2.getFontMetrics().stringWidth(s);
            g2.setColor(disabledColor);
            g2.fillRect(playerPos - clip.x - sw / 2 - 5, 11, sw + 10, 9);
            g2.setColor(dkGray);
            g2.drawString(s, playerPos - clip.x - sw / 2, 18);
        }

        int y = TOP_LINE - 10;
        int h = dim.height - TOP_LINE + 10 - BOTTOM_BORDER;
        if (!active) {
            if (hiliteCue == MOVE_REMAINDER) {
                g2.setColor(blue);
            } else {
                g2.setColor(playerColor);
            }
            g2.fillRect(playerPos - clip.x - 1, y, 3, h);
            if (!live) {
                g2.fillRect(playerPos - clip.x - 1, TOP_BORDER, 3, 8);
            }
        } else {
            g2.setColor(playerColor3);
            g2.fillRect(playerPos - clip.x - 1 - 2, y, 1, h);
            g2.setColor(playerColor2);
            g2.fillRect(playerPos - clip.x - 1 - 1, y, 1, h);
            g2.setColor(playerColor);
            g2.fillRect(playerPos - clip.x - 1, y, 3, h);
        }
    }

    /**
     * Description of the Method
     *
     * @param g2 Description of the Parameter
     */
    public void paintPlayerPosition(Graphics2D g2) {
        paintPlayerPosition(g2, false);
    }

    /**
     * Description of the Method
     *
     * @param g2 Description of the Parameter
     */
    public void paintSketch(Graphics2D g2) {
        if (!sketchStarted()) {
            return;
        }
        if (sketchPos < 1) {
            return;
        }

        g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));
        g2.setColor(blue);
        Point p1 = sketch[0];
        for (int k = 1; k < sketchPos; k++) {
            Point p2 = sketch[k];
            g2.drawLine(-clip.x + p1.x, p1.y, -clip.x + p2.x, p2.y);
            p1 = p2;
        }
    }

    /**
     * Description of the Method
     *
     * @param g2 Description of the Parameter
     */
    public void paintRectangles(Graphics2D g2) {
        Enumeration<YassTable> ts = tables.elements();
        for (Enumeration<Vector<YassRectangle>> e = rects.elements(); e
                .hasMoreElements(); ) {
            Vector<YassRectangle> r = e.nextElement();
            YassTable t = ts.nextElement();
            if (r == rect) {
                continue;
            }
            paintRectangles(g2, r, t.getTableColor(), false);
        }
        if (rect != null) {
            paintRectangles(g2, rect, table.getTableColor(), true);
        }
    }

    /**
     * Description of the Method
     *
     * @param g2    Description of the Parameter
     * @param rect  Description of the Parameter
     * @param col   Description of the Parameter
     * @param onoff Description of the Parameter
     */
    public void paintRectangles(Graphics2D g2, Vector<?> rect, Color col,
                                boolean onoff) {
        YassRectangle r = null;
        int i = 0;
        new Line2D.Double(0, 0, 0, 0);
        RoundRectangle2D.Double mouseRect = new RoundRectangle2D.Double(0, 0,
                0, 0, 0, 0);
        RoundRectangle2D.Double cutRect = new RoundRectangle2D.Double(0, 0, 0,
                0, 10, 10);
        YassRectangle prev;
        YassRectangle next = null;

        int rows[] = table != null ? table.getSelectedRows() : null;
        int selnum = rows != null ? rows.length : 1;
        int selfirst = table != null ? table.getSelectionModel()
                .getMinSelectionIndex() : -1;
        int sellast = table != null ? table.getSelectionModel()
                .getMaxSelectionIndex() : -1;

        int pn = 1;

        int cx = clip.x;
        int cw = clip.width;
        int ch = clip.height;
        Line2D.Double smallRect = new Line2D.Double(0, 0, 0, 0);
        Rectangle clip2 = new Rectangle(cx, 0, cw, ch);

        Paint oldPaint = g2.getPaint();
        Stroke oldStroke = g2.getStroke();

        for (Enumeration<?> e = rect.elements(); e.hasMoreElements()
                || next != null; i++) {
            prev = r;
            if (next != null) {
                r = next;
                next = e.hasMoreElements() ? (YassRectangle) e.nextElement()
                        : null;
            } else {
                r = (YassRectangle) e.nextElement();
            }
            if (next == null) {
                next = e.hasMoreElements() ? (YassRectangle) e.nextElement()
                        : null;
            }
            if (r.isPageBreak()) {
                pn = r.getPageNumber();
            }

            Color borderCol = col;

            if (r.x < clip.x + clip.width && r.x + r.width > clip.x) {
                if (!r.isPageBreak()) {
                    hhPageMin = r.getPageMin();
                }

                boolean isSelected = table != null && table.isRowSelected(i);

                if (onoff) {
                    if (!r.isPageBreak() && table.getMultiSize() > 1) {
                        Color bg = pn % 2 == 1 ? oddpageColor : evenpageColor;
                        int w = (next == null || next.isPageBreak() || next
                                .hasType(YassRectangle.END)) ? (int) r.width
                                : (int) (next.x - r.x + 1);
                        g2.setColor(bg);
                        g2.fillRect((int) r.x, clip.height - BOTTOM_BORDER, w,
                                16);
                    }
                    Color shadeCol = table.isRowSelected(i) ? colorSet[2]
                            : colorSet[1];
                    Color hiliteFill = colorSet[0];
                    if (r.isType(YassRectangle.GOLDEN)) {
                        hiliteFill = colorSet[3];
                    } else if (r.isType(YassRectangle.FREESTYLE)) {
                        hiliteFill = colorSet[4];
                    } else if (r.isType(YassRectangle.WRONG)) {
                        hiliteFill = colorSet[5];
                    }
                    if (noshade) {
                        g2.setPaint(table.isRowSelected(i) ? colorSet[2]
                                : hiliteFill);
                    } else {
                        g2.setPaint(new GradientPaint((float) r.x,
                                (float) r.y + 2, hiliteFill, (float) (r.x),
                                (float) (r.y + r.height), shadeCol));
                    }
                }

                if (r.isPageBreak()) {
                    Line2D.Double dashLine = new Line2D.Double(0, 0, 0, 0);
                    float dash1[] = {8f, 2f};

                    float dashWidth = .5f;

                    BasicStroke dashed = new BasicStroke(dashWidth,
                            BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                            10.0f, dash1, 0.0f);

                    g2.setStroke(dashed);
                    if (r.isType(YassRectangle.WRONG)) {
                        g2.setColor(colorSet[6]);
                    }
                    dashLine.y1 = TOP_LINE - 10;
                    dashLine.y2 = clip.height - BOTTOM_BORDER;
                    if (wSize > 5) {
                        dashLine.x1 = dashLine.x2 = r.x - 3;
                        g2.draw(dashLine);
                    }
                    dashLine.x1 = dashLine.x2 = r.x - 1;
                    g2.draw(dashLine);
                    if (r.width >= 2 * wSize) {
                        dashLine.x1 = dashLine.x2 = r.x;
                        dashLine.x1 = dashLine.x2 = r.x + r.width;
                        g2.draw(dashLine);
                    }
                } else {
                    if (r.isType(YassRectangle.GAP)) {
                        if (!live) {
                            g2.setColor(dkGray);
                            Rectangle2D.Double rec = new Rectangle2D.Double(
                                    r.x, r.y, 6, r.height);
                            g2.fill(rec);
                            rec.x = rec.x + r.width - 5;
                            g2.fill(rec);
                        }
                        continue;
                    }
                    if (r.isType(YassRectangle.START)) {
                        if (!live) {
                            g2.setColor(dkGray);
                            Rectangle2D.Double rec = new Rectangle2D.Double(
                                    r.x, r.y, 2, r.height);
                            g2.fill(rec);
                            rec.width = 4;
                            rec.height = 2;
                            g2.fill(rec);
                            rec.y = r.y + r.height - 2;
                            g2.fill(rec);
                        }
                        continue;
                    }
                    if (r.isType(YassRectangle.END)) {
                        if (!live) {
                            g2.setColor(dkGray);
                            Rectangle2D.Double rec = new Rectangle2D.Double(
                                    r.x, r.y, 2, r.height);
                            g2.fill(rec);
                            rec.x = r.x - 4;
                            rec.width = 4;
                            rec.height = 2;
                            g2.fill(rec);
                            rec.y = r.y + r.height - 2;
                            g2.fill(rec);
                        }
                        continue;
                    }

                    if (onoff && r.width > 2.4) {
                        g2.fill(r);

                        if (showNoteLength) {
                            int len = (int) ((r.width + 2) / wSize + 0.5);
                            String lenstr = "" + len;
                            int yoff = 4;

                            int midx = (int) (r.x + r.width / 2);
                            Font oldFont = g2.getFont();
                            g2.setColor(dkGray);
                            g2.setFont(big[16]);
                            FontMetrics metrics = g2.getFontMetrics();
                            int strw = metrics.stringWidth(lenstr);
                            int lenx = (int) (r.x + r.width - 2 - strw);
                            int leny = (int) (r.y + r.height - yoff + 16);
                            if (lenx < midx + 2)
                                lenx = midx;
                            if (r.width < 24) {
                                lenx = (int) (midx - strw / 2 + 0.5);
                            }
                            g2.drawString(lenstr, lenx, leny);
                            g2.setFont(oldFont);

                        }
                        if (showNoteHeight) {
                            int pitch = table.getRowAt(i).getHeightInt();
                            String hstr = "" + getNoteName(pitch + 60);

                            int yoff = 4;

                            int midx = (int) (r.x + r.width / 2);
                            Font oldFont = g2.getFont();
                            g2.setColor(dkGray);
                            g2.setFont(big[16]);
                            FontMetrics metrics = g2.getFontMetrics();
                            int strw = metrics.stringWidth(hstr);
                            int hx = (int) (r.x + 3);
                            int hy = (int) (r.y + r.height - yoff + 16);
                            if (hx + strw > midx - 2)
                                hx = midx - strw;
                            if (r.width < 24) {
                                hx = (int) (midx - strw / 2 + 0.5);
                                if (showNoteLength) hy += 10;
                            }
                            g2.drawString(hstr, hx, hy);
                            g2.setFont(oldFont);

                        }
                    }

                    if (!live) {
                        boolean paintLeft = false;
                        boolean paintRight = false;
                        if (mouseover) {
                            if (isSelected && hiliteAction != ACTION_NONE) {
                                if (selnum == 1) {
                                    if (hiliteAction == ACTION_CONTROL_ALT) {
                                        paintLeft = paintRight = true;
                                    }
                                    if (hiliteAction == ACTION_CONTROL) {
                                        paintLeft = true;
                                    }
                                    if (hiliteAction == ACTION_ALT) {
                                        paintRight = true;
                                    }
                                } else if (selnum >= 2) {
                                    if (hiliteAction == ACTION_CONTROL_ALT) {
                                        if (selfirst == i) {
                                            paintLeft = true;
                                        }
                                        if (sellast == i) {
                                            paintRight = true;
                                        }
                                    }
                                    if (hiliteAction == ACTION_CONTROL) {
                                        paintLeft = true;
                                    }
                                    if (hiliteAction == ACTION_ALT) {
                                        paintRight = true;
                                    }
                                }
                            }
                        } else {
                            if ((selnum == 1 && isSelected)
                                    || (hilite == i && !isSelected)) {
                                if (hilite < 0 || hilite == i) {
                                    if (hiliteAction == ACTION_CONTROL_ALT) {
                                        paintLeft = paintRight = true;
                                    }
                                    if (hiliteAction == ACTION_CONTROL) {
                                        paintLeft = true;
                                    }
                                    if (hiliteAction == ACTION_ALT) {
                                        paintRight = true;
                                    }
                                }
                            } else if (selnum >= 2 && isSelected) {
                                if (hilite < 0 || table.isRowSelected(hilite)) {
                                    if (hiliteAction == ACTION_CONTROL_ALT) {
                                        if (selfirst == i) {
                                            paintLeft = true;
                                        }
                                        if (sellast == i) {
                                            paintRight = true;
                                        }
                                    }
                                }
                                if (hiliteAction == ACTION_CONTROL) {
                                    paintLeft = true;
                                }
                                if (hiliteAction == ACTION_ALT) {
                                    paintRight = true;
                                }
                            }
                        }
                        int dragw = r.width > Math.max(wSize, 32) * 3 ? (int) Math
                                .max(wSize, 32) : (r.width > 72 ? 24
                                : (r.width > 48 ? 16 : 5));
                        if (paintLeft) {
                            g2.setColor(blueDrag);
                            clip.width = (int) r.x - clip.x + dragw;
                            g2.setClip(clip);
                            g2.fill(r);
                        }
                        if (paintRight) {
                            g2.setColor(blueDrag);
                            clip.x = (int) (r.x + r.width - dragw + 1);
                            g2.setClip(clip);
                            g2.fill(r);
                        }
                        if (paintLeft || paintRight) {
                            clip.x = cx;
                            clip.width = cw;
                            g2.setClip(clip);
                        }
                    }

                    borderCol = hilite == i ? colorSet[2] : borderCol;

                    g2.setColor(borderCol);

                    if (wSize < 10) {
                        g2.setStroke(stdStroke);
                    } else {
                        g2.setStroke(medStroke);
                    }

                    if (hilite == i && !r.isType(YassRectangle.GAP)
                            && !r.isType(YassRectangle.START)
                            && !r.isType(YassRectangle.END)) {
                        if (hiliteCue == CUT || hiliteCue == JOIN_LEFT
                                || hiliteCue == JOIN_RIGHT) {
                            if (r.width > 5) {
                                mouseRect.x = r.x;
                                mouseRect.y = r.y - hSize;
                                mouseRect.width = r.width;
                                mouseRect.height = hSize - 1;

                                g2.setColor(blueDrag);
                                g2.fill(mouseRect);

                                g2.setColor(borderCol);
                                clip2.x = (int) (r.x);
                                clip2.width = (int) (wSize / 2);
                                g2.setClip(clip2);
                                g2.fill(mouseRect);

                                clip2.x = (int) (r.x + r.width - wSize / 2);
                                clip2.width = cw;
                                g2.setClip(clip2);
                                g2.fill(mouseRect);

                                g2.setClip(clip);

                                if (wSize / 2 > 5) {
                                    g2.setColor(Color.white);
                                    int hh = (int) (r.y - hSize / 2);
                                    g2.drawLine((int) (r.x + 3), hh, (int) (r.x
                                            + wSize / 2 - 4), hh);
                                    g2.drawLine((int) (r.x + 5), hh - 1,
                                            (int) (r.x + 5), hh + 1);
                                    g2.drawLine((int) (r.x + 6), hh - 2,
                                            (int) (r.x + 6), hh + 2);
                                    g2.drawLine((int) (r.x + r.width - wSize
                                                    / 2 + 3), hh,
                                            (int) (r.x + r.width - 4), hh);
                                    g2.drawLine((int) (r.x + r.width - 6),
                                            hh - 1, (int) (r.x + r.width - 6),
                                            hh + 1);
                                    g2.drawLine((int) (r.x + r.width - 7),
                                            hh - 2, (int) (r.x + r.width - 7),
                                            hh + 2);
                                }

                                // int cutx = (int) (r.x - 2 + wSize / 2);
                                // g2.drawLine(cutx, (int) (r.y - hSize), cutx,
                                // (int) (r.y - 3));
                                // cutx = (int) (r.x + r.width - 2 - wSize / 2);
                                // g2.drawLine(cutx, (int) (r.y - hSize), cutx,
                                // (int) (r.y - 3));
                                // for (cutx = (int) (r.x + wSize); cutx < (int)
                                // (r.x + r.width - wSize / 2); cutx += wSize) {
                                // g2.drawLine(cutx, (int) (r.y - hSize), cutx,
                                // (int) (r.y - 3));
                                // }

                                g2.setColor(borderCol);
                            }
                        }

                        if (hiliteCue == CUT) {
                            cutRect.x = r.x;
                            cutRect.y = r.y;
                            cutRect.width = wSize
                                    * Math.round(cutPercent * r.width / wSize);
                            cutRect.height = r.height;
                            g2.draw(cutRect);
                            cutRect.x = r.x + cutRect.width;
                            cutRect.y = r.y;
                            cutRect.width = r.width - cutRect.width;
                            cutRect.height = r.height;
                            g2.draw(cutRect);
                        } else if (hiliteCue == JOIN_LEFT && prev != null) {
                            cutRect.x = prev.x;
                            cutRect.y = r.y;
                            cutRect.width = r.x - prev.x + r.width;
                            cutRect.height = r.height;
                            g2.draw(cutRect);
                        } else if (hiliteCue == JOIN_RIGHT && next != null) {
                            cutRect.x = r.x;
                            cutRect.y = r.y;
                            cutRect.width = next.x - r.x + next.width;
                            cutRect.height = r.height;
                            g2.draw(cutRect);
                        }
                    }

                    if (hilite != i
                            || (hiliteCue != CUT && hiliteCue != JOIN_RIGHT && hiliteCue != JOIN_LEFT)) {
                        if (r.width > 2.4) {
                            g2.draw(r);
                        } else if (r.width > 1.4) {
                            Color c = table.isRowSelected(i) ? colorSet[2]
                                    : dkGray;
                            g2.setColor(c);

                            smallRect.x1 = r.x;
                            smallRect.y1 = r.y;
                            smallRect.x2 = r.x;
                            smallRect.y2 = r.y + r.height;
                            g2.draw(smallRect);
                            smallRect.x1++;
                            smallRect.x2++;
                            g2.draw(smallRect);
                        } else {
                            Color c = table.isRowSelected(i) ? colorSet[2]
                                    : dkGray;
                            g2.setColor(c);

                            smallRect.x1 = r.x;
                            smallRect.y1 = r.y;
                            smallRect.x2 = r.x;
                            smallRect.y2 = r.y + r.height;
                            g2.draw(smallRect);
                        }
                    }
                }
            }
        }
        g2.setPaint(oldPaint);
        g2.setStroke(oldStroke);
    }

    /**
     * Description of the Method
     *
     * @param g2 Description of the Parameter
     */
    public void paintPlainRectangles(Graphics2D g2) {
        YassRectangle r;
        new YassRectangle();
        Color borderCol = dkGray;

        int cx = clip.x;
        int cw = clip.width;
        int ch = clip.height;

        Paint oldPaint = g2.getPaint();

        Rectangle clip2 = new Rectangle(cx, 0, cw, ch);

        for (Enumeration<?> e = rect.elements(); e.hasMoreElements(); ) {
            r = (YassRectangle) e.nextElement();
            if (r.isPageBreak() || r.isType(YassRectangle.START)
                    || r.isType(YassRectangle.GAP)
                    || r.isType(YassRectangle.END)) {
                continue;
            }

            if (!(r.x < clip.x + clip.width && r.x + r.width > clip.x)) {
                continue;
            }

            // r.x = r.x - clip.x;

            Color shadeCol = colorSet[1];
            Color hiliteFill = colorSet[0];
            if (r.isType(YassRectangle.GOLDEN)) {
                hiliteFill = colorSet[3];
            } else if (r.isType(YassRectangle.FREESTYLE)) {
                hiliteFill = colorSet[4];
            } else if (r.isType(YassRectangle.WRONG)) {
                hiliteFill = colorSet[5];
            }

            g2.setPaint(new GradientPaint((float) r.x, (float) r.y + 2,
                    hiliteFill, (float) (r.x), (float) (r.y + r.height),
                    shadeCol));

            clip2.width = cw;
            g2.setClip(clip2);
            g2.fill(r);

            g2.setPaint(new GradientPaint((float) r.x, (float) r.y - 4,
                    playBlueHi, (float) (r.x), (float) (r.y + r.height),
                    playBlue));

            clip2.width = playerPos - cx;
            g2.setClip(clip2);
            g2.fill(r);

            clip2.width = cw;
            g2.setClip(clip2);

            g2.setColor(borderCol);
            g2.setStroke(new BasicStroke(1.5f));
            g2.draw(r);

            // r.x = r.x + clip.x;
        }
        g2.setPaint(oldPaint);
    }

    /**
     * Description of the Method
     *
     * @param g2 Description of the Parameter
     */
    public void paintSnapshot(Graphics2D g2) {
        if (snapshot == null) {
            return;
        }

        Paint oldPaint = g2.getPaint();

        int next = nextElement(playerPos);
        if (next < 0) {
            return;
        }
        if (!table.isRowSelected(next)) {
            return;
        }
        YassRectangle rec = rect.elementAt(next);
        int pageMin = rec.getPageMin();

        double timelineGap = table.getGap() * 4 / (60 * 1000 / table.getBPM());

        YassRectangle r;
        YassRow row;
        int startx = -1;
        for (Enumeration<Cloneable> e = snapshotRect.elements(), te = snapshot
                .elements(); e.hasMoreElements() && te.hasMoreElements(); ) {
            r = (YassRectangle) e.nextElement();
            row = (YassRow) te.nextElement();

            // copied from update
            int beat = row.getBeatInt();
            int length = row.getLengthInt();
            int height = row.getHeightInt();
            r.x = (timelineGap + beat) * wSize + 1;
            if (paintHeights) {
                r.x += heightBoxWidth;
            }
            if (pan) {
                r.y = dim.height - BOTTOM_BORDER - (height - pageMin + 2)
                        * hSize - hSize + 1;
            } else {
                r.y = dim.height - BOTTOM_BORDER - (height - minHeight) * hSize
                        - hSize + 1;
            }
            r.width = length * wSize - 2;
            r.height = 2 * hSize - 2;

            if (startx < 0) {
                startx = (int) r.x;
            }

            r.x = r.x - startx + playerPos;

            g2.setPaint(tex);
            g2.fill(r);

            if (r.height > 3 * hSize) {
                g2.fill(r);
            } else {
                g2.fill(r);
            }
        }
        g2.setPaint(oldPaint);
    }

    /**
     * Sets the versionTextPainted attribute of the YassSheet object
     *
     * @param onoff The new versionTextPainted value
     */
    public void setVersionTextPainted(boolean onoff) {
        versionTextPainted = onoff;
    }

    /**
     * Description of the Method
     *
     * @param g2 Description of the Parameter
     */
    public void paintVersionsText(Graphics2D g2) {
        if (!versionTextPainted) {
            return;
        }

        int off = 1;
        Enumeration<Vector<YassRectangle>> er = rects.elements();
        for (Enumeration<YassTable> e = tables.elements(); e.hasMoreElements()
                && er.hasMoreElements(); ) {
            YassTable t = e.nextElement();
            Vector<?> r = er.nextElement();
            if (t == table) {
                continue;
            }
            Color c = t.getTableColor();
            paintTableText(g2, t, r, c.darker(), c, off, 0, false);
            off++;
        }
    }

    /**
     * Description of the Method
     *
     * @param g2 Description of the Parameter
     */
    public void paintText(Graphics2D g2) {
        int i = tables.indexOf(table);
        if (i < 0) {
            return;
        }
        Color c = table.getTableColor();
        paintTableText(g2, table, rect, c.darker(), c, 0, -clip.x, true);
    }

    /**
     * Description of the Method
     *
     * @param g2   Description of the Parameter
     * @param t    Description of the Parameter
     * @param re   Description of the Parameter
     * @param c1   Description of the Parameter
     * @param c2   Description of the Parameter
     * @param off  Description of the Parameter
     * @param offx Description of the Parameter
     * @param info Description of the Parameter
     */
    public void paintTableText(Graphics2D g2, YassTable t, Vector<?> re,
                               Color c1, Color c2, int off, int offx, boolean info) {
        String str;
        String ostr;
        int strw;
        int strh;
        YassRectangle r;
        YassRectangle next = null;
        FontMetrics metrics;

        int pn = 1;
        float sx;
        float sh;

        Rectangle2D.Double lastStringBounds = null;
        Rectangle2D.Double strBounds = new Rectangle2D.Double(0, 0, 0, 0);

        Enumeration<?> en = ((YassTableModel) t.getModel()).getData()
                .elements();
        for (Enumeration<?> ren = re.elements(); ren.hasMoreElements()
                && en.hasMoreElements(); ) {
            if (next != null) {
                r = next;
                next = ren.hasMoreElements() ? (YassRectangle) ren
                        .nextElement() : null;
            } else {
                r = (YassRectangle) ren.nextElement();
            }
            if (next == null) {
                next = ren.hasMoreElements() ? (YassRectangle) ren
                        .nextElement() : null;
            }

            str = ((YassRow) en.nextElement()).getText();

            if (r.isType(YassRectangle.GAP)) {
                continue;
            }
            if (r.isType(YassRectangle.START)) {
                continue;
            }
            if (r.isType(YassRectangle.END)) {
                continue;
            }
            if (r.isPageBreak()) {
                pn = r.getPageNumber();
            }

            boolean isVisible = r.x < clip.x + clip.width
                    && r.x + r.width > getLeftX();
            if (!isVisible) {
                continue;
            }

            if (info && r.isType(YassRectangle.FIRST)) {
                String s = pn + "";
                g2.setColor(dkGray);
                g2.setFont(big[18]);
                // g2.drawString(s, (float) (r.x + offx + 5), 18);
                g2.drawString(s, (float) (r.x + offx + 5), clip.height
                        - BOTTOM_BORDER + 14);
            }

            if (str.length() < 1) {
                continue;
            }
            ostr = str = str.replace(YassRow.SPACE, ' ');

            if (off == 0 && playerPos >= r.x && playerPos < r.x + r.width) {
                g2.setColor(c1);
                // if (isPlaying) {
                // int shade = (int) ((big.length - 1 - 6) - (big.length - 18 -
                // 6) * (playerPos - r.x) / r.width);
                // g2.setFont(big[shade]);
                // } else {
                g2.setFont(big[18]);
                // }
            } else {
                g2.setColor(c2);
                g2.setFont(big[18]);

                if (r.isType(YassRectangle.FREESTYLE)) {
                    g2.setFont(fonti);
                } else if (r.isType(YassRectangle.GOLDEN)) {
                    g2.setFont(fontb);
                } else if (table != t) {
                    g2.setFont(fontv);
                } else {
                    g2.setFont(font);
                }
            }

            metrics = g2.getFontMetrics();
            strw = metrics.stringWidth(str);

            if (off == 0 && strw > r.width) {
                g2.setFont(big[15]);
                metrics = g2.getFontMetrics();
                strw = metrics.stringWidth(str);
            }
            if (off == 0 && strw > r.width) {
                g2.setFont(big[13]);
                metrics = g2.getFontMetrics();
                strw = metrics.stringWidth(str);
            }

            sx = (float) Math.round(r.x + r.width / 2f - strw / 2f + offx + 1);
            strh = metrics.getAscent();
            if (off == 0) {
                sh = (float) (r.y + r.height / 2 + strh / 2f);
            } else {
                sh = (float) (r.y + r.height + 2 + strh);
            }

            if (strw <= r.width) {
                if (off == 0) {
                    g2.setColor(white);
                }
                g2.drawString(str, sx, sh);
            } else {
                g2.setFont(big[18]);
                metrics = g2.getFontMetrics();
                strw = metrics.stringWidth(ostr);
                strh = metrics.getAscent();
                if (strh > r.width + 4) {
                    g2.setFont(big[15]);
                    metrics = g2.getFontMetrics();
                    strh = metrics.getAscent();
                }
                if (strh > r.width + 4) {
                    g2.setFont(big[13]);
                    metrics = g2.getFontMetrics();
                    strh = metrics.getAscent();
                }
                sx = (float) (r.x + r.width / 2 + strh / 2f + offx - 1);
                sh = (float) (r.y - 5);
                strBounds.x = r.x + r.width / 2 - strh / 2f + offx - 1;
                strBounds.y = r.y - 5 - strw;
                strBounds.width = strh;
                strBounds.height = strw;
                if (strh <= r.width + 4 || lastStringBounds == null
                        || !lastStringBounds.intersects(strBounds)) {
                    if (lastStringBounds == null) {
                        lastStringBounds = new Rectangle2D.Double(0, 0, 0, 0);
                    }
                    lastStringBounds.setRect(strBounds);

                    g2.translate(sx, sh);
                    g2.rotate(-Math.PI / 2);

                    g2.setColor(dkGray);
                    g2.drawString(ostr, 0, 0);

                    g2.rotate(Math.PI / 2);
                    g2.translate(-sx, -sh);
                }
            }
        }
    }

    /**
     * Gets the live attribute of the YassSheet object
     *
     * @return The live value
     */
    public boolean isLive() {
        return live;
    }

    /**
     * Sets the live attribute of the YassSheet object
     *
     * @param onoff The new live value
     */
    public void setLive(boolean onoff) {
        getComponent(0).setVisible(!onoff);
        live = onoff;
    }

    /**
     * Description of the Method
     *
     * @param g2     Description of the Parameter
     * @param waitms Description of the Parameter
     */
    public void paintWait(Graphics2D g2, int waitms) {
        int sec = waitms / 1000;

        int leftx = live ? 0 : LEFT_BORDER;

        if (waitms <= 4000) {
            int width = (int) (60 * waitms / 4000.0);
            g2.setColor(blue);
            g2.fillRect(leftx, clip.height - BOTTOM_BORDER + 16, width,
                    BOTTOM_BORDER - 16);
        } else {
            int width = 60;
            g2.setColor(blue);
            g2.fillRect(leftx, clip.height - BOTTOM_BORDER + 16, width,
                    BOTTOM_BORDER - 16);
        }
        if (waitms > 3000) {
            String s = sec + "";
            g2.setFont(big[24]);
            g2.setColor(Color.white);
            FontMetrics metrics = g2.getFontMetrics();
            int strw = metrics.stringWidth(s);
            float sx = leftx + 30 - strw / 2;
            float sh = clip.height - 12;
            g2.drawString(s, sx, sh);
        }
    }

    /**
     * Description of the Method
     *
     * @param g2 Description of the Parameter
     */
    public void paintPlayerText(Graphics2D g2) {
        String str;
        int strw;
        int strh;
        YassRectangle r = null;
        YassRow row = null;
        FontMetrics metrics = null;

        int strpos = 0;

        if (table == null) {
            return;
        }

        int i = table.getSelectionModel().getMinSelectionIndex();
        int j = table.getSelectionModel().getMaxSelectionIndex();

        if (i < 0) {
            i = nextElement();
            if (i < 0) {
                i = rect.size() - 2;
            }
        }
        if (i < 0) {
            return;
        }
        if (j < 0) {
            j = i;
        }
        int ij[] = table.enlargeToPages(i, j);
        i = ij[0];
        j = ij[1];

        if (showVideo() || showBackground()) {
            int leftx = 0;
            g2.setColor(playertextBG);
            if (live) {
                g2.fillRect(leftx, clip.height - BOTTOM_BORDER + 16,
                        clip.width, BOTTOM_BORDER - 16);
            } else {
                g2.fillRect(leftx + +LEFT_BORDER, clip.height - BOTTOM_BORDER
                                + 16, clip.width - LEFT_BORDER - RIGHT_BORDER,
                        BOTTOM_BORDER - 16);
            }
        }
        float sh = clip.height - 12;

        int k = 0;
        Enumeration<?> en = ((YassTableModel) table.getModel()).getData()
                .elements();
        Enumeration<?> ren = rect.elements();
        for (; ren.hasMoreElements() && en.hasMoreElements(); k++) {
            r = (YassRectangle) ren.nextElement();
            row = (YassRow) en.nextElement();
            if (k == i) {
                break;
            }
        }
        if (k != i || row == null) {
            return;
        }

        boolean first = true;
        int strwidth = 0;
        for (; ren.hasMoreElements() && en.hasMoreElements() && k <= j; ) {
            if (!first) {
                r = (YassRectangle) ren.nextElement();
                row = (YassRow) en.nextElement();
                k++;
            } else {
                first = false;
            }

            str = row.getText();
            if (r.isType(YassRectangle.GAP)) {
                continue;
            }
            if (r.isType(YassRectangle.START)) {
                continue;
            }
            if (r.isType(YassRectangle.END)) {
                continue;
            }
            if (r.isPageBreak()) {
                if (strwidth == 0) {
                    continue;
                }
                str = " / ";
            }
            if (str.length() < 1) {
                continue;
            }
            str = str.replace(YassRow.SPACE, ' ');

            g2.setFont(big[24]);
            g2.setColor(colorSet[1]);
            metrics = g2.getFontMetrics();
            strw = metrics.stringWidth(str);

            strwidth += strw;
        }
        if (strwidth > clip.width) {
            str = toomuchtext;
            strwidth = metrics.stringWidth(str);

            g2.setFont(big[24]);
            g2.setColor(colorSet[1]);

            float sx = clip.width / 2 - strwidth / 2;
            g2.drawString(str, sx, sh);
            return;
        }

        k = 0;
        en = ((YassTableModel) table.getModel()).getData().elements();
        for (ren = rect.elements(); ren.hasMoreElements()
                && en.hasMoreElements() && k <= j; k++) {
            r = (YassRectangle) ren.nextElement();
            row = (YassRow) en.nextElement();

            if (k < i) {
                continue;
            }

            str = row.getText();
            if (r.isType(YassRectangle.GAP)) {
                continue;
            }
            if (r.isType(YassRectangle.START)) {
                continue;
            }
            if (r.isType(YassRectangle.END)) {
                continue;
            }
            if (r.isPageBreak()) {
                if (strwidth == 0) {
                    continue;
                }
                str = " / ";
            }
            if (str.length() < 1) {
                continue;
            }
            str = str.replace(YassRow.SPACE, ' ');

            g2.setFont(big[24]);
            g2.setColor(colorSet[1]);
            metrics = g2.getFontMetrics();
            strw = metrics.stringWidth(str);
            strh = metrics.getHeight();

            int offx = 0;

            int offy = 0;
            if (isPlaying) {
                if (playerPos >= r.x && playerPos < r.x + r.width) {
                    int shade = (int) ((big.length - 1) - (big.length - 24)
                            * (playerPos - r.x) / r.width);
                    g2.setColor(colorSet[2]);
                    g2.setFont(big[shade]);
                    metrics = g2.getFontMetrics();
                    int strw2 = metrics.stringWidth(str);
                    int strh2 = metrics.getHeight();
                    offx = -(strw2 - strw) / 2;
                    offy = (strh2 - strh) / 4;
                }
            } else {
                if (playerPos >= r.x - 2 && playerPos < r.x + r.width) {
                    g2.setColor(colorSet[2]);
                }
            }

            float sx = clip.width / 2 - strwidth / 2 + strpos;
            strpos += strw;

            g2.drawString(str, sx + offx, sh + offy);
        }
    }

    /**
     * Gets the temporaryNotes attribute of the YassSheet object
     *
     * @return The temporaryNotes value
     */
    public Vector<Long> getTemporaryNotes() {
        return tmpNotes;
    }

    /**
     * Description of the Method
     */
    public void paintTemporaryNotes() {
        Graphics2D g2 = backVolImage.createGraphics();

        g2.setColor(dkRed);
        Enumeration<Long> e = tmpNotes.elements();
        int i;
        int o;
        int x1;
        int x2;
        double ms;
        double ms2;
        while (e.hasMoreElements()) {
            Long in = e.nextElement();
            i = (int) in.longValue();
            ms = i / 1000.0;
            x1 = toTimeline(ms);

            if (e.hasMoreElements()) {
                Long out = e.nextElement();
                o = (int) out.longValue();
                ms2 = o / 1000.0;
                x2 = toTimeline(ms2);
            } else {
                x2 = playerPos;
            }

            x1 = x1 - clip.x;
            x2 = x2 - clip.x;
            if (x1 < 0) {
                x1 = 0;
            }
            if (x2 >= clip.width) {
                x2 = clip.width - 1;
            }
            g2.fillRect(x1, getTopLine() - 10, x2 - x1, (int) hSize);
        }
        g2.dispose();
    }

    public void paintRecordedNotes() {
        if (session == null)
            return;

        Graphics2D g2 = backVolImage.createGraphics();

        YassTrack track = session.getTrack(0);

        Vector<YassPlayerNote> playerNotes = track.getPlayerNotes();
        int lastPlayerNote = playerNotes.size() - 1;
        if (lastPlayerNote < 0) {
            return;
        }

        g2.setStroke(medStroke);

        for (int playerNoteIndex = lastPlayerNote; playerNoteIndex >= 0; playerNoteIndex--) {
            YassPlayerNote playerNote = playerNotes.elementAt(playerNoteIndex);
            if (playerNote.isNoise()) {
                continue;
            }
            long startMillis = playerNote.getStartMillis();
            long endMillis = playerNote.getEndMillis();
            int playerHeight = playerNote.getHeight();

            int currentNote = track.getCurrentNote();
            YassNote note = track.getNote(currentNote);
            while (note.getStartMillis() >= endMillis && currentNote > 0)
                note = track.getNote(--currentNote);
            int noteHeight = note.getHeight();
            if (playerHeight < noteHeight) {
                while (Math.abs(playerHeight - noteHeight) > 6)
                    playerHeight += 12;
            } else {
                while (Math.abs(playerHeight - noteHeight) > 6)
                    playerHeight -= 12;
            }

            int x1 = toTimeline(startMillis);
            int x2 = toTimeline(endMillis);

            x1 = x1 - clip.x;
            x2 = x2 - clip.x;
            if (x1 < 0) {
                x1 = 0;
            }
            if (x2 >= clip.width) {
                x2 = clip.width - 1;
            }

            int h = pan ? (playerHeight - hhPageMin + 3) : playerHeight
                    - minHeight + 1;
            if (h <= 0)
                h += 12;
            g2.setColor(new Color(0, 120, 0, 100));
            g2.fillRoundRect(x1 + 1, (int) (dim.height - BOTTOM_BORDER - h
                    * hSize) + 1, x2 - x1 - 3, (int) (2 * hSize - 2), 10, 10);
            g2.setColor(new Color(160, 200, 160));
            g2.drawRoundRect(x1 + 1, (int) (dim.height - BOTTOM_BORDER - h
                    * hSize) + 1, x2 - x1 - 3, (int) (2 * hSize - 2), 10, 10);
        }
        g2.dispose();

        // StringBuffer sb = new StringBuffer();
        // for (YassPlayerNote playerNote: playerNotes) {
        // if (playerNote.isNoise())
        // continue;
        // int height = playerNote.getHeight();
        // sb.append((height)+", ");
        // }
        // System.out.println(sb.toString());
    }

    /**
     * Sets the message attribute of the YassSheet object
     *
     * @param s The new message value
     */
    public void setMessage(String s) {
    }

    /**
     * Sets the messageInfo attribute of the YassSheet object
     *
     * @param s The new messageInfo value
     */
    public void setErrorMessage(String s) {
        message = s;
    }

    /**
     * Description of the Method
     *
     * @param g2 Description of the Parameter
     */
    public void paintMessage(Graphics2D g2) {
        if (message == null || message.length() < 1) {
            return;
        }

        g2.setFont(big[19]);
        FontMetrics metrics = g2.getFontMetrics();
        metrics.stringWidth(message);
        metrics.getHeight();
        g2.setColor(Color.blue);
        g2.drawString(message, clip.x + 4, 2 + metrics.getAscent());
    }

    /**
     * Description of the Method
     * @param t    Description of the Parameter
     * @param i    Description of the Parameter
     * @param prev Description of the Parameter
     * @param r    Description of the Parameter
     * @param rr   Description of the Parameter
     */
    private void updateFromRow(YassTable t, int i, YassRow prev,
                               YassRow r, YassRectangle rr) {
        double timelineGap = t.getGap() * 4 / (60 * 1000 / t.getBPM());

        if (r.isNote()) {
            int pageMin = r.getHeightInt();
            if (pan) {
                int j = i - 1;
                YassRow p = t.getRowAt(j);
                while (p.isNote()) {
                    pageMin = Math.min(pageMin, p.getHeightInt());
                    p = t.getRowAt(--j);
                }
                j = i + 1;
                p = t.getRowAt(j);
                while (p != null && p.isNote()) {
                    pageMin = Math.min(pageMin, p.getHeightInt());
                    p = t.getRowAt(++j);
                }
            }
            int beat = r.getBeatInt();
            int length = r.getLengthInt();
            int height = r.getHeightInt();
            rr.x = (timelineGap + beat) * wSize + 1;
            if (paintHeights) {
                rr.x += heightBoxWidth;
            }

            if (pan) {
                rr.y = dim.height - (height - pageMin + 2) * hSize - hSize
                        - BOTTOM_BORDER + 1;
            } else {
                rr.y = dim.height - (height - minHeight) * hSize - hSize
                        - BOTTOM_BORDER + 1;
            }
            rr.width = length * wSize - 2;
            if (rr.width < 1) {
                rr.width = 1;
            }
            rr.height = 2 * hSize - 2;
            rr.setPageMin(pan ? pageMin : minHeight);
            if (r.hasMessage()) {
                rr.setType(YassRectangle.WRONG);
            } else if (r.isGolden()) {
                rr.setType(YassRectangle.GOLDEN);
            } else if (r.isFreeStyle()) {
                rr.setType(YassRectangle.FREESTYLE);
            } else {
                rr.resetType();
            }

            if (prev != null && prev.isPageBreak()) {
                rr.addType(YassRectangle.FIRST);
            }
        } else if (r.isPageBreak()) {
            int beat = r.getBeatInt();
            int beat2 = r.getSecondBeatInt();
            int length = beat2 - beat;
            rr.x = (timelineGap + beat) * wSize + 2;
            if (paintHeights) {
                rr.x += heightBoxWidth;
            }

            rr.width = (length == 0) ? wSize / 4.0 : length * wSize - 2;
            if (pan) {
                rr.height = (2 * NORM_HEIGHT / 2 - 1) * hSize;
            } else {
                rr.height = (2 * maxHeight / 2 - 1 - minHeight) * hSize;
            }
            rr.y = dim.height - BOTTOM_BORDER - rr.height;
            if (r.hasMessage()) {
                rr.setType(YassRectangle.WRONG);
            } else {
                rr.resetType();
            }
            rr.setPageNumber(0);
        } else if (r.isComment() && r.getCommentTag().equals("GAP:")) {
            // choose correct index i
            rr.x = timelineGap * wSize - 10;
            if (paintHeights) {
                rr.x += heightBoxWidth;
            }

            rr.width = 20;
            rr.height = 9;
            rr.y = 0;
            rr.setType(YassRectangle.GAP);
        } else if (r.isComment()
                && (r.getCommentTag().equals("START:") || r.getCommentTag()
                .equals("TITLE:"))) {
            double start = t.getStart() * 4 / (60 * 1000 / t.getBPM());

            // choose correct index i
            rr.x = start * wSize;
            if (paintHeights) {
                rr.x += heightBoxWidth;
            }

            rr.width = 10;
            rr.height = 18;
            rr.y = 21;
            rr.setType(YassRectangle.START);
        } else if (r.isEnd()) {
            double end = t.getEnd();
            if (end < 0 || end > duration) {
                end = duration;
            }
            end = end * 4 / (60 * 1000 / t.getBPM());

            // choose correct index i
            rr.x = end * wSize - 5;
            if (paintHeights) {
                rr.x += heightBoxWidth;
            }

            rr.width = 10;
            rr.height = 18;
            rr.y = 21;
            rr.setType(YassRectangle.END);
        } else if (r.isComment()) {
            rr.setType(YassRectangle.HEADER);
        }
        // imageChanged=true;
    }

    /**
     * Description of the Method
     *
     * @param onoff Description of the Parameter
     */
    public void enablePan(boolean onoff) {
        pan = onoff;
        updateHeight();
        revalidate();
    }

    /**
     * Gets the panEnabled attribute of the YassSheet object
     *
     * @return The panEnabled value
     */
    public boolean isPanEnabled() {
        return pan;
    }

    /**
     * Gets the heightRange attribute of the YassSheet object
     *
     * @return The heightRange value
     */
    public int[] getHeightRange() {
        int minH = 128;
        int maxH = -128;

        Enumeration<YassTable> et = tables.elements();
        for (Enumeration<Vector<YassRectangle>> e = rects.elements(); e
                .hasMoreElements() && et.hasMoreElements(); ) {
            e.nextElement();
            YassTable t = et.nextElement();
            int n = t.getRowCount();
            YassRow row;
            for (int i = 0; i < n; i++) {
                row = t.getRowAt(i);
                if (row.isNote()) {
                    int height = row.getHeightInt();
                    minH = Math.min(minH, height);
                    maxH = Math.max(maxH, height);
                }
            }
        }
        if (minH == 128) {
            minH = 0;
        }

        int maxHeight = maxH + 3;// < 0 ? (int) (maxH / 12) * 12 : (int) (maxH /
        // 12 + 1) * 12;
        int minHeight = minH - 1;// < 0 ? (int) (minH / 12 - 1) * 12 : (int)
        // (minH / 12) * 12;

        if (maxHeight - minHeight < 19) {
            maxHeight = minHeight + 19;
        }

        // 10 lines
        // maxH = maxH < 0 ? (int)(maxH / 10)*10 : (int)(maxH / 10+1)*10;
        // minH = minH < 0 ? (int)(minH / 10-1)*10 : (int)(minH / 10)*10;
        // if (maxH-minH <17) maxH=minH+17;

        return new int[]{minHeight, maxHeight};
    }

    /**
     * Description of the Method
     */
    public void init() {
        firePropertyChange("play", null, "stop");

        int maxwait = 10;
        while (isRefreshing() && maxwait-- > 0) {
            try {
                Thread.currentThread();
                Thread.sleep(100);
            } catch (Exception ignored) {
            }
        }

        int[] minmax = getHeightRange();
        minHeight = minmax[0];
        maxHeight = minmax[1];

        Enumeration<YassTable> et = tables.elements();
        for (Enumeration<Vector<YassRectangle>> e = rects.elements(); e
                .hasMoreElements() && et.hasMoreElements(); ) {
            Vector<YassRectangle> r = e.nextElement();
            YassTable t = et.nextElement();

            // System.out.println("Sheet-Init start singleton");
            // synchronized (YassSingleton.getInstance()) {
            r.removeAllElements();
            int n = t.getRowCount();
            for (int i = 0; i < n; i++) {
                r.addElement(new YassRectangle());
            }
            // }
            // System.out.println("Sheet-Init finish singleton");
        }

        if (isValid()) {
            updateHeight();
            update();
            repaint();
        }
    }

    /**
     * Description of the Method
     */
    public void updateHeight() {
        if (dim == null || getParent() == null) {
            return;
        }

        dim.setSize(dim.width, getParent().getSize().height);
        if (pan) {
            hSize = (dim.height - BOTTOM_BORDER - 30)
                    / (double) (NORM_HEIGHT - 2);
        } else {
            hSize = (dim.height - BOTTOM_BORDER - 30)
                    / (double) (maxHeight - minHeight - 2);
        }
        if (hSize > 16) {
            hSize = 16;
        }

        TOP_LINE = 0;
        if (pan) {
            TOP_LINE = dim.height - BOTTOM_BORDER + 10
                    - (int) (hSize * (NORM_HEIGHT - 2));
        } else {
            TOP_LINE = dim.height - BOTTOM_BORDER + 10
                    - (int) (hSize * (maxHeight - minHeight - 2));
        }
    }

    /**
     * Description of the Method
     */
    public void update() {
        updateHeight();

        if (table != null) {
            gap = table.getGap();
            bpm = table.getBPM();
            beatgap = gap * 4 / (60 * 1000 / bpm);
        }

        outgap = 0;
        Enumeration<YassTable> et = tables.elements();
        for (Enumeration<Vector<YassRectangle>> e = rects.elements(); e
                .hasMoreElements() && et.hasMoreElements(); ) {
            Vector<YassRectangle> r = e.nextElement();
            YassTable t = et.nextElement();

            int i = 0;

            int pn = 1;
            Enumeration<?> ren = r.elements();
            Enumeration<?> ten = ((YassTableModel) t.getModel()).getData()
                    .elements();
            YassRow row = null;
            YassRow prev;
            YassRow next = null;
            while (ren.hasMoreElements()) {
                prev = row;
                if (next != null) {
                    row = next;
                    next = ten.hasMoreElements() ? (YassRow) ten.nextElement()
                            : null;
                } else {
                    row = (YassRow) ten.nextElement();
                }
                if (next == null) {
                    next = ten.hasMoreElements() ? (YassRow) ten.nextElement()
                            : null;
                }

                if (row.isNote()) {
                    outgap = Math.max(outgap,
                            row.getBeatInt() + row.getLengthInt());
                } else if (row.isPageBreak()) {
                    outgap = Math.max(outgap, row.getSecondBeatInt());
                }
                YassRectangle rr = (YassRectangle) ren.nextElement();
                updateFromRow(t, i++, prev, row, rr);
                if (rr.isPageBreak()) {
                    rr.setPageNumber(++pn);
                    // should better add PAGE_BREAK type
                    rr.removeType(YassRectangle.DEFAULT);
                }
            }
        }
    }

    public void setHNoteEnabled(boolean b) {
        if (b)
            actualNoteTable = hNoteTable;
        else
            actualNoteTable = bNoteTable;
    }

    /**
     * Description of the Method
     */
    public void updateActiveTable() {
        if (table == null) {
            return;
        }

        gap = table.getGap();
        bpm = table.getBPM();
        beatgap = gap * 4 / (60 * 1000 / bpm);

        int i = 0;

        int pn = 1;
        Enumeration<?> ren = rect.elements();
        Enumeration<?> ten = ((YassTableModel) table.getModel()).getData()
                .elements();
        YassRow row = null;
        YassRow prev;
        YassRow next = null;
        while (ren.hasMoreElements() && ten.hasMoreElements()) {
            prev = row;
            if (next != null) {
                row = next;
                next = ten.hasMoreElements() ? (YassRow) ten.nextElement()
                        : null;
            } else {
                row = (YassRow) ten.nextElement();
            }
            if (next == null) {
                next = ten.hasMoreElements() ? (YassRow) ten.nextElement()
                        : null;
            }

            if (row.isNote()) {
                outgap = Math
                        .max(outgap, row.getBeatInt() + row.getLengthInt());
            } else if (row.isPageBreak()) {
                outgap = Math.max(outgap, row.getSecondBeatInt());
            }
            YassRectangle rr = (YassRectangle) ren.nextElement();
            updateFromRow(table, i++, prev, row, rr);
            if (rr.isPageBreak()) {
                rr.setPageNumber(++pn);
            }
        }
    }

    /**
     * Gets the playerPosition attribute of the YassSheet object
     *
     * @return The playerPosition value
     */
    public int getPlayerPosition() {
        return playerPos;
    }

    /**
     * Sets the playerPosition attribute of the YassSheet object
     *
     * @param x The new playerPosition value
     */
    public void setPlayerPosition(int x) {
        //new Exception("playerpos ="+x).printStackTrace();

        playerPos = x;

        // Rectangle w = ((JViewport)getParent()).getViewRect();
		/*
		 * if (playerPos < clip.x || playerPos > clip.x+clip.width) {
		 * table.zoomPage(); scrollRectToVisible(new
		 * Rectangle(playerPos,0,clip.width-1,clip.height-1));
		 * imageChanged=true; refreshImage(); }
		 */
    }

    /**
     * Gets the inSelect attribute of the YassSheet object
     *
     * @return The inSelect value
     */
    public long getInSnapshot() {
        return inSnapshot;
    }

    /**
     * Gets the outSelect attribute of the YassSheet object
     *
     * @return The outSelect value
     */
    public long getOutSnapshot() {
        return outSnapshot;
    }

    /**
     * Sets the paintHeights attribute of the YassSheet object
     *
     * @param onoff The new paintHeights value
     */
    public void setPaintHeights(boolean onoff) {
        paintHeights = onoff;
    }

    /**
     * Description of the Method
     *
     * @param ms Description of the Parameter
     * @return Description of the Return Value
     */
    public int toTimeline(double ms) {
        int x = (int) (4 * bpm * ms / (60 * 1000) * wSize + .5);
        if (paintHeights) {
            x += heightBoxWidth;
        }
        return x;
    }

    /**
     * Description of the Method
     *
     * @param x Description of the Parameter
     * @return Description of the Return Value
     */
    public long fromTimeline(double x) {
        if (paintHeights) {
            x -= heightBoxWidth;
        }
        return (long) (x * 60 * 1000L / (4.0 * bpm * wSize) + .5);
    }

    /**
     * Description of the Method
     *
     * @param x Description of the Parameter
     * @return Description of the Return Value
     */
    public double fromTimelineExact(double x) {
        if (paintHeights) {
            x -= heightBoxWidth;
        }
        return x * 60 * 1000L / (4.0 * bpm * wSize);
    }

    /**
     * Description of the Method
     *
     * @param beat Description of the Parameter
     * @return Description of the Return Value
     */
    public int beatToTimeline(int beat) {
        int x = (int) ((beatgap + beat) * wSize + .5);
        if (paintHeights) {
            x += heightBoxWidth;
        }
        return x;
    }

    /**
     * Description of the Method
     *
     * @param ms Description of the Parameter
     * @return Description of the Return Value
     */
    public int toBeat(double ms) {
        return (int) ((ms - gap) * 4 * bpm / (60 * 1000));
    }

    /**
     * Description of the Method
     *
     * @return Description of the Return Value
     */
    public int nextElement() {
        return nextElement(playerPos);
    }

    /**
     * Description of the Method
     *
     * @param pos Description of the Parameter
     * @return Description of the Return Value
     */
    public int nextElement(int pos) {
        YassRectangle r;
        int i = 0;
        for (Enumeration<?> e = rect.elements(); e.hasMoreElements(); i++) {
            r = (YassRectangle) e.nextElement();
            if (r.x - 1 >= pos || r.x + r.width >= pos) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Description of the Method
     *
     * @return Description of the Return Value
     */
    public int firstVisibleElement() {
        int x = clip.x + LEFT_BORDER;
        if (paintHeights) {
            x += heightBoxWidth;
        }
        return nextElement(x);
    }

    /**
     * Sets the viewToNextPage attribute of the YassSheet object
     */
    public void setViewToNextPage() {
        table.gotoPage(1);
    }

    /**
     * Gets the duration attribute of the YassSheet object
     *
     * @return The duration value
     */
    public double getDuration() {
        return duration;
    }

    // equivalent to

    // sheetPane.getHorizontalScrollBar().setUnitIncrement(30);
    // sheetPane.getHorizontalScrollBar().setBlockIncrement(150);

    /**
     * Sets the duration attribute of the YassSheet object
     *
     * @param ms The new duration value
     */
    public void setDuration(double ms) {
        if (ms <= 0) {
            ms = 10000L;
        }
        duration = ms;
        dim.setSize(toTimeline(duration), dim.height);
        setSize(dim);
    }

    /**
     * Gets the beatSize attribute of the YassSheet object
     *
     * @return The beatSize value
     */
    public double getBeatSize() {
        return wSize;
    }

    /**
     * Sets the beatSize attribute of the YassSheet object
     *
     * @param w The new beatSize value
     */
    public void setBeatSize(double w) {
        wSize = w;
        update();
    }

    /**
     * Sets the zoom attribute of the YassSheet object
     *
     * @param w The new zoom value
     */
    public void setZoom(double w) {
        wSize = w;
        dim.setSize(toTimeline(duration), dim.height);
        setSize(dim);

        update();
        if (table != null) {
            int i = table.getSelectionModel().getMinSelectionIndex();
            int j = table.getSelectionModel().getMaxSelectionIndex();
            if (i >= 0) {
                scrollRectToVisible(i, j);
            }
        }
        repaint();
    }

    /**
     * Sets the zoom attribute of the YassSheet object
     *
     * @param i     The new zoom value
     * @param j     The new zoom value
     * @param force The new zoom value
     */
    public void setZoom(int i, int j, boolean force) {
        if (table == null) {
            return;
        }

        int min = Integer.MAX_VALUE;

        int max = Integer.MIN_VALUE;
        int beat = min;
        int end = max;

        for (int k = i; k <= j; k++) {
            YassRow r = table.getRowAt(k);

            if (r.isNote()) {
                beat = r.getBeatInt();
                end = beat + r.getLengthInt();
            } else if (r.isPageBreak()) {
                end = r.getSecondBeatInt();
            } else if (r.isComment() && !r.getCommentTag().equals("END:")) {
                beat = toBeat(0);
                // table.getStart()
                if (r.getCommentTag().equals("GAP:")) {
                    end = 0;
                }
            } else if (r.isEnd()) {
                beat = Math.max(outgap - 1, 0);

                double b = table.getEnd();
                if (b < 0) {
                    b = duration;
                }
                end = toBeat(b);
            }

            min = Math.min(min, beat);
            max = Math.max(max, end);
        }
        if (min == Integer.MAX_VALUE) {
            return;
        }

        // quick hack to get actual size on screen
        int d = ((JViewport) getParent()).getExtentSize().width - 2;

        if (d < 0) {
            System.out.println("warning: invalid sheet width");
        }

        d -= LEFT_BORDER + RIGHT_BORDER;

        // clip.width -2 ; //
        if (paintHeights) {
            d -= heightBoxWidth;
        }

        double val = min == max ? d : d / (double) (max - min);

        if (force || val < wSize) {
            // adjust wSize
            wSize = val;
            dim.setSize(toTimeline(duration), dim.height);
            setSize(dim);

            if (isVisible()) {
                validate();
                update();
            }
        }
        scrollRectToVisible(i, j);
    }

    // //////////////////////// PLAYBACK RENDERER

    /**
     * Gets the preferredSize attribute of the YassSheet object
     *
     * @return The preferredSize value
     */
    public Dimension getPreferredSize() {
        return dim;
    }

    /**
     * Gets the preferredScrollableViewportSize attribute of the YassSheet
     * object
     *
     * @return The preferredScrollableViewportSize value
     */
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    /**
     * Gets the scrollableUnitIncrement attribute of the YassSheet object
     *
     * @param visibleRect Description of the Parameter
     * @param orientation Description of the Parameter
     * @param direction   Description of the Parameter
     * @return The scrollableUnitIncrement value
     */
    public int getScrollableUnitIncrement(Rectangle visibleRect,
                                          int orientation, int direction) {
        if (orientation == SwingConstants.HORIZONTAL) {
            return (int) wSize;
        }
        return 1;
    }

    /**
     * Gets the scrollableBlockIncrement attribute of the YassSheet object
     *
     * @param visibleRect Description of the Parameter
     * @param orientation Description of the Parameter
     * @param direction   Description of the Parameter
     * @return The scrollableBlockIncrement value
     */
    public int getScrollableBlockIncrement(Rectangle visibleRect,
                                           int orientation, int direction) {
        if (orientation == SwingConstants.HORIZONTAL) {
            return (int) (5 * wSize);
        }
        return 10;
    }

    /**
     * Gets the scrollableTracksViewportWidth attribute of the YassSheet object
     *
     * @return The scrollableTracksViewportWidth value
     */
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    /**
     * Gets the scrollableTracksViewportHeight attribute of the YassSheet object
     *
     * @return The scrollableTracksViewportHeight value
     */
    public boolean getScrollableTracksViewportHeight() {
        return true;
    }

    /**
     * Gets the availableAcceleratedMemory attribute of the YassSheet object
     *
     * @return The availableAcceleratedMemory value
     */
    public int getAvailableAcceleratedMemory() {
        GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        try {
            GraphicsDevice[] gs = ge.getScreenDevices();

            // Get current amount of available memory in bytes for each screen
            for (GraphicsDevice g : gs) {
                // Workaround; see description
                VolatileImage im = g.getDefaultConfiguration()
                        .createCompatibleVolatileImage(1, 1);

                // Retrieve available free accelerated image memory
                int bytes = g.getAvailableAcceleratedMemory();

                // Release the temporary volatile image
                im.flush();

                return bytes;
            }
        } catch (HeadlessException e) {
            // Is thrown if there are no screen devices
        }
        return 0;
    }

    /**
     * Gets the iD attribute of the YassSheet object
     *
     * @return The iD value
     */
    public String getID() {
        return "sheet";
    }

    /**
     * Description of the Method
     *
     * @param s Description of the Parameter
     * @param p Description of the Parameter
     * @param t Description of the Parameter
     */
    public void init(yass.renderer.YassSession s, yass.screen.YassTheme t,
                     YassProperties p) {
        session = s;
    }

    /**
     * Gets the session attribute of the YassSheet object
     *
     * @return The session value
     */
    public yass.renderer.YassSession getSession() {
        return session;
    }

    /**
     * Sets the videoFrame attribute of the YassSheet object
     *
     * @param img The new videoFrame value
     */
    public void setVideoFrame(BufferedImage img) {
        videoFrame = img;
    }

    /**
     * Gets the playbackInterrupted attribute of the PlaybackRenderer object
     *
     * @return The playbackInterrupted value
     */
    public boolean isPlaybackInterrupted() {
        return pisinterrupted;
    }

    /**
     * Sets the playbackInterrupted attribute of the YassSheet object
     *
     * @param onoff The new playbackInterrupted value
     */
    public void setPlaybackInterrupted(boolean onoff) {
        pisinterrupted = onoff;
    }

    /**
     * Description of the Method
     *
     * @param inpoint_ms  Description of the Parameter
     * @param endpoint_ms Description of the Parameter
     * @return Description of the Return Value
     */
    public boolean preparePlayback(long inpoint_ms, long endpoint_ms) {
        Graphics2D pg2 = (Graphics2D) getGraphics();
        if (pg2 == null) {
            return false;
        }
        pg2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);
        // g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
        // RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        // g2.setRenderingHint(RenderingHints.KEY_RENDERING,
        // RenderingHints.VALUE_RENDER_SPEED);

        pgb = getBackBuffer().createGraphics();
        // gb.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        // RenderingHints.VALUE_ANTIALIAS_OFF);
        // gb.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
        // RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        // gb.setRenderingHint(RenderingHints.KEY_RENDERING,
        // RenderingHints.VALUE_RENDER_SPEED);

        ppos = playerPos;
        playerPos = -1;
        setPlaying(true);

        psheetpos = getViewPosition();

        int maxwait = 10;
        while (isRefreshing() && maxwait-- > 0) {
            try {
                Thread.currentThread();
                Thread.sleep(10);
            } catch (Exception ignored) {
            }
        }

        // stalls sometimes in c.print:
        // sheet.refreshImage();
        return true;
    }

    /**
     * Description of the Method
     */
    public void startPlayback() {
    }

    /**
     * Description of the Method
     *
     * @param pos_ms Description of the Parameter
     */
    public void updatePlayback(long pos_ms) {
        int newPlayerPos = toTimeline(pos_ms);

        if (newPlayerPos <= playerPos) {
            return;
        }
        playerPos = newPlayerPos;

        if (playerPos > clip.x + clip.width) {
            setTemporaryStop(true);
            setPlaying(false);

            if (live) {
                setViewToNextPage();
                playerPos = toTimeline(pos_ms);
            } else {
                Point p = getViewPosition();
                p.x += clip.width;
                setViewPosition(p);
            }

            paintComponent(pgb);
            setPlaying(true);
            setTemporaryStop(false);
        }
        if (isPlaybackInterrupted()) {
            return;
        }

        if (!isRefreshing()) {
            VolatileImage plain = getPlainBuffer();

            if (showVideo()) {
                BufferedImage img = videoFrame;
                if (img != null) {
                    int w = plain.getWidth();
                    int h = plain.getHeight();
                    int hh = (int) (w * 3 / 4.0);
                    int yy = h / 2 - hh / 2;

                    pgb.setColor(Color.white);
                    pgb.fillRect(0, 0, w, yy);
                    pgb.fillRect(0, yy, w, h);
                    pgb.drawImage(img, 0, yy, w, hh, null);
                } else {
                    pgb.drawImage(plain, 0, 0, null);
                }
                pgb.translate(-clip.x, 0);
                paintLines(pgb);
                paintPlainRectangles(pgb);
                pgb.translate(clip.x, 0);
            } else if (showBackground()) {
                BufferedImage img = getBackgroundImage();
                if (img != null) {
                    int w = plain.getWidth();
                    int h = plain.getHeight();
                    int hh = (int) (w * 3 / 4.0);
                    int yy = h / 2 - hh / 2;

                    pgb.setColor(Color.white);
                    pgb.fillRect(0, 0, w, yy);
                    pgb.fillRect(0, yy, w, h);
                    pgb.drawImage(img, 0, yy, w, hh, null);
                } else {
                    pgb.drawImage(plain, 0, 0, null);
                }
                pgb.translate(-clip.x, 0);
                paintLines(pgb);
                paintPlainRectangles(pgb);
                pgb.translate(clip.x, 0);
            } else {
                    int top = getTopLine() - 10;
                    int w = plain.getWidth();
                    int h = plain.getHeight() - top;
                    pgb.drawImage(plain, 0, top, w, top + h, 0, top, w,
                            top + h, null);
            }

            if (getPlainBuffer().contentsLost()) {
                setErrorMessage(bufferlost);
            }
            if (isPlaybackInterrupted()) {
                return;
            }

            paintText(pgb);
            paintPlayerText(pgb);
            paintPlayerPosition(pgb, true);

            if (playerPos < clip.x) {
                paintWait(pgb, (int) fromTimeline(clip.x - playerPos));
            }

            paintTemporaryNotes();
            paintRecordedNotes();

            Graphics2D pg2 = (Graphics2D) getGraphics();
            if (!showVideo()) {
                int top = getTopLine() - 10;
                int w = plain.getWidth();
                int h = plain.getHeight() - top;
                paintBackBuffer(pg2, 0, top, w, top + h);
            } else {
                paintBackBuffer(pg2);
            }
        }
    }

    /**
     * Description of the Method
     */
    public void finishPlayback() {
        Graphics2D pg2 = (Graphics2D) getGraphics();
        pg2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        if (isLive()) {
            previewEdit(false);
            showVideo(false);
        }
        showBackground(false);

        setLyricsVisible(true);
        if (!isLive()) {
            setViewPosition(psheetpos);
            setPlayerPosition(ppos);
        }
        setLive(false);
        setPlaying(false);
        repaint();
    }

    /**
     * Gets the component attribute of the YassSheet object
     *
     * @return The component value
     */
    public JComponent getComponent() {
        return this;
    }

    /**
     * Sets the pause attribute of the YassSheet object
     *
     * @param r The new ratio value
     */
    public void setRatio(int r) {
    }

    /**
     * Sets the pause attribute of the YassSheet object
     *
     * @param onoff The new pause value
     */
    public void setPause(boolean onoff) {
    }

}
