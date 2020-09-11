import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.awt.Color;
import java.util.ArrayList;

public class Province extends JButton {
    protected static final Color null_nonStar_Color=new Color(80,63,132, 130),
                                 null_Star_Color=new Color(135, 206, 235, 50);
    protected ImageIcon img;
    protected BufferedImage img_rgb;
    protected String name,ch_name;
    protected Color currentColor = null_nonStar_Color,chooseColor;
    protected static Color NullColor=null_nonStar_Color;
    public ArrayList<College> allCollege=new ArrayList<>();
    public Point realLocation;

    public String getName() {
        return name;
    }

    Province(ImageIcon img, String icon, String s,String ch,int x,int y) {
        super();
        realLocation=new Point(x,y);
        name = s;
        ch_name=ch;
        setBorderPainted(false);
        setContentAreaFilled(false);
        this.img = img;
        setSize(img.getIconWidth(), img.getIconHeight());
        try {
            img_rgb = ImageIO.read(new File(icon));
        } catch (Exception ignored) {
        }
        this.addActionListener(e -> {
            MapFrame.currentButton =MapFrame.tool_button;
            MapFrame.myInfo.refreshData(this);
        });
    }

    public void changeColorByStudent(){
        if (allCollege.size()==0){
            changeColorTo(NullColor);
        }else{
            if(chooseColor!=null){
                changeColorTo(chooseColor);
            }else {
                changeRandomColor();
            }
        }
    }

    public void changeRandomColor() {
        changeColorTo(new Color((int) (Math.random() * 205) + 50, (int) (Math.random() * 205) + 50,
                (int) (Math.random() * 205 + 50), (int) (Math.random() * 180) + 75));
    }

    public void changeColorTo(Color color) {
        int w = img_rgb.getWidth(), h = img_rgb.getHeight();
        int[] RGBs = new int[w * h];
        int rgb = color.getRGB();
        img_rgb.getRGB(0, 0, w, h, RGBs, 0, w);
        for (int i = 0; i < w * h; i++) {
            if (RGBs[i] == currentColor.getRGB()) {
                RGBs[i] = rgb;
            }
        }
        img_rgb.setRGB(0, 0, w, h, RGBs, 0, w);
        this.img = new ImageIcon(img_rgb);
        currentColor = color;
        repaint();
    }

    public void stopColor(boolean flag) {
        if(flag) {
            chooseColor = currentColor;
        }else{
            chooseColor=null;
        }
    }

    public void changeColor() {
        int w = img_rgb.getWidth(), h = img_rgb.getHeight();
        int[] RGBs = new int[w * h];
        img_rgb.getRGB(0, 0, w, h, RGBs, 0, w);
        int rgb = new Color(Color.LIGHT_GRAY.getRed(), Color.LIGHT_GRAY.getGreen(), Color.LIGHT_GRAY.getBlue(),
                Color.LIGHT_GRAY.getAlpha() - 130).getRGB();
        for (int i = 0; i < w * h; i++) {
            if (RGBs[i] != 0) {
                if (RGBs[i] != -16759464 && RGBs[i] != -1728001127) {//各省份的颜色和北京的颜色
                    RGBs[i] = rgb;
                } else {
                    RGBs[i] = currentColor.getRGB();
                }
            }
        }
        img_rgb.setRGB(0, 0, w, h, RGBs, 0, w);
        this.img = new ImageIcon(img_rgb);
    }

    public void paintComponent(Graphics g) { // 按下事件
        if ((this.getModel().isPressed() && this.getModel().isRollover()) || !this.getModel().isPressed() && !this.getModel().isRollover()) {

            g.drawImage(img.getImage(), 3, 3, this);
        } else {

            g.drawImage(img.getImage(), 0, 0, this);
        }
    }
    // 重写contains方法。
    public boolean contains(int x, int y) {
        int rgb, alpha;
        try {
            rgb = img_rgb.getRGB(x, y);
            alpha = (rgb >> 24) & 0xFF;
            if (alpha != 0) {
                return true;
            }
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        return false;
    }
}

