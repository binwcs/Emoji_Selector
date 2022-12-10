package model;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;

/**
 * Font
 */
public class FontAndText {
    private String msg = "", name = "Song"; //

    private int size = 0; // Font

    private Color color = new Color(225, 225, 225); // Font color

    /**
     * Free Construction(for newline)
     */

    public FontAndText() {
    }

    public FontAndText(String msg, String fontName, int fontSize, Color color) {
        this.msg = msg;
        this.name = fontName;
        this.size = fontSize;
        this.color = color;
    }

    /**
     * return Attribute set
     *
     */
    public SimpleAttributeSet getAttrSet() {
        // Attribute Set
        SimpleAttributeSet attrSet = new SimpleAttributeSet();
        if (name != null) {
            StyleConstants.setFontFamily(attrSet, name);
        }
        StyleConstants.setBold(attrSet, false);
        StyleConstants.setItalic(attrSet, false);
        StyleConstants.setFontSize(attrSet, size);
        if (color != null)
            StyleConstants.setForeground(attrSet, color);
        return attrSet;
    }

    public String toString() {
        //divide message to 4 parts
        return name + "|"
                + size + "|"
                + color.getRed() + "-" + color.getGreen() + "-" + color.getBlue() + "|"
                + msg;
    }

    public String getText() {
        return msg;
    }

    public void setText(String text) {
        this.msg = text;
    }


    public void setColor(Color color) {
        this.color = color;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSize(int size) {
        this.size = size;
    }
}