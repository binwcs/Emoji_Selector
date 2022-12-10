package component;

import model.EmoInfo;
import msg.MsgType;
import msg.Sender;
import model.FontAndText;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.*;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Chat Windows
 *
 * @author Bin Wang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class ChatFrame extends JFrame implements MouseListener, Runnable {
    private static final long serialVersionUID = 1L;
    private static final Color TIP_COLOR = new Color(255, 255, 225);
    public final int F_WIDTH = 550;
    public final int F_HEIGHT = 500;
    /* Left and Right Windows */
    public JLabel left = new JLabel();
    JScrollPane jspChat;
    JScrollPane jspMsg;
    /**
     * Add new Message to Chat
     */
    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    FontAndText dateFont = new FontAndText("", "Song", 20, Color.BLACK);
    /**
     * insert emoji
     *
     */
    int pos1;
    int pos2;
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    /*Chat Context*/
    private JTextPane jpChat;
    /*Send Context*/
    private JTextPane jpMsg;
    /* the font of Insert Text*/
    private StyledDocument docChat = null;
    private StyledDocument docMsg = null;
    private JButton btnSend;
    private JComboBox<String> fontName = null;
    private JComboBox<String> fontSize = null;
    private JComboBox<String> fontColor = null;
    /*insert button;clear button; emoji button*/
    private JButton b_shake = null, b_emo, b_remove = null;
    /* Error Text*/
    private ToolAndTips error_tip;
    /*Emoji Windows*/
    private EmosWindow emoWindow;
    private final List<EmoInfo> myEmoInfo = new LinkedList<>();
    private final List<EmoInfo> receiveEmoInfo = new LinkedList<>();
    /**
     * Send Message
     */
    private FontAndText myFont = null;

    public ChatFrame() {
        init();
    }

    public static void setUIFont(FontUIResource f) {
	/*	sets the default font for all Swing components.
		ex.setUIFont (new
		javax.swing.plaf.FontUIResource("Serif",Font.ITALIC,12));*/
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource)
                UIManager.put(key, f);
        }
    }

    public static void main(String[] args) {
        /*	 UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
         */     /* Consistent with the look and feel of the system */
        setUIFont(new FontUIResource("宋体", Font.PLAIN, 15));
        new ChatFrame().setVisible(true);
    }

    /**
     * Init Windows
     */
    private void init() {
        setLayout(new BorderLayout());
        /*this.setUndecorated(true);*/
        /*		getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
         */
        setSize(F_WIDTH, F_HEIGHT);
        this.setMinimumSize(new Dimension(F_WIDTH, F_HEIGHT));
        this.getContentPane().setBackground(Color.GRAY);
        setResizable(false);
        setLocationRelativeTo(null);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(1);
            }

        });
        this.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                ChatFrame.this.emoWindow.dispose();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                ChatFrame.this.emoWindow.dispose();
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                ChatFrame.this.emoWindow.dispose();
            }

        });
        /* Windows On Top */
        setAlwaysOnTop(true);
        /* Chat Windows */
        jpChat = new JTextPane();
        jpChat.addMouseListener(this);
        jpChat.setEditable(false);
        jspChat = new JScrollPane(jpChat,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        /* Text Windows */
        jpMsg = new JTextPane();
        jpMsg.addMouseListener(this);
        jspMsg = new JScrollPane(jpMsg,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        jspMsg.setPreferredSize(new Dimension(100, 100));
        error_tip = new ToolAndTips(this, jspMsg, TIP_COLOR, 3, 10);

        /* Send button */
        btnSend = new JButton("Send");
        btnSend.setFocusable(false);
        /* Get the Document of JTextPane to set the font */
        docChat = jpChat.getStyledDocument();
        docMsg = jpMsg.getStyledDocument();

        /* Add mouse event listener */
        btnSend.addMouseListener(this);
        /* font area */
        JLabel lblSend = new JLabel();
        lblSend.setLayout(new FlowLayout(FlowLayout.RIGHT));
        String[] str_name = {"Song", "Black", "Dialog", "Gulim"};
        String[] str_Size = {"12", "14", "18", "22", "30", "40"};
        String[] str_Color = {"Black", "Red", "Blue", "Yellow", "Green"};
        fontName = new JComboBox<>(str_name);
        fontSize = new JComboBox<>(str_Size);
        fontColor = new JComboBox<>(str_Color);
        Box box = Box.createHorizontalBox();
        box.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        box.add(new JLabel("Font:"));
        box.add(fontName);

        box.add(Box.createHorizontalStrut(3));
        box.add(new JLabel("Size:"));
        box.add(fontSize);
        box.add(Box.createHorizontalStrut(3));
        box.add(new JLabel("Color:"));
        box.add(fontColor);
        box.add(Box.createHorizontalStrut(3));
        box.add(btnSend);

        JPanel paneLeftSouth = new JPanel();
        paneLeftSouth.setLayout(new BorderLayout());

        b_emo = new JButton("Emoji");
        b_emo.setFocusable(false);
        b_shake = new JButton("Shake");
        b_shake.setFocusable(false);
        b_remove = new JButton("Clear");
        b_remove.setFocusable(false);
        emoWindow = new EmosWindow(this);
        b_emo.addMouseListener(this);
        b_remove.addMouseListener(this);
        b_shake.addMouseListener(this);
        Box box_1 = Box.createHorizontalBox();
        box_1.add(b_emo);
        box_1.add(b_shake);
        box_1.add(b_remove);

        paneLeftSouth.add(box_1, BorderLayout.NORTH);//font, emoticon, shake
        paneLeftSouth.add(jspMsg, BorderLayout.CENTER);
        paneLeftSouth.add(box, BorderLayout.SOUTH);
        paneLeftSouth.setBackground(Color.CYAN);

        left.setLayout(new BorderLayout());
        left.setOpaque(false);
        left.add(jspChat, BorderLayout.CENTER);
        left.add(paneLeftSouth, BorderLayout.SOUTH);
        add(left, BorderLayout.CENTER);
        new Thread(this).start();
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        error_tip.setVisible(false);
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        emoWindow.setVisible(false);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (getY() <= 0) {
            setLocation(getX(), 0);
        }
        if (e.getButton() != 1)
            return;/*not left button*/

        JComponent source = (JComponent) e.getSource();
        /*When the mouse is released in the event source, it responds to the click event*/
        if (e.getX() >= 0 && e.getX() <= source.getWidth() && e.getY() >= 0
                && e.getY() <= source.getHeight()) {
            if (source == btnSend) {
                sendMsg();
            } else if (source == this.b_shake) {
                sendShake();
            } else if (source == this.b_emo) {
                emoWindow.setVisible(true);
            } else if (source == this.b_remove) {
                jpChat.setText("");
                jpChat.revalidate();
            }
        }
    }

    /**
     * Send a shake to a friend
     */
    public void sendShake() {
        String uname = Sender.localIP + ":" + Sender.SendPort;
        if (!Sender.sendUDPMsg(MsgType.SHAKE, uname, Sender.localIP, Sender.SendPort, "shake")) {
            error_tip.setText("");
            error_tip.setVisible(true);
        }
        insert("You " + uname + " Send a Shake");
    }

    /**
     * shake for more than three seconds
     *
     */
    public void shake(String uname) {
        setExtendedState(Frame.NORMAL);
        setVisible(true);
        insert(uname + " ");
        new Thread() {
            final long begin = System.currentTimeMillis();
            long end = System.currentTimeMillis();
            final Point p = ChatFrame.this.getLocationOnScreen();

            public void run() {
                int i = 1;
                while ((end - begin) / 1000 < 3) {
                    ChatFrame.this.setLocation(new Point((int) p.getX() - 5 * i, (int) p.getY() + 5 * i));
                    end = System.currentTimeMillis();
                    try {
                        Thread.sleep(5);
                        i = -i;
                        ChatFrame.this.setLocation(p);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public void sendMsg() {
        String message = jpMsg.getText();
        if (message.length() == 0) {
            error_tip.setText("Enter your message");
            error_tip.setVisible(true);
            return;
        }
        if (message.length() > 100) {
            error_tip.setText("Message Maximum" + message.length() + "");
            error_tip.setVisible(true);
            return;
        }

        String uname = Sender.localIP + ":" + Sender.chatPort;
        myFont = getFontAttrib();
        if (Sender.sendUDPMsg(MsgType.CHAT, uname, Sender.localIP, Sender.SendPort,
                myFont.toString())) {
            addMeg(uname);
            this.jpMsg.setText("");
        } else {
            error_tip.setText("");
            error_tip.setVisible(true);
        }
    }

    public void addMeg(String uname) {
        String msg = uname + " " + sf.format(new Date());
        dateFont.setText(msg);
        insert(dateFont);
        pos2 = jpChat.getCaretPosition();
        myFont.setText(jpMsg.getText());
        insert(myFont);
        insertEmo(false);
    }

    public void addRecMsg(String uname, String message) {
        setExtendedState(Frame.NORMAL);
        //setVisible(true);
        String msg = uname + " " + sf.format(new Date());
        dateFont.setText(msg);
        insert(dateFont);/*time and user information*/
        int index = message.lastIndexOf("*");

        System.out.println("index=" + index);
        pos1 = jpChat.getCaretPosition();
        if (index > 0 && index < message.length() - 1) { /*There is expression information*/
            FontAndText attr = getReceiveFont(message.substring(0, index));
            insert(attr);
            receivedEmoInfo(message.substring(index + 1));
            insertEmo(true);
        } else {
            FontAndText attr = getReceiveFont(message);
            insert(attr);
        }
    }

    /**
     * insert Emoji
     *
     */
    public void insertSendEmo(ImageIcon imgIc) {
        jpMsg.insertIcon(imgIc); // insert Emoji
        System.out.print(imgIc.toString());
    }

    /*
     * Reorganize the received emoticon message string
     */
    public void receivedEmoInfo(String picInfos) {
        String[] infos = picInfos.split("[+]");
        for (String info : infos) {
            String[] tem = info.split("&");
            if (tem.length == 2) {
                EmoInfo pic = new EmoInfo(Integer.parseInt(tem[0]), tem[1]);
                receiveEmoInfo.add(pic);
            }
        }
    }

    /**
     * Reorganize sent emoticons
     *
     */
    private String buildEmoInfo() {
        StringBuilder sb = new StringBuilder();
        //Traversing the jtextpane to find all the image information encapsulated into the specified format
        for (int i = 0; i < this.jpMsg.getText().length(); i++) {
            if (docMsg.getCharacterElement(i).getName().equals("icon")) {
                //ChatPic = (ChatPic)
                Icon icon = StyleConstants.getIcon(jpMsg.getStyledDocument().getCharacterElement(i).getAttributes());
                ChatEmo cupic = (ChatEmo) icon;
                EmoInfo emoInfo = new EmoInfo(i, cupic.getIm() + "");
                myEmoInfo.add(emoInfo);
                sb.append(i).append("&").append(cupic.getIm()).append("+");
            }
        }
        System.out.println(sb);
        return sb.toString();
        //return null;
    }

    /**
     * Convert the received message into a custom font class object
     *
     * @param message received chat messages
     * @return Font class object
     */
    public FontAndText getReceiveFont(String message) {
        String[] msgs = message.split("[|]");
        String fontName = "";
        int fontSize = 0;
        String[] color;
        StringBuilder text = new StringBuilder(message);
        Color fontC = new Color(222, 222, 222);
        if (msgs.length >= 4) {  /* Simple processing here means that there is font information */
            fontName = msgs[0];
            fontSize = Integer.parseInt(msgs[1]);
            color = msgs[2].split("-");
            if (color.length == 3) {
                int r = Integer.parseInt(color[0]);
                int g = Integer.parseInt(color[1]);
                int b = Integer.parseInt(color[2]);
                fontC = new Color(r, g, b);
            }
            text = new StringBuilder();
            for (int i = 3; i < msgs.length; i++) {
                text.append(msgs[i]);
            }
        }
        FontAndText attr = new FontAndText();

        attr.setName(fontName);
        attr.setSize(fontSize);
        attr.setColor(fontC);

        attr.setText(text.toString());

        System.out.println("getRecivedFont(String message):" + attr);
        return attr;
    }

    private void insertEmo(boolean isFriend) {

        if (isFriend) {
            if (this.receiveEmoInfo.size() == 0) {
                return;
            } else {
                for (EmoInfo emo : receiveEmoInfo) {
                    String fileName;
                    jpChat.setCaretPosition(pos1 + emo.getPos()); /*insert Emoji*/
                    fileName = "defaultemo/" + emo.getVal() + ".gif";/*Change Address of Emoji*/
                    jpChat.insertIcon(new ImageIcon(Objects.requireNonNull(EmosWindow.class.getResource(fileName)))); /*Insert Emoji*/
                    /*					jpChat.updateUI();*/
                }
                receiveEmoInfo.clear();
            }
        } else {

            if (myEmoInfo.size() == 0) {
                return;
            } else {
                for (EmoInfo pic : myEmoInfo) {
                    jpChat.setCaretPosition(pos2 + pic.getPos()); /* the insert position*/
                    String fileName;
                    fileName = "defaultemo/" + pic.getVal() + ".gif";/* change the emoji address */
                    jpChat.insertIcon(new ImageIcon(EmosWindow.class.getResource(fileName))); /* insert emoji */
                    /*jpChat.updateUI();*/
                }
                myEmoInfo.clear();
            }
        }
        jpChat.setCaretPosition(docChat.getLength()); /* set scroll to down*/
        //insert(new FontAttrib()); /* get newline */
    }

    /**
     * Insert formatted text into JTextPane
     *
     */
    private void insert(FontAndText attrib) {
        try { // insert text
            docChat.insertString(docChat.getLength(), attrib.getText() + "\n",
                    attrib.getAttrSet());
            jpChat.setCaretPosition(docChat.getLength()); // set scroll down
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void insert(String text) {
        try { // insert text
            docChat.insertString(docChat.getLength(), text + "\n",
                    dateFont.getAttrSet());
            jpChat.setCaretPosition(docChat.getLength()); // set insert position

        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get Font Set
     *
     * @return FontAttrib
     */
    private FontAndText getFontAttrib() {
        FontAndText att = new FontAndText();
        att.setText(jpMsg.getText() + "*" + buildEmoInfo());// the information of Emo and Text
        att.setName((String) fontName.getSelectedItem());
        att.setSize(Integer.parseInt((String) Objects.requireNonNull(fontSize.getSelectedItem())));
        String temp_color = (String) fontColor.getSelectedItem();
        assert temp_color != null;
        switch (temp_color) {
            case "BLACK":
                att.setColor(new Color(0, 0, 0));
                break;
            case "RED":
                att.setColor(new Color(255, 0, 0));
                break;
            case "BLUE":
                att.setColor(new Color(0, 0, 255));
                break;
            case "YEllOW":
                att.setColor(new Color(255, 255, 0));
                break;
            case "GREEN":
                att.setColor(new Color(0, 255, 0));
                break;
        }
        return att;
    }

    @Override
    public void run() {
        DatagramSocket chatSoc = null;
        try {
            chatSoc = new DatagramSocket(Sender.chatPort);
        } catch (SocketException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "");
            System.exit(1);
        }
        while (true) {
            try {
                byte[] bytes = new byte[1024 * 128];
                DatagramPacket dp = new DatagramPacket(bytes, bytes.length);
                chatSoc.receive(dp);
                executorService.execute(() -> {
                    try {
                        receiveMessage(bytes, dp);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "");
                e.printStackTrace();
            }
        }
    }

    private void receiveMessage(byte[] bytes, DatagramPacket dp) throws UnsupportedEncodingException {
        String recStr = new String(bytes, 0, dp.getLength(), StandardCharsets.UTF_8);
        //System.out.println("GroupsPanel recStr = " + recStr);
        String[] strs = recStr.split("[*]");
        int msgType;
        if (strs.length >= 3) {
            msgType = Integer.parseInt(strs[0]);
        } else {
            /*System.out.println("Not Friend's Message");*/
            return;
        }
        String uname = strs[1];

        StringBuilder message = new StringBuilder(strs[2]);
        if (strs.length > 3) {
            for (int i = 3; i < strs.length; i++) {
                message.append("*").append(strs[i]);
            }
        }
        if (msgType == MsgType.CHAT) {
            this.addRecMsg(uname, message.toString());

        } else if (msgType == MsgType.SHAKE) {
            this.shake(uname);
        }
    }

    public JButton getPicBtn() {
        return b_emo;
    }
}