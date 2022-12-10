package component;

import javax.swing.*;
import java.net.URL;
public class ChatEmo extends ImageIcon{
    /**
     *Image Description
     */
    private static final long serialVersionUID = 1L;
    int im;//Picture Number
    public int getIm() {
        return im;
    }
    public void setIm(int im) {
        this.im = im;
    }
    public ChatEmo(URL url,int im){
        super(url);
        this.im = im;
    }
}