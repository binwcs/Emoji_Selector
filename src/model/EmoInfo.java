package model;

/**
 * EmoInfo
 */
public class EmoInfo {
    /* Emo Info*/
    int pos;
    String val;

    public EmoInfo(int pos, String val) {
        this.pos = pos;
        this.val = val;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }


    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }
}