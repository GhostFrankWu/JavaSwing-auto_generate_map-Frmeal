import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class PanelArea extends JTextPane{
    private int x1, x2, y1, y2;
    private SimpleAttributeSet attrSet = new SimpleAttributeSet();

    public static void changeColors(Color a, Color b, Color c) {
        PanelAreaLabel.changeColors(a,b,c);
        MapFrame.panelArea_Button.forEach((k,v)->{
            for(RealPanel pa :v.panels){
                if(!k.equals("G")&&!k.equals("H")) {
                    pa.reInsert();
                }
            }
        });
    }

    public static Color getProvinceColor() {
        return PanelAreaLabel.getProvinceColor();
    }

    public static Color getSchoolColor() {
        return PanelAreaLabel.getSchoolColor();
    }

    public static Color getStudentColor() {
        return PanelAreaLabel.getStudentColor();
    }

    public static void changeColors(Color a, int i) {
        PanelAreaLabel.changeColors(a,i);
        MapFrame.panelArea_Button.forEach((k,v)->{
            for(RealPanel pa :v.panels){
                if(!k.equals("G")&&!k.equals("H")) {
                    pa.reInsert();
                }
            }
        });
    }

    public boolean contains(int x, int y) {
        return false;
    }

    public void removeData() {
        Document doc = this.getDocument();
        try {
            doc.remove(0, this.getDocument().getLength());
        } catch (BadLocationException e) {
            JOptionPane.showMessageDialog(null,
                    "清空列表失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public PanelArea() {
        setFocusable(false);
        setSelectionColor(new Color(0, 0, 0, 0));
        setEditable(false);
        setBackground(new Color(0, 0, 0, 0));
    }
    public void refreshLocation() {
        setLocation(x1, y1);
        setSize(x2 - x1, y2 - y1);
        repaint();
    }

    public void setPoint(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }

    public void insert(String str, AttributeSet attrSet) {
        Document doc = this.getDocument();
        try {
            doc.insertString(doc.getLength(), str, attrSet);
        } catch (BadLocationException e) {
            JOptionPane.showMessageDialog(null,
                    "添加文字异常", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setCenter() {
        StyledDocument doc = getStyledDocument();
        StyleConstants.setAlignment(attrSet, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), attrSet, false);
    }

    public void setDocs(String str, Color col, boolean bold, int fontSize) {
        StyleConstants.setForeground(attrSet, col);             //颜色
        if (bold) {
            StyleConstants.setBold(attrSet, true);
        }//字体类型
        StyleConstants.setFontSize(attrSet, fontSize);             //字体大小
        insert(str, attrSet);
    }
}
