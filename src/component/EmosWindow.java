package component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Emoji Windows
 */
public class EmosWindow extends JWindow {
    public static final String FACE_IMAGE_DIR = "defaultemo/";
    public static final String GIF_SUB = ".gif";
    private static final long serialVersionUID = 1L;
    GridLayout gridLayout1 = new GridLayout(7, 15);
    JLabel[] ico = new JLabel[105]; /*put emoji*/
    int i;
    ChatFrame owner;

    public EmosWindow(ChatFrame owner) {
        super(owner);
        this.owner = owner;
        try {
            init();
            this.setAlwaysOnTop(true);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void init() {
        this.setPreferredSize(new Dimension(28 * 15, 28 * 7));
        JPanel p = new JPanel();
        p.setOpaque(true);
        this.setContentPane(p);
        p.setLayout(gridLayout1);
        p.setBackground(SystemColor.text);
        String fileName;
        for (i = 0; i < ico.length; i++) {
            fileName = FACE_IMAGE_DIR + i + GIF_SUB;/*change the emoji address */
            ico[i] = new JLabel(new ChatEmo(EmosWindow.class.getResource(fileName), i), SwingConstants.CENTER);
            ico[i].setBorder(BorderFactory.createLineBorder(new Color(225, 225, 225), 1));
            ico[i].setToolTipText(i + "");
            ico[i].addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == 1) {
                        JLabel cubl = (JLabel) (e.getSource());
                        ChatEmo cupic = (ChatEmo) (cubl.getIcon());
                        owner.insertSendEmo(cupic);
                        cubl.setBorder(BorderFactory.createLineBorder(new Color(225, 225, 225), 1));
                        getObj().dispose();
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    ((JLabel) e.getSource()).setBorder(BorderFactory.createLineBorder(Color.BLUE));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    ((JLabel) e.getSource()).setBorder(BorderFactory.createLineBorder(new Color(225, 225, 225), 1));
                }

            });
            p.add(ico[i]);
        }
        p.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                getObj().dispose();
            }

        });
    }

    @Override
    public void setVisible(boolean show) {
        if (show) {
            determineAndSetLocation();
        }
        super.setVisible(show);
    }

    private void determineAndSetLocation() {
        Point loc = owner.getPicBtn().getLocationOnScreen();/*The position of the control relative to the screen*/
        setBounds(loc.x - getPreferredSize().width / 3, loc.y - getPreferredSize().height,
                getPreferredSize().width, getPreferredSize().height);
    }

    private JWindow getObj() {
        return this;
    }

}