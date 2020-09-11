import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

/***
 * main class of this program
 * HashMaps are used to storage Buttons, Select boxes, decorations and word areas.
 * static JButtons is for upd via other classes
 * static int is for quick upd(debug
 * logo_rate is default compass rate of logoImg
 * private final Label are for init via different method
 * self defined classes are init for upd visible state
 */
public class MapFrame extends JFrame {
    public static HashMap<String, Province> province_Button = new HashMap<>();
    public static HashMap<String, Point> province_Location = new HashMap<>();
    public static HashMap<String, PanelAreaLabel> panelArea_Button = new HashMap<>();
    public static HashMap<String, ArrayList<Point[]>> lines = new HashMap<>();
    public static HashMap<String, JButton> starButtons = new HashMap<>();
    public static JButton currentButton, bgi_Logo = new JButton(), tool_button = new JButton();
    public static int offset = 5, this_with = 1440, this_height = 800, border = 230,
            offset_lineBonds = 2, offset_x = 0, offset_y = 0;
    private static double logo_rate = 0.5;
    private final JLabel bgi_China = new JLabel();
    private final File bg_logo_pth = new File("res/xh.jpg");
    public static final Selection selectionDialog = new Selection();
    public static JPanel linePanel;
    protected static final ProvinceInfo myInfo = new ProvinceInfo();
    private final ArtDialog artDialog = new ArtDialog();
    private final CameraTest cam = new CameraTest();
    private static final ArrayList<JButton> hideButton = new ArrayList<>();
    public static boolean finishUpd = false;
    protected static boolean flagOfStar = false, flagOfMade = false, flagOfDraw = true, flagOfInsert = false,
            flagOfFirstFormat=false,flagOfLarge = false;// define select Boxes and some progress
    private ImageIcon starBG = null;
    public static Color graphColor = new Color(0, 255, 255,140);//default color of lines

    public static void main(String[] args) { // new MainFrame.show
        new MapFrame();
    }

    static class shot extends Thread {//used to hide all buttons for user in 1S
        public void run() {
            for (JButton bt : hideButton) {
                bt.setVisible(false);//hide all buttons
            }
            try {
                Thread.sleep(1000);//Delay 1s for users
            } catch (Exception ignored) {
            }
            for (JButton bt : hideButton) {
                bt.setVisible(true);//show all buttons
            }
        }
    }

    class delayForShot extends Thread {// get a screenshot, use Thread to get a 0.05S windowMax size
        public void run() {
            for (JButton bt : hideButton) {
                bt.setVisible(false);
            }
            try {
                Thread.sleep(50);
            } catch (Exception ignored) {
            }
            cam.snapShot();
            for (JButton bt : hideButton) {
                bt.setVisible(true);
            }
            setExtendedState(JFrame.NORMAL);//change Frame size to normal
        }
    }

    class startReformatTextPanel extends Thread {
        public void run() {
            new Timer(10000, e -> System.gc()).start();//advice JVM to run a GC for fluent use (not sure
            new Timer(50, e -> repaint()).start();//repaint to avoid show bugs (blank area..etc
            new reformatTextPanel().start();//auto reformat thread
        }
    }

    static class reformatTextPanel extends Thread {
        public void run() {
            Timer timerX = new Timer(10, e -> {
                if (!flagOfInsert) {//if not inserting, format (otherwise format while insert will cause bugs(unsafe malty Thread)
                    try {
                        panelArea_Button.forEach((k, v) -> {
                            flagOfLarge = true;
                            for (RealPanel pa : v.panels) {
                                flagOfLarge = pa.getLarge() && flagOfLarge;
                            }
                            if (flagOfLarge) {
                                v.reFormat();
                            }
                            hideButton.get(14).setBorderPainted(true);//refresh any button can keep frame(it fix this way
                            hideButton.get(14).setBorderPainted(false);//fresh, or JScrollbar will not show(don't know why
                        });
                    } catch (Exception ignored) {
                    }
                }
            });
            timerX.start();
        }
    }
    public MapFrame() { ///////////////////////////标题初始化&图标初始化&界面框架初始化
        Thread ref = new startReformatTextPanel();
        ref.start();
        setSize(this_with, this_height);//大小
        setLocationRelativeTo(null); // Center the window.
        setLayout(null);
        String blank_blank = "                                      ";
        setTitle("蹭饭地图生成器" + blank_blank + blank_blank + blank_blank + blank_blank + "V2.0 beta By.Frank");
        this.setIconImage(new ImageIcon("res/logo.jpg").getImage());

        //basic style init
        initBkgLo();
        initProvinceAndPane();
        initDebugButton();
        //init buttons
        for (JButton bt : hideButton) {
            this.add(bt);
        }
        starButtons.forEach((k, v) -> this.add(v));
        initLinePanel();
        province_Button.forEach((k, v) -> {
            this.add(v);
            v.changeColor();
        });
        offset(-20, 0);
        this.add(bgi_China);
        //add init buttons in order(first in is upper

        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {//cache when exit
            public void windowClosing(WindowEvent e) {
                try {
                    selectionDialog.record0();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null,
                            "尝试缓存学生信息失败！", "提示", JOptionPane.INFORMATION_MESSAGE);
                }
                super.windowClosing(e);
            }
        });
        record1();//read cache
        setExtendedState(JFrame.NORMAL);//pop up
        finishUpd = true;//thread safe
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////结束
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////省份按钮初始化
    private Province makeProvince(String province, int x, int y, String ch, int real_x, int real_y) {
        String icon_path = "bg/" + province + ".png";
        Province bt = null;//for every province button, add a star logo(default hide
        JButton star = new JButton(starBG);
        star.setSize(12, 18);
        star.setBorderPainted(false);
        star.setContentAreaFilled(false);
        star.setVisible(false);
        star.setLocation(x + real_x + offset_x + offset - 30, y + real_y + offset_y - 6);
        starButtons.put(province, star);
        try {
            ImageIcon button_icon = new ImageIcon(icon_path);
            bt = new Province(button_icon, icon_path, province, ch, real_x, real_y);
            bt.setLocation(x, y);
            province_Location.put(ch, new Point(x, y));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "初始化省份轮廓失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
        return bt;
    }
    /////////////////////////////////////////////////////////////////////////////ED

    ///////////////////////////////////////////////////////////////////读取缓存
    public static void record1() {//Location and Color cache of Province
        File filename = new File("res/mapCache.txt");
        InputStreamReader reader;
        try {
            reader = new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(reader);
            String line;
            line = br.readLine();
            while (line != null) {
                if (line.split(",").length > 4) {
                    String[] str = line.split(",");
                    province_Button.get(str[0]).changeColorTo(new Color(Integer.parseInt(str[1]),
                            Integer.parseInt(str[2]), Integer.parseInt(str[3]), Integer.parseInt(str[4])));
                }
                line = br.readLine();
            }
        } catch (IOException ignored) {
        }
    }

    /////////////////////////////////////////////////////////////////////////////ED

    public void offset(int x, int y) {//change location of all Provinces (China
        offset_x = x;
        offset_y = y;
        province_Button.forEach((k, v) ->
                v.setLocation(v.getX() + x, v.getY() + y));
    }

    public void initBkgLo() {//just init background
        bgi_Logo.setLocation(this_with - 280, 620);
        bgi_Logo.setBorderPainted(false);
        bgi_Logo.setContentAreaFilled(false);
        bgi_Logo.addActionListener(e ->
                currentButton = bgi_Logo
        );
        add(bgi_Logo);
        currentButton = tool_button;
        bgi_China.setLocation(0, 0);
        bgi_China.setBounds(0, 0, this_with, this_height);
        try {
            int s_rand = (int) (Math.random() * 3) + 1;
            changeBackgroundTo(ImageIO.read(new File("res/bg" + s_rand + ".jpg")));
            changeLogoTo(ImageIO.read(bg_logo_pth), 0.4);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "初始化默认背景失败", "注意", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void initDebugButton() {////////////////////初始化方向键
        MealButton qqButton = new MealButton("星空风格", 180, 50, 50, false);
        qqButton.setLocation(this_with - 229, 355);
        qqButton.addActionListener(e -> {
            flagOfStar = !flagOfStar;
            changeModeToStar();
        });
        hideButton.add(qqButton);

        MealButton drawButton = new MealButton("省份染色", 180, 50, 50, true);
        drawButton.setLocation(this_with - 229, 405);
        drawButton.addActionListener(e -> {
            flagOfDraw = !flagOfDraw;
            if (flagOfDraw) {
                province_Button.forEach((k, v) ->
                        v.changeColorByStudent()
                );
            } else {
                province_Button.forEach((k, v) -> {
                    if (flagOfStar) {
                        v.changeColorTo(Province.null_Star_Color);
                    } else {
                        v.changeColorTo(Province.null_nonStar_Color);
                    }
                });
            }
        });
        hideButton.add(drawButton);

        MealButton selectIButton = new MealButton("录入同学信息", 170, 50);
        selectIButton.setLocation(this_with - 229, 153);
        selectIButton.addActionListener(e -> {
            selectionDialog.setVisible(true);
            selectionDialog.sort("");
            selectionDialog.setExtendedState(JFrame.NORMAL);
            Selection.jScrollPane.setViewportView(Selection.jTable);
        });
        hideButton.add(selectIButton);

        MealButton changeButton = new MealButton("更换地图背景", 170, 50);
        changeButton.setLocation(this_with - 229, 203);
        changeButton.addActionListener(e -> changeBackground());
        hideButton.add(changeButton);

        MealButton changeLogoButton = new MealButton("高级风格设置", 170, 50);
        changeLogoButton.setLocation(this_with - 229, 253);
        changeLogoButton.addActionListener(e -> {
            artDialog.refreshData();
            artDialog.setVisible(true);
            artDialog.setExtendedState(JFrame.NORMAL);//pop up
        });
        hideButton.add(changeLogoButton);

        MealButton makeButton = new MealButton("生成蹭饭地图", 170, 50);
        makeButton.setLocation(this_with - 229, 302);
        makeButton.addActionListener(e -> {
            flagOfMade = true;
            formatDataOfStudent();
            if(!flagOfFirstFormat){
                flagOfMade=true;
                formatDataOfStudent();//main method
                flagOfFirstFormat=true;//debug
            }
        });
        hideButton.add(makeButton);

        MealButton imgButton = new MealButton("保存当前截图", 170, 50);
        imgButton.setLocation(this_with - 229, 460);
        imgButton.addActionListener(e -> {
            this.setExtendedState(JFrame.MAXIMIZED_BOTH);
            Thread delay = new delayForShot();
            delay.start();
        });
        hideButton.add(imgButton);

        MealButton about = new MealButton("蒟蒻的开发者", 170, 50);
        about.setLocation(this_with - 229, 510);
        about.addActionListener(e -> {
            String url = "http://106.52.237.196/reme.html";
            try {
                java.net.URI uri = java.net.URI.create(url);
                java.awt.Desktop dp = java.awt.Desktop.getDesktop();
                if (dp.isSupported(java.awt.Desktop.Action.BROWSE)) {
                    dp.browse(uri);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,
                        "没有成功打开浏览器，您可以手动访问" + url, "--啊哦--", JOptionPane.INFORMATION_MESSAGE);
            }

        });
        hideButton.add(about);

        //these keys ard mainly for debug, after develop, I kept them for little usages
        //current button is decide which button to move, tool button means allProvince(China
        MealButton up = new MealButton(50, 50, MealButton.UP);
        up.setLocation(50 + (this_with - border), 0);
        up.addActionListener(e -> {
            if (currentButton != tool_button) {
                currentButton.setLocation(currentButton.getX(), currentButton.getY() - offset);
            } else {
                offset(0, -offset);
            }
        });
        hideButton.add(up);

        MealButton down = new MealButton(50, 50, MealButton.DOWN);
        down.setLocation(50 + (this_with - border), 100);
        down.addActionListener(e -> {
            if (currentButton != tool_button) {
                currentButton.setLocation(currentButton.getX(), currentButton.getY() + offset);
            } else {
                offset(0, offset);
            }
        });
        hideButton.add(down);

        MealButton left = new MealButton(50, 50, MealButton.LEFT);
        left.setLocation((this_with - border), 50);
        left.addActionListener(e -> {
            if (currentButton != tool_button) {
                currentButton.setLocation(currentButton.getX() - offset, currentButton.getY());
            } else {
                offset(-offset, 0);
            }
        });
        hideButton.add(left);

        MealButton right = new MealButton(50, 50, MealButton.RIGHT);
        right.setLocation(100 + (this_with - border), 50);
        hideButton.add(right);
        right.setSize(50, 50);
        right.addActionListener(e -> {
            if (currentButton != tool_button) {
                currentButton.setLocation(currentButton.getX() + offset, currentButton.getY());
            } else {
                offset(offset, 0);
            }
        });

        MealButton cent = new MealButton(50, 50, MealButton.PLUS);
        cent.setLocation(50 + (this_with - border), 50);
        hideButton.add(cent);
        cent.addActionListener(e -> new shot().start());

        MealButton plus = new MealButton(50, 50, MealButton.PLUS);
        plus.setLocation(150 + (this_with - border), 0);
        plus.addActionListener(e -> {
            if (currentButton != bgi_Logo) {
                offset++;
            } else {
                logo_rate += 0.1;
                try {
                    changeLogoTo(ImageIO.read(bg_logo_pth), logo_rate);
                } catch (IOException ignored) {
                }
            }
        });
        hideButton.add(plus);

        MealButton sub = new MealButton(50, 50, MealButton.MINUS);
        sub.setLocation(150 + (this_with - border), 100);
        sub.addActionListener(e -> {
            if (currentButton != bgi_Logo) {
                offset--;
            } else {
                logo_rate -= 0.1;
                try {
                    changeLogoTo(ImageIO.read(bg_logo_pth), logo_rate);
                } catch (IOException ignored) {
                }
            }
        });
        hideButton.add(sub);

        JButton func = new JButton("@");//actually this buttons is used for debug and I didn't delete it
        //I change it size to mini and hide it behind other buttons. Maybe in next(if have) version, it will become a bonus :-D
        func.setLocation(this_with - 150, 300);
        func.setSize(20, 20);
        func.addActionListener(e -> System.out.println(currentButton.getX() + "," + currentButton.getY() + ");"));
        hideButton.add(func);
    }

    ///////////////////////////////////////////风格调整
    public void changeModeToStar() {
        if (flagOfStar) {//if mode is star:
            if (ArtDialog.wordSelect.getText().equals("时间如风，记忆入壶\n自此别后,望君珍重")) {//if is default string, change it
                ArtDialog.wordSelect.setText("聚是一团火\n散做满天星");
            }
            try {
                changeBackgroundTo(ImageIO.read(new File("res/bg-star" + ((int) (Math.random() * 2) + 1) + ".jpg")));
            } catch (IOException ignored) {
            }
            Province.NullColor = Province.null_Star_Color;
            province_Button.forEach((k, v) -> {
                if(v.chooseColor==null) {
                    v.changeColorTo(Province.null_Star_Color);
                }
            });
            starButtons.forEach((k, v) -> {
                if (province_Button.get(k).allCollege.size() != 0) {
                    v.setVisible(true);//show star in province have students
                }
            });//back to default color
            //fixme next version, lock will make color unchangeable
            PanelAreaLabel.changeColors(new Color(240, 248, 255), new Color(225, 255, 255), new Color(245, 255, 250));
        } else {
            if (ArtDialog.wordSelect.getText().equals("聚是一团火\n散做满天星")) {
                ArtDialog.wordSelect.setText("时间如风，记忆入壶\n自此别后,望君珍重");
            }
            try {
                changeBackgroundTo(ImageIO.read(new File("res/bg" + ((int) (Math.random() * 3) + 1) + ".jpg")));
            } catch (IOException ignored) {
            }
            Province.NullColor = Province.null_nonStar_Color;
            province_Button.forEach((k, v) -> {
                if(v.chooseColor==null) {
                    v.changeColorTo(Province.null_nonStar_Color);
                }
            });
            starButtons.forEach((k, v) ->
                    v.setVisible(false)
            );//similar as above
            PanelAreaLabel.changeColors(Color.red, Color.lightGray, Color.yellow);
        }
        if (flagOfMade) {
            formatDataOfStudent();//if clicked(made), auto reformat data
            //fixme next version, auto fix format will nit need to reformat
        }
    }

    ///////////////////////////////////////文字区域格式调整

    private void formatDataOfStudent() {
        panelArea_Button.forEach((k, v) -> {
            for (RealPanel pa : v.panels) {
                remove(pa);
            }
        });
        flagOfInsert = true;
        panelArea_Button.forEach((k, v) -> v.removeData());
        province_Button.forEach((k, v) -> v.allCollege.clear());
        lines.forEach((k, v) -> v.clear());
        //finish init data
        if (ArtDialog.flagOfMap) {
            artDialog.insertDocM(true);
        }
        if (ArtDialog.flagOfWord) {
            artDialog.insertDocW(true);
        }
        //if map/word panel changed, insert it
        ArrayList<College> colleges = selectionDialog.getAllStudentData();
        for (College col : colleges) {
            province_Button.forEach((k, v) -> {
                if (v.ch_name.equals(col.getProvince())) {
                    v.allCollege.add(col);
                }
            });
        }
        //get all student data and add to colleges
        province_Button.forEach((k, v) -> {
            if (v.allCollege.size() != 0) {
                ArrayList<String> unFormatData = new ArrayList<>();
                unFormatData.add(v.ch_name + " \n");
                ArrayList<College> colData = v.allCollege;
                for (College col : colData) {
                    unFormatData.add(col.getName() + ": ");
                    ArrayList<Student> stu = col.getStudents();
                    StringBuilder str = new StringBuilder();
                    for (int j = 0; j < stu.size(); j++) {
                        if (j != stu.size() - 1) {
                            str.append(stu.get(j).getNameOfStudent()).append("、");
                        } else {
                            str.append(stu.get(j).getNameOfStudent()).append("\n");
                        }
                    }
                    unFormatData.add(str.toString());
                }
                //finish 生成 data of Stu Strings
                int indexOfPanelArea = 65, currentDistance = Integer.MAX_VALUE;
                Point p1 = v.getLocation();
                boolean allFull = true;
                for (int i = 65; i < 71; i++) {
                    if (!panelArea_Button.get((char) i + "").isFull()) {
                        if (colData.size() > 1 && (i == 67 || i == 68))
                            continue;
                        allFull = false;
                        Point p2 = panelArea_Button.get((char) i + "").getLocation();
                        Point p3 = new Point(panelArea_Button.get((char) i + "").getWidth(), panelArea_Button.get((char) i + "").getHeight());
                        int distance = (p2.x + (p3.x / 4) - p1.x) * (p2.x + (p3.x / 4) - p1.x) + (p2.y + (p3.y / 4) - p1.y) * (p2.y + (p3.y / 4) - p1.y);
                        indexOfPanelArea = distance < currentDistance ? i : indexOfPanelArea;
                        currentDistance = Math.min(distance, currentDistance);
                    }//auto choose closet textArea for Province
                    if (allFull) {
                            panelArea_Button.get((char) ((int) (Math.random() * 5) + 65) + "").forceReformat();
                    }//if no rest area, decrease fontSize of all province
                    //fixme next version, judge of full and reformat more wisely
                }
                panelArea_Button.get((char) indexOfPanelArea + "").insertData(unFormatData);
                offset(0, 0);
                starButtons.get(k).setLocation(v.getX() + v.realLocation.x + offset_x - 5, v.getY() + v.realLocation.y + offset_y - 5);
                lines.get((char) indexOfPanelArea + "").add(new Point[]{new Point(v.getX() + v.realLocation.x + offset_x,
                        v.getY() + v.realLocation.y + offset_y)});
            }
        });
        if (flagOfDraw) {
            province_Button.forEach((k, v) -> v.changeColorByStudent());
        }
        province_Button.forEach((k, v) -> remove(v));
        remove(linePanel);
        remove(bgi_China);
        panelArea_Button.forEach((k, v) -> {
            for (RealPanel pa : v.panels) {
                add(pa);
            }
        });
        add(linePanel);
        province_Button.forEach((k, v) -> add(v));
        add(bgi_China);
        repaint();
        //remove and re add to make panel always in front
        flagOfInsert = false;
    }

    /////////////曲线绘制函数
    public void initLinePanel() {
        linePanel = new JPanel() {
            public void paint(Graphics g) {
                g.setColor(graphColor);
                lines.forEach((k, v) -> {
                    int ind = 0;
                    for (Point[] p : v) {
                        g.fillOval(p[0].x-2,p[0].y-2,5,5);
                        for (int i = 0; i < offset_lineBonds; i++) {
                            Point px = panelArea_Button.get(k).panels.get(ind).getMyPoint();
                            g.drawLine(p[0].x + i, p[0].y, px.x > 0 ? p[0].x + i : px.x + i, px.y < 0 ? p[0].y : px.y);
                            g.drawLine(px.x > 0 ? p[0].x : px.x, px.y < 0 ? p[0].y + i : px.y + i, px.x, px.y + i);
                        }
                        ind++;
                    }
                });
            }
        };
        linePanel.setFocusable(false);
        linePanel.setLocation(0, 0);
        linePanel.setSize(this_with, this_height);
        add(linePanel);
    }

    ////////////省份初始化
    public void initProvinceAndPane() {
        //初始化图片
        try {
            int ht = 12, lh = 18;
            BufferedImage orin_img = (ImageIO.read(new File("res/star.jpg")));
            BufferedImage newImage = new BufferedImage(ht, lh,
                    orin_img.getType());
            Graphics g = newImage.getGraphics();
            g.drawImage(orin_img, 0, 0, ht, lh, null);
            g.dispose();
            orin_img = newImage;
            starBG = new ImageIcon(orin_img);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "初始化背景星失败", "注意", JOptionPane.INFORMATION_MESSAGE);
        }
        initProvince();
        for (int i = 65; i < 73; i++) {////////////初始化文字区域
            panelArea_Button.put((char) i + "", new PanelAreaLabel((char) i));
            lines.put((char) i + "", new ArrayList<>());//form A to K
        }
    }

    private void initProvince() {
        province_Button.put("hk", makeProvince("hk", 748, 671, "香港特别行政区", 748, 671));
        province_Button.put("ll", makeProvince("ll", 740, 222, "辽宁省", 64, 50));
        province_Button.put("jl", makeProvince("jl", 772, 164, "吉林省", 69, 47));
        province_Button.put("xz", makeProvince("xz", 67, 332, "西藏自治区", 159, 99));
        province_Button.put("tw", makeProvince("tw", 789, 610, "台湾省", 13, 30));
        province_Button.put("fj", makeProvince("fj", 711, 546, "福建省", 34, 47));
        province_Button.put("hn", makeProvince("hn", 609, 385, "河南省", 48, 52));
        province_Button.put("bj", makeProvince("bj", 688, 283, "北京市", 15, 20));
        province_Button.put("nmg", makeProvince("nmg", 410, 25, "内蒙古自治区", 203, 253));
        province_Button.put("js", makeProvince("js", 708, 404, "江苏省", 54, 28));
        province_Button.put("nx", makeProvince("nx", 510, 322, "宁夏回族自治区", 34, 39));
        province_Button.put("jx", makeProvince("jx", 667, 512, "江西省", 41, 50));
        province_Button.put("sc", makeProvince("sc", 387, 418, "四川省", 95, 80));
        province_Button.put("sd", makeProvince("sd", 683, 342, "山东省", 45, 46));
        province_Button.put("yn", makeProvince("yn", 368, 524, "云南省", 66, 105));
        province_Button.put("sh", makeProvince("sh", 795, 469, "上海市", 10, 15));
        province_Button.put("qh", makeProvince("qh", 262, 304, "青海省", 99, 73));
        province_Button.put("gd", makeProvince("gd", 595, 609, "广东省", 76, 35));
        province_Button.put("sx", makeProvince("sx", 527, 318, "陕西省", 56, 105));
        province_Button.put("hlj", makeProvince("hlj", 750, 17, "黑龙江省", 96, 124));
        province_Button.put("ah", makeProvince("ah", 686, 418, "安徽省", 48, 60));
        province_Button.put("gs", makeProvince("gs", 326, 237, "甘肃省", 182, 167));
        province_Button.put("hn3", makeProvince("hn3", 581, 721, "海南省", 16, 25));
        province_Button.put("hn2", makeProvince("hn2", 580, 515, "湖南省", 45, 59));
        province_Button.put("sx1", makeProvince("sx1", 608, 293, "山西省", 33, 75));
        province_Button.put("cq", makeProvince("cq", 521, 474, "重庆市", 44, 40));
        province_Button.put("zj", makeProvince("zj", 745, 484, "浙江省", 30, 45));
        province_Button.put("gx", makeProvince("gx", 497, 594, "广西省", 88, 54));
        province_Button.put("xj", makeProvince("xj", 37, 81, "新疆维吾尔自治区", 182, 177));
        province_Button.put("gz", makeProvince("gz", 485, 536, "贵州省", 65, 49));
        province_Button.put("tj", makeProvince("tj", 710, 301, "天津市", 11, 22));
        province_Button.put("hb2", makeProvince("hb2", 574, 452, "湖北省", 74, 43));
        province_Button.put("hb", makeProvince("hb", 660, 251, "河北省", 27, 92));
    }

    public static double getLogoRate() {
        return logo_rate;
    }

    public static void changeLogoTo(BufferedImage orin_img, double rate) {// refer method in Class "Province"
        int w = orin_img.getWidth(), h = orin_img.getHeight();
        int[] RGBs = new int[w * h];
        orin_img.getRGB(0, 0, w, h, RGBs, 0, w);
        for (int i = 0; i < w * h; i++) {
            if (RGBs[i] == -16777216) {
                RGBs[i] = 0;
            }
        }
        orin_img.setRGB(0, 0, w, h, RGBs, 0, w);
        bgi_Logo.setSize((int) (orin_img.getWidth() * rate), (int) (orin_img.getHeight() * rate));
        BufferedImage newImage = new BufferedImage((int) (orin_img.getWidth() * rate), (int) (orin_img.getHeight() * rate), orin_img.getType());
        Graphics g = newImage.getGraphics();
        g.drawImage(orin_img, 0, 0, (int) (orin_img.getWidth() * rate), (int) (orin_img.getHeight() * rate), null);
        g.dispose();
        orin_img = newImage;
        bgi_Logo.setIcon(new ImageIcon(orin_img));
    }
    /////////////////////////////////////////////////////////////////////////////////校徽结束

    //////////////////////////////////////////改变背景
    public void changeBackground() {
        JFileChooser jFileChooser = new JFileChooser(new File("."));
        int status = jFileChooser.showOpenDialog(null);
        if (status == JFileChooser.APPROVE_OPTION) {
            File file = jFileChooser.getSelectedFile();
            try {
                changeBackgroundTo(ImageIO.read(file));
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                        "图片读取失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void changeBackgroundTo(BufferedImage orin_img) {
        BufferedImage newImage = new BufferedImage(this_with, this_height, orin_img.getType());
        Graphics g = newImage.getGraphics();
        g.drawImage(orin_img, 0, 0, this_with, this_height, null);
        g.dispose();
        orin_img = newImage;
        bgi_China.setIcon(new ImageIcon(orin_img));
        repaint();
    }
    /////////////////////////////////////////////////////////////////////////////////背景结束
}
//地图调试代码