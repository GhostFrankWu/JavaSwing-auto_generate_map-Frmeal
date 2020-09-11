import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/***
 * this class is a self defined Button, ti may look little better than default JButton
 *
 * 
 */
public class MealButton extends JButton {
    private ImageIcon buttonBefore = null, buttonAfter = null;
    private String btName;
    private Font font;
    protected boolean flag = false,key=false;
    public int offset_X = 10, dim = 0;
    public static final int UP = 1, RIGHT = 2, DOWN = 3, LEFT = 4,MINUS=5,PLUS=6;

    public MealButton(String btName, int x, int y, int d, boolean flags) {
        offset_X = d;
        dim = 1;
        setForeground(Color.red);
        this.btName = btName;
        this.setBorderPainted(false);
        this.setContentAreaFilled(false);
        this.setSize(x, y);
        iniTTF(this.getHeight() / 2);
        initImg("choice");
        this.addActionListener(e -> {
            flag = !flag;
            MapFrame.currentButton = this; //debug
        });
        if (flags) {
            ImageIcon tmp = buttonAfter;
            buttonAfter = buttonBefore;
            buttonBefore = tmp;
            repaint();
        }
    }

    public MealButton(int x, int y, int n) {
        key=true;
        this.btName = "";
        this.setBorderPainted(false);
        this.setContentAreaFilled(false);
        this.setSize(x, y);
        iniTTF(this.getHeight() / 2);
        initImg("key"+n);
    }

    public MealButton(String btName, int x, int y) {
        setForeground(Color.red);
        this.btName = btName;
        this.setBorderPainted(false);
        this.setContentAreaFilled(false);
        this.setSize(x, y);
        iniTTF(this.getHeight() / 2);
        initImg("button");
        this.addActionListener(e -> {
            MapFrame.currentButton = this; //debug
        });
    }

    public void initImg(String name) {
        try {
            BufferedImage orin_img = (ImageIO.read(new File("res/" + name + ".jpg")));
            BufferedImage newImage;
            if (!name.equals("choice")) {
                newImage = new BufferedImage(this.getWidth(), this.getHeight(), orin_img.getType());
                Graphics g = newImage.getGraphics();
                g.drawImage(orin_img, 0, 0, this.getWidth(), this.getHeight(), null);
                g.dispose();
                orin_img = newImage;
            }
            buttonBefore = new ImageIcon(orin_img);
            if (!name.contains("key")) {
                orin_img = (ImageIO.read(new File("res/" + name + "0.jpg")));
            }
            if (!name.equals("choice")) {
                newImage = new BufferedImage(this.getWidth(), this.getHeight(), orin_img.getType());
                Graphics g = newImage.getGraphics();
                g.drawImage(orin_img, 0, 0, this.getWidth(), this.getHeight(), null);
                g.dispose();
                orin_img = newImage;
            }
            buttonAfter = new ImageIcon(orin_img);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "初始化背景失败", "注意", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void iniTTF(int k) {
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new BufferedInputStream(new FileInputStream(
                    new File("ttf/normal.ttf"))));
            font = font.deriveFont(Font.ITALIC, k);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "字体丢失", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void paintComponent(Graphics g) {
        g.setFont(font);
        if (dim == 0) {
            if ((this.getModel().isPressed() && this.getModel().isRollover()) || !this.getModel().isPressed() && !this.getModel().isRollover()) {
                g.drawImage(buttonBefore.getImage(), 0, 0, this);
                setForeground(Color.red);
                if(key){
                    setBorderPainted(false);
                }
            } else {
                g.drawImage(buttonAfter.getImage(), 0, 0, this);
                setForeground(Color.black);
                if(key){
                    setBorderPainted(true);
                }
            }
        } else {
            if (flag) {
                g.drawImage(buttonAfter.getImage(), 2, 2, this);
            } else {
                g.drawImage(buttonBefore.getImage(), 2, 2, this);
            }
        }
        g.drawString(btName, offset_X, (int) (this.getHeight() * 0.66));
    }
}
