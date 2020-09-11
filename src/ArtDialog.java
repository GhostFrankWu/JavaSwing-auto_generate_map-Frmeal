import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/***
 * This class is the dialog of "高级风格设置"
 * titles
 * flags are conditions of SelectBox
 * color is cache of Province&School&Student
 * JTextArea is view of mapString and wordString
 * mapColor, wordColor as its name
 */
public class ArtDialog extends JFrame {
    private final String[] titles = {"从别后，忆相逢。几回魂梦与君同", "今宵剩把银缸照，犹恐相逢是梦中",
            "浮云一别后，流水十年间", "别后悠悠君莫问，无限事，不言中", "寒雨连江夜入吴，平明送客楚山孤",
            "春风知别苦，不遣柳条青", "劝君更尽一杯酒,西出阳关无故人"};
    public static boolean flagOfLogo = true, flagOfApply = false, flagOfLine = true, flagOfMap = true, flagOfWord = true;
    private Color[] colorData = {null, null, null};
    private PanelArea showString = new PanelArea();
    public static final JTextArea mapSelect = new JTextArea(), wordSelect = new JTextArea();
    private Color mapColor = Color.orange, wordColor = Color.CYAN;

    class refresh extends Thread {  //refresh to avoid show bug (swing的图层)
        public void run() {
            Timer timerY = new Timer(50, e -> {
                if (isVisible()) {
                    repaint();
                }
            });
            timerY.start();
        }
    }

    public void refreshData() {   //refresh color view of PSS data
        showString.removeData();
        showString.setDocs("省份：\n", PanelArea.getProvinceColor(), true, 40);
        showString.setDocs("学校：", PanelArea.getSchoolColor(), true, 40);
        showString.setDocs("X同学", PanelArea.getStudentColor(), true, 40);
    }

    class changeTitle extends Thread {  //randomly change title
        public void run() {
            Timer changer = new Timer(5000, e -> {
                if (isVisible() && (int) (Math.random() * 2) > 0) {
                    setTitle(titles[(int) (Math.random() * titles.length)]);
                }
            });
            changer.start();
        }
    }

    public ArtDialog() {

        setVisible(false);//default not show
        int this_height = 450, this_with = 600; //easy for debug(change size)
        setSize(this_with, this_height);
        setLocationRelativeTo(null);//center
        setLayout(null);
        setTitle("==========埋首烟波，似水流年==========");
        setIconImage(new ImageIcon("res/logo.jpg").getImage());
        new changeTitle().start();
        new refresh().start();
        //finish initialize

        showString.setPoint(250, this_height - 150, this_with, this_height);
        showString.refreshLocation();
        showString.setCenter();
        showString.setDocs("省份：\n", PanelArea.getProvinceColor(), true, 40);
        showString.setDocs("学校：", PanelArea.getSchoolColor(), true, 40);
        showString.setDocs("X同学", PanelArea.getStudentColor(), true, 40);
        add(showString);
        //view of PSS

        //start init buttons and select boxes
        MealButton logoChangeButton = new MealButton("改变印章图片", 170, 50);
        logoChangeButton.setLocation(10, 10);
        logoChangeButton.addActionListener(e -> changeLogo());
        add(logoChangeButton);

        MealButton logoButton = new MealButton("显示印章", 160, 50, 50, flagOfLogo);
        logoButton.setLocation(190, 10);
        logoButton.addActionListener(e -> {
            flagOfLogo = !flagOfLogo;//flag to decide action
            MapFrame.bgi_Logo.setVisible(flagOfLogo);
        });
        add(logoButton);

        MealButton lineButton = new MealButton("显示折线", 160, 50, 50, flagOfLine);
        lineButton.setLocation(355, 10);
        lineButton.addActionListener(e -> {
            flagOfLine = !flagOfLine;
            if (flagOfLine) {
                MapFrame.offset_lineBonds = 3; //default width
            } else {
                MapFrame.offset_lineBonds = 0;//width = 0 : paint nothing
            }
        });
        add(lineButton);

        MealButton mColor = new MealButton("颜色", 70, 50);
        mColor.setLocation(510, 10);
        mColor.addActionListener(e -> {
            Color color = JColorChooser.showDialog(null, "请选择绘制曲线的颜色", PanelArea.getStudentColor());
            if (color != null) {
                MapFrame.graphColor = color; //if chosen one color, change it
            }
        });
        add(mColor);

        mapSelect.setBounds(10, 65, 260, 60);
        mapSelect.setText("XX中学\n2020级X班\n毕业地图");
        add(mapSelect);
        wordSelect.setBounds(320, 65, 260, 60);
        wordSelect.setText("时间如风，记忆入壶\n自此别后,望君珍重");
        add(wordSelect); //editable text view

        MealButton mapButton = new MealButton("显示左上角班级", 260, 50, 50, flagOfLogo);
        mapButton.setLocation(10, 125);
        mapButton.addActionListener(e -> {
            flagOfMap = !flagOfMap;
            if (flagOfMap) {
                insertDocM(false);//reinsert words
            } else {
                for(RealPanel pa:MapFrame.panelArea_Button.get("G").panels){
                    pa.removeData(); //G is name of mapWordArea
                }
            }
        });
        add(mapButton);

        MealButton mapColor = new MealButton("选择班级信息颜色", 260, 50);
        mapColor.setLocation(10, 180);
        mapColor.addActionListener(e -> {
            Color color = JColorChooser.showDialog(null, "请选择班级信息颜色", PanelArea.getProvinceColor());
            if (color != null) {
                mapSelect.setForeground(color);
                changeColor(color, true);
            }
        });
        add(mapColor);

        MealButton mapTTF = new MealButton("选择班级信息字体", 260, 50);
        mapTTF.setLocation(10, 235);
        mapTTF.addActionListener(e -> setTTF(false));
        add(mapTTF);

        MealButton wordButton = new MealButton("显示右栏艺术字", 260, 50, 50, flagOfLogo);
        wordButton.setLocation(320, 125);
        wordButton.addActionListener(e -> {
            flagOfWord = !flagOfWord;
            if (flagOfWord) {
                insertDocW(false);
                CameraTest.width = MapFrame.this_with - 2;
            } else {
                for(RealPanel pa:MapFrame.panelArea_Button.get("H").panels){
                    pa.removeData(); //H is name of WordWordArea
                }
                CameraTest.width = MapFrame.this_with - 220;//change width of screenshot
            }
        });
        add(wordButton);

        MealButton wordColor = new MealButton("选择右侧艺术字颜色", 260, 50);
        wordColor.setLocation(320, 180);
        wordColor.addActionListener(e -> {
            Color color = JColorChooser.showDialog(null, "请选择右侧艺术字颜色", PanelArea.getProvinceColor());
            if (color != null) {
                wordSelect.setForeground(color);
                changeColor(color, false);
            }
        });
        add(wordColor);

        MealButton wordTTF = new MealButton("选择右侧艺术字字体", 260, 50);
        wordTTF.setLocation(320, 235);
        wordTTF.addActionListener(e -> setTTF(true));
        add(wordTTF);

        MealButton pColor = new MealButton("省份", 70, 50);
        pColor.setLocation(10, this_height - 150);
        pColor.addActionListener(e -> {
            Color color = JColorChooser.showDialog(null, "请选择省份的颜色", PanelArea.getProvinceColor());
            if (color != null) {
                showString.removeData();
                colorData[0] = color;
                showString.setDocs("省份：\n", colorData[0], true, 40);
                showString.setDocs("学校：", colorData[1] == null ? PanelArea.getSchoolColor() : colorData[1], true, 40);
                showString.setDocs("X同学", colorData[2] == null ? PanelArea.getStudentColor() : colorData[2], true, 40);
                //reinsert data, don't know which changed,so use ?: to judge if color need to be change
                if (flagOfApply) {
                    PanelArea.changeColors(colorData[0], 1); //is select apply, auto change color
                }
            }
        });
        add(pColor);

        MealButton sColor = new MealButton("学校", 70, 50);
        sColor.setLocation(90, this_height - 150);
        sColor.addActionListener(e -> {
            Color color = JColorChooser.showDialog(null, "请选择学校的颜色", PanelArea.getSchoolColor());
            if (color != null) {
                showString.removeData();
                colorData[1] = color;
                showString.setDocs("省份：\n", colorData[0] == null ? PanelArea.getProvinceColor() : colorData[0], true, 40);
                showString.setDocs("学校：", colorData[1], true, 40);
                showString.setDocs("X同学", colorData[2] == null ? PanelArea.getStudentColor() : colorData[2], true, 40);
                if (flagOfApply) {
                    PanelArea.changeColors(colorData[1], 2);
                }
            }
        });
        add(sColor);

        MealButton nColor = new MealButton("姓名", 70, 50);
        nColor.setLocation(170, this_height - 150);
        nColor.addActionListener(e -> {
            Color color = JColorChooser.showDialog(null, "请选择名字的颜色", PanelArea.getStudentColor());
            if (color != null) {
                showString.removeData();
                colorData[2] = color;
                showString.setDocs("省份：\n", colorData[0] == null ? PanelArea.getProvinceColor() : colorData[0], true, 40);
                showString.setDocs("学校：", colorData[1] == null ? PanelArea.getSchoolColor() : colorData[1], true, 40);
                showString.setDocs("X同学", colorData[2], true, 40);
                if (flagOfApply) {
                    PanelArea.changeColors(colorData[2], 3);
                }
            }
        });
        add(nColor);

        MealButton allColor = new MealButton("应用/还原 字体", 250, 50, 50, flagOfApply);
        allColor.setLocation(10, this_height - 95);
        allColor.addActionListener(e -> {
            flagOfApply = !flagOfApply;
            if (flagOfApply) {
                for (int i = 0; i < 3; i++) {
                    if (colorData[i] != null) {
                        PanelArea.changeColors(colorData[i], i + 1);//change mode to self defined
                    }
                }
            } else {//change mode to default
                if (MapFrame.flagOfStar) {
                    PanelArea.changeColors(new Color(240, 248, 255), new Color(225, 255, 255), new Color(245, 255, 250));
                } else {
                    PanelArea.changeColors(Color.red, Color.lightGray, Color.yellow);
                }
            }
        });
        add(allColor);
        //finish init buttons

        JLabel background = new JLabel();//background label
        background.setLocation(0, 0);
        background.setSize(this_with, this_height - 20);
        try {//convert img to bufferedImg, use graphics to change it size and convert back to ImgIcon
            BufferedImage orin_img = (ImageIO.read(new File("res/bgOfArt.jpg")));
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

    private void changeColor(Color color, boolean flag) {
        if (flag) {//change color-> true is mapColor; false is wordColor
            mapColor = color;
        } else {
            wordColor = color;
        }
    }

    private void setTTF(boolean flag) {
        JTextArea jA;//change TTF, true is wordSelect, false is mapSelect
        if (flag) {
            jA = wordSelect;
        } else {
            jA = mapSelect;
        }
        try {//if choose one file, change the TTF of select area
            JFileChooser jFileChooser = new JFileChooser(new File("."));
            int status = jFileChooser.showOpenDialog(null);
            if (status == JFileChooser.APPROVE_OPTION) {
                File file = jFileChooser.getSelectedFile();
                Font font = Font.createFont(Font.TRUETYPE_FONT, new BufferedInputStream(new FileInputStream(file)));
                font = font.deriveFont(Font.ITALIC, 30);
                jA.setFont(font);
                if (flag) {
                    MapFrame.panelArea_Button.get("H").setTTF(file.getPath());
                } else {
                    MapFrame.panelArea_Button.get("G").setTTF(file.getPath());
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "请选择.ttf格式的字体文件~", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void insertDocM(boolean flag) { //insert wordDoc, if first insert, insert as 50(frontSize) else use current frontSize
        if (flagOfMap) {
            if (flag) {
                MapFrame.panelArea_Button.get("G").setDoc(mapSelect.getText(), mapColor, true, 50);
            } else {
                MapFrame.panelArea_Button.get("G").setNewDoc(mapSelect.getText(), mapColor);//use current frontSize
            }
        }
    }

    public void insertDocW(boolean flag) {//same as insert docM
        if (flagOfWord) {
            /*start convert string form Horizontal to Vertical
            /*e.g AbcD->A
                        bc
                          D
             */
            char blank = '　';
            String[] insertStr = wordSelect.getText().split("\n");

            int cols = insertStr.length;
            int blankLength = insertStr[0].length() / 3;
            int height = (cols - 1) * blankLength + (insertStr[0].length() + 1);
            for (int i = 1; i < insertStr.length; i++) {
                height = Math.max((cols - 1) * blankLength + (insertStr[i].length() + 1), height);
            }
            char[][] finalData = new char[height][cols];
            char[][] insertData = new char[cols][];
            for (int i = 0; i < cols; i++) {
                finalData[0][i] = blank;
                insertData[i] = insertStr[i].toCharArray();
            }
            for (int i = 0, cnt = 0; i < cols; i++) {
                for (int j = 1; j < height; j++) {
                    if (j > cnt * blankLength && j < insertData[i].length + cnt * blankLength + 1) {
                        finalData[j][i] = insertData[cnt][j - 1 - cnt * blankLength];
                    } else {
                        finalData[j][i] = blank;
                    }

                }
                cnt++;
            }
            StringBuilder str = new StringBuilder();
            for (int i = 1; i < height; i++) {
                str.append(finalData[i]);
                str.append("\n");
            }
            //end convert
            if (flag) {//H is name of wordPanel
                MapFrame.panelArea_Button.get("H").setDoc(str.toString(), wordColor, false, 50);
            } else {
                MapFrame.panelArea_Button.get("H").setNewDoc(str.toString(), wordColor);
            }
        }
    }

    /////////////////////////////改变校徽
    public void changeLogo() {
        JFileChooser jFileChooser = new JFileChooser(new File("."));
        int status = jFileChooser.showOpenDialog(null);
        if (status == JFileChooser.APPROVE_OPTION) {
            File file = jFileChooser.getSelectedFile();
            try {
                MapFrame.changeLogoTo(ImageIO.read(file), MapFrame.getLogoRate());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                        "图片读取失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
