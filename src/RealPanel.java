import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import javax.swing.text.*;

public class RealPanel extends JScrollPane {
    //////////////////////////////////////////////////////start
    static class MyScrollBarUI extends BasicScrollBarUI {
        private final Dimension d = new Dimension();
        protected JButton createDecreaseButton(int orientation) {
            return new JButton() {
                public Dimension getPreferredSize() {
                    return d;
                }
            };
        }
        protected JButton createIncreaseButton(int orientation) {
            return new JButton() {
                public Dimension getPreferredSize() {
                    return d;
                }
            };
        }
        protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
        }
        protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
        }
    }
    ////////end refer: W3CSchool

    public JTextPane realText = new JTextPane();
    public boolean format = false, isNMGLeft = false, OK = false;
    private final ArrayList<String> insertString = new ArrayList<>();
    private final ArrayList<SimpleAttributeSet> insertFormat = new ArrayList<>();
    private SimpleAttributeSet attrSet = new SimpleAttributeSet();
    private final char area_name;
    private int indexOFme;

    public RealPanel(int width, int height, int indexOFme, char area_name) {
        this.indexOFme = indexOFme;
        this.area_name = area_name;
        Border nullBorder = BorderFactory.createEmptyBorder(); //JScrollPane没有setBorder()方法，JScrollPane的源码中设置，
        setBorder(nullBorder);                     //若没有边框则默认添加边框，所以给它加个空border。
        setFocusable(false);
        setSize(width, height);
        setBackground(new Color(0, 0, 0, 50));
        realText.setBackground(new Color(0, 0, 0, 0));
        realText.setFocusable(false);
        realText.setEditable(false);
        setViewportView(realText);
        setLocation(MapFrame.panelArea_Button.get(area_name + "").getLocation().x, MapFrame.panelArea_Button.get(area_name + "").getLocation().y
                + MapFrame.panelArea_Button.get(area_name + "").currentHeight - (indexOFme*12));
        getVerticalScrollBar().setUI(new MyScrollBarUI());
    }

    public boolean contains(int x, int y) {
        return false;
    }

    public Point getMyPoint() {
        Point p = new Point(getX(), getY());
        if (area_name == 'F' && !isNMGLeft) {
            p.x += 140;
        }
        if (area_name == 'D' || area_name == 'E') {
            p.x += 50;
        }
        p.x += 3;
        p.y += 20;
        return p;
    }

    public void removeData() {
        insertString.clear();
        insertFormat.clear();
        Document doc = realText.getDocument();
        try {
            doc.remove(0, realText.getDocument().getLength());
            if (insertString.size() > 0) {
                insertFormat.subList(0, insertString.size()).clear();
            }
        } catch (BadLocationException e) {
            JOptionPane.showMessageDialog(null,
                    "清空列表失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void upLocation(int y) {
        setLocation(getX(), getY() + y);
    }

    private void getChange(int height) {
        height *= 2;
        setSize(getWidth(), getHeight() + height);
        MapFrame.panelArea_Button.get(area_name + "").getUpd(indexOFme, height);
        MapFrame.panelArea_Button.get(area_name + "").currentHeight += height;
        repaint();
    }

    public void insertData(ArrayList<String> insertDataList) {
        setDocs(insertDataList.get(0), PanelAreaLabel.getProvinceColor(), true, 30);
        for (int i = 2; i < insertDataList.size(); i += 2) {
            setDocs(insertDataList.get(i - 1), PanelAreaLabel.getSchoolColor(), false, 25);
            if (i + 2 >= insertDataList.size()) {
                setDocs(insertDataList.get(i).trim(), PanelAreaLabel.getStudentColor(), false, 25);
            } else {
                setDocs(insertDataList.get(i), PanelAreaLabel.getStudentColor(), false, 25);
            }
        }
        if (insertDataList.get(0).contains("新疆") || insertDataList.get(0).contains("青海")
                || insertDataList.get(0).contains("西藏")) {
            isNMGLeft = true;
        }
        setTTF();
    }


    public boolean judgeIfTextOutOfBounds() {
        return !format && !(getVerticalScrollBar().toString().contains("hidden"));
    }

    public boolean getLarge() {
        if (!judgeIfTextOutOfBounds()) {
            if (MapFrame.panelArea_Button.get(area_name + "").finish) OK = true;
        } else {
            if (OK) return true;
            getChange(-1);
            if (judgeIfTextOutOfBounds()) {
                getChange(2);
                return false;
            }
        }
        return true;
    }

    public void reInsert() {
        Document doc = realText.getDocument();
        try {
            doc.remove(0, realText.getDocument().getLength());
            setNewDocs(insertString.get(0),  PanelAreaLabel.getProvinceColor(), false, StyleConstants.getFontSize(insertFormat.get(0)));
            for (int i = 2; i < insertString.size(); i += 2) {
                setNewDocs(insertString.get(i - 1), PanelAreaLabel.getSchoolColor(), false, StyleConstants.getFontSize(insertFormat.get(i-1)));
                if (i + 2 >= insertString.size()) {
                    setNewDocs(insertString.get(i).trim(), PanelAreaLabel.getStudentColor(), false, StyleConstants.getFontSize(insertFormat.get(i)));
                } else {
                    setNewDocs(insertString.get(i), PanelAreaLabel.getStudentColor(), false, StyleConstants.getFontSize(insertFormat.get(i)));
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "重置列表失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void forceReformat() {
        if (!format) {
            OK = false;
            format = true;
            Document doc = realText.getDocument();
            try {
                doc.remove(0, realText.getDocument().getLength());
                for (int i = 0; i < insertString.size(); i++) {
                    StyleConstants.setFontSize(insertFormat.get(i), StyleConstants.getFontSize(insertFormat.get(i)) - 1);
                    getChange(-1);
                    doc.insertString(doc.getLength(), insertString.get(i), insertFormat.get(i));
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "重置列表失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
            format = false;
        }
    }

    public void insert(String str, AttributeSet attrSet) {
        Document doc = realText.getDocument();
        try {
            doc.insertString(doc.getLength(), str, attrSet);
        } catch (BadLocationException e) {
            JOptionPane.showMessageDialog(null,
                    "添加文字异常", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setDocsX(String str, Color col) {
        StyleConstants.setForeground(attrSet, col);
        insert(str, attrSet);
    }

    public void setNewDocs(String str, Color col, boolean bold, int fontSize){
        StyleConstants.setForeground(attrSet, col);             //颜色
        if (bold) {
            StyleConstants.setBold(attrSet, true);
        }//字体类型
        StyleConstants.setFontSize(attrSet, fontSize);             //字体大小
        insert(str, attrSet);
    }

    public void setDocs(String str, Color col, boolean bold, int fontSize) {
        StyleConstants.setForeground(attrSet, col);             //颜色
        if (bold) {
            StyleConstants.setBold(attrSet, true);
        }//字体类型
        StyleConstants.setFontSize(attrSet, fontSize);             //字体大小
        insert(str, attrSet);
        insertFormat.add(new SimpleAttributeSet(attrSet.copyAttributes()));
        insertString.add(str);
    }

    public void setCenter() {
        if (MapFrame.panelArea_Button.get(area_name + "").isCenter()) {
            StyledDocument doc = realText.getStyledDocument();
            StyleConstants.setAlignment(attrSet, StyleConstants.ALIGN_CENTER);
            doc.setParagraphAttributes(0, doc.getLength(), attrSet, false);
        }
    }


    public void setTTF() {
        if (MapFrame.panelArea_Button.get(area_name + "").getTTFPath() != null) {
            String path = MapFrame.panelArea_Button.get(area_name + "").getTTFPath();
            try {
                Font font = Font.createFont(Font.TRUETYPE_FONT, new BufferedInputStream(new FileInputStream(
                        new File(path))));
                font = font.deriveFont(Font.ITALIC, 30);
                realText.setFont(font);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "字体丢失", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
        setCenter();
    }
}
