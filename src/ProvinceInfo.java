import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class ProvinceInfo extends JFrame {
    private final String[] titles = {"愿你所到之处，遍地阳光", "愿有前程可奔赴,有岁可回首",
            "愿你所在的城市有暖阳有清风，每天都过得很好", "时间如风，记忆入壶，自此别后,望君珍重", "茫茫人海分离,终相见",
            "毕业相离，此去年年，爱念", "从此将各奔西东，此刻把酒祝东风，且共从容", "来日茫茫，愿你还能见花欣喜，一如当初青涩模样",
            "回首青春时光，只愿来日方长"};
    private Province currentProvince;
    private PanelArea provinceName = new PanelArea();
    private boolean flagOfColor = false, flagOfLock = false;
    private final JScrollPane jScrollPane = new JScrollPane();
    private static Color selectColor = new Color(135, 206, 235, 200);
    private final JLabel backgroundIMG = new JLabel();
    private BufferedImage provinceIMG;
    private final int[] index = {1, 1, 1, 80},
            operate = {1, 1, 1, 1};

    class refresh extends Thread {
        public void run() {
            Timer timerY = new Timer(10, e -> {
                if (isVisible() && !flagOfLock) {
                    for (int i = 0; i < 3; i++) {
                        if (index[i] == 255) {
                            operate[i] = -1;
                        } else if (index[i] == 0) {
                            operate[i] = 1;
                        }
                    }
                    if (index[3] > 240) {
                        operate[3] = -1;
                    } else if (index[3] < 80) {
                        operate[3] = 1;
                    }
                    changeColorTo(new Color((int) (Math.random() * 2) > 0 ? index[0] += operate[0] : index[0],
                            (int) (Math.random() * 2) > 0 ? index[1] += operate[1] : index[1],
                            (int) (Math.random() * 2) > 0 ? index[2] += operate[2] : index[2],
                            (int) (Math.random() * 2) > 0 ? index[3] += operate[3] : index[3]));
                    repaint();
                }
            });
            timerY.start();
        }
    }

    class changeTitle extends Thread {
        public void run() {
            Timer changer = new Timer(5000, e -> {
                if (isVisible() && (int) (Math.random() * 2) > 0) {
                    setTitle(titles[(int) (Math.random() * titles.length)]);
                }
            });
            changer.start();
        }
    }

    public void refreshData(Province p) {
        currentProvince = p;
        setVisible(true);
        setExtendedState(JFrame.NORMAL);
        newStudent();
        initProvince();
        provinceName.removeData();
        provinceName.setDocs(p.ch_name, p.currentColor, true, 40);
    }

    private void newStudent() {
        jScrollPane.setViewportView(Selection.jTable);
        MapFrame.selectionDialog.sort(currentProvince.ch_name);
    }

    public void initProvince() {
        index[0] = currentProvince.currentColor.getRed();
        index[1] = currentProvince.currentColor.getGreen();
        index[2] = currentProvince.currentColor.getBlue();
        index[3] = currentProvince.currentColor.getAlpha();
        provinceIMG = currentProvince.img_rgb;
        double width = 220, height = 220, w = provinceIMG.getWidth(), h = provinceIMG.getHeight();
        while (w > width || h > height) {
            w *= 0.9;
            h *= 0.9;
        }
        int x = (int) w, y = (int) h;
        backgroundIMG.setSize(x, y);
        BufferedImage newImage = new BufferedImage(x, y, provinceIMG.getType());
        Graphics g = newImage.getGraphics();
        g.drawImage(provinceIMG, 0, 0, x, y, null);
        g.dispose();
        provinceIMG = newImage;
        backgroundIMG.setIcon(new ImageIcon(provinceIMG));
    }

    public void changeColorTo(Color color) {
        int w = provinceIMG.getWidth(), h = provinceIMG.getHeight();
        int[] RGBs = new int[w * h];
        int rgb = color.getRGB();
        provinceIMG.getRGB(0, 0, w, h, RGBs, 0, w);
        for (int i = 0; i < w * h; i++) {
            if (RGBs[i] != 0) {
                RGBs[i] = rgb;
            }
        }
        provinceIMG.setRGB(0, 0, w, h, RGBs, 0, w);
        backgroundIMG.setIcon(new ImageIcon(provinceIMG));
    }

    public ProvinceInfo() {
        setVisible(false);
        int this_height = 450, this_with = 600;
        setSize(this_with, this_height);//大小
        setLocationRelativeTo(null);
        setLayout(null);
        setTitle("愿所到之处皆为热土，愿所遇之人皆为挚友");
        provinceName.setPoint(this_with / 4, 0, (this_with * 3) / 4, 60);
        provinceName.refreshLocation();
        provinceName.setCenter();
        provinceName.setBackground(new Color(255, 255, 255, 100));
        add(provinceName);
        setIconImage(new ImageIcon("res/logo.jpg").getImage());

        jScrollPane.setBounds(10, 65, this_with - 250, this_height - 225);
        add(jScrollPane);
        add(backgroundIMG);
        backgroundIMG.setLocation(this_with - 240, 100);

        MealButton changeColor = new MealButton("改变省份颜色", 170, 50);
        changeColor.setLocation(10, this_height - 155);
        changeColor.addActionListener(e -> {
            Color color = JColorChooser.showDialog(null, "请选择颜色", currentProvince.currentColor);
            if (color == null) {
                color = currentProvince.currentColor;
            }
            currentProvince.changeColorTo(color);
            provinceName.removeData();
            provinceName.setDocs(currentProvince.ch_name, color, true, 40);
            currentProvince.stopColor(flagOfColor);
            initProvince();
            repaint();
        });
        add(changeColor);

        MealButton randomColor = new MealButton("随机省份颜色", 170, 50);
        randomColor.setLocation(190, this_height - 155);
        randomColor.addActionListener(e -> {
            currentProvince.changeRandomColor();
            provinceName.removeData();
            provinceName.setDocs(currentProvince.ch_name, currentProvince.currentColor, true, 40);
            currentProvince.stopColor(flagOfColor);
            initProvince();
            repaint();
        });
        add(randomColor);

        MealButton copyColor = new MealButton("复制省份颜色", 170, 50);
        copyColor.setLocation(10, this_height - 100);
        copyColor.addActionListener(e -> selectColor = currentProvince.currentColor);
        add(copyColor);

        MealButton pasteColor = new MealButton("粘贴省份颜色", 170, 50);
        pasteColor.setLocation(190, this_height - 100);
        pasteColor.addActionListener(e -> {
            currentProvince.changeColorTo(selectColor);
            provinceName.removeData();
            provinceName.setDocs(currentProvince.ch_name, selectColor, true, 40);
            currentProvince.stopColor(flagOfColor);
            initProvince();
            repaint();
        });
        add(pasteColor);

        MealButton lockColor = new MealButton("锁定当前颜色", 200, 50, 50, false);
        lockColor.setLocation(370, this_height - 155);
        lockColor.addActionListener(e -> {
            flagOfLock = !flagOfLock;
            currentProvince.changeColorTo(new Color(index[0], index[1], index[2], index[3]));
            provinceName.removeData();
            provinceName.setDocs(currentProvince.ch_name, new Color(index[0], index[1], index[2], index[3]), true, 40);
            currentProvince.stopColor(flagOfColor);
            initProvince();
            repaint();
        });
        add(lockColor);

        MealButton defaultColor = new MealButton("固定所选颜色", 200, 50, 50, false);
        defaultColor.setLocation(370, this_height - 100);
        defaultColor.addActionListener(e -> {
            flagOfColor = !flagOfColor;
            currentProvince.stopColor(flagOfColor);
        });
        add(defaultColor);

        Thread chgTit = new changeTitle();
        chgTit.start();
        Thread ref = new refresh();
        ref.start();

        JLabel background = new JLabel();
        background.setLocation(0, 0);
        background.setSize(this_with, this_height - 20);
        try {
            provinceIMG = (ImageIO.read(new File("res/bgOfProvince.jpg")));
            BufferedImage orin_img = (ImageIO.read(new File("res/bgOfProvince.jpg")));
            BufferedImage newImage = new BufferedImage(this_with, this_height - 20,
                    orin_img.getType());
            Graphics g = newImage.getGraphics();
            g.drawImage(orin_img, 0, 0, this_with, this_height - 20, null);
            g.dispose();
            orin_img = newImage;
            background.setIcon(new ImageIcon(orin_img));
        } catch (Exception ignored) {
        }
        add(background);

    }
}