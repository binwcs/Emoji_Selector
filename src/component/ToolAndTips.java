package component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
/**
 * Bubble prompt box class
 */
@SuppressWarnings("ALL")
public class ToolAndTips extends JPanel {
    private static final long serialVersionUID = 1L;
    final JLabel label = new JLabel();
    final Component parentWindow;    // The form of the component to display the balloon
    private boolean haveShowPlace;
    private final Component attachedCom;    // The component to display the bubble

    public ToolAndTips(Component parent, Component attachedComponent, Color fillColor,
                       int borderWidth, int offsete) {
        this.parentWindow = parent;
        this.attachedCom = attachedComponent;
        label.setBorder(new EmptyBorder(borderWidth, borderWidth, borderWidth,
                borderWidth));
        label.setBackground(fillColor);
        label.setOpaque(true);
        label.setFont(new Font("system", 0, 12));

        setOpaque(false);
        // this.setBorder(new BalloonBorder(fillColor, offset));
        this.setLayout(new BorderLayout());
        add(label);

        setVisible(false);
        // When the bubble is displayed, the component moves, and the bubble moves with it
        this.attachedCom.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                if (isShowing()) {//The floating prompt is displayed, reset the position
                    determineAndSetLocation();
                }
            }
        });
    }

    private void determineAndSetLocation() {
        if (!attachedCom.isShowing()) {
            return;
        }
        Point loc = attachedCom.getLocationOnScreen();    //The position of the control relative to the screen
        Point paPoint = parentWindow.getLocationOnScreen();    //Corresponds to the position of the form relative to the screen
        //System.out.println(attachedComponent.getLocationOnScreen());
        setBounds(loc.x - paPoint.x, loc.y - paPoint.y - getPreferredSize().height,
                getPreferredSize().width, getPreferredSize().height);
    }

    public void setText(String text) {
        label.setText(text);
    }
    // Set bubble background image

    // Set the distance between the text and picture of the bubble
    @Override
    public void setVisible(boolean show) {
        if (show) {
            determineAndSetLocation();
            findShowPlace();
        }
        super.setVisible(show);
    }

    private void findShowPlace() {
        if (haveShowPlace) {
            return;
        }
        // we use the popup layer of the top level container (frame or
        // dialog) to show the balloon tip
        // first we need to determine the top level container
        JLayeredPane layeredPane = null;
        if (parentWindow instanceof JDialog) {
            layeredPane = ((JDialog) parentWindow).getLayeredPane();
        } else if (parentWindow instanceof JFrame) {
            layeredPane = ((JFrame) parentWindow).getLayeredPane();
        }

        if (layeredPane != null) {
            layeredPane.add(this, JLayeredPane.POPUP_LAYER);
            haveShowPlace = true;
        }
    }
}