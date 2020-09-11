import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Selection extends JFrame {
    private final int this_with = 1000, this_height = 600;
    protected static final SelectCollege select = new SelectCollege();
    private Timer timer;
    private final ArrayList<College> allCollege = new ArrayList<>();
    private final String[] titles = {"虽相见恨晚，但终将一路同行", "送君千里，终须一别", "最美好的时光，最青涩的季节", "所以词穷致谢，因为来日方长",
            "无所谓前程似锦，只要问心无愧", "但愿花开如常你会笑着抬头望", "青春时光，友情岁月，愿彼此是记忆里最美的画面", "悟以往之不谏，知来者可追",
            "愿你以梦为马，莫负韶华，时光匆匆，我们都不会太平庸。"};
    private static final DefaultTableModel defaultTableModel = new DefaultTableModel(null, new String[]{"姓名", "录取大学", "所在城市"}) {
        public boolean isCellEditable(int row, int column) {
            return column != 2;
        }
    };
    public static final JTable jTable = new JTable(defaultTableModel);
    private final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(defaultTableModel);
    private final JComboBox<String> cityComboBox = new JComboBox<>();
    private final JTextField nameSelect = new JTextField();
    private final String rem = ":rem:";
    private ArrayList<String> falseList = new ArrayList<>();
    private final int[] index = {0};
    public static JScrollPane jScrollPane = new JScrollPane(jTable);
    public JPanel nullPanel = new JPanel();

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

    class add extends Thread{
        public void run(){
            try{
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
            boolean flag=true;
            File filename = new File("newData.txt");
            InputStreamReader reader;
            try {
                reader = new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(reader);
                String line;
                line = br.readLine();
                while (line != null) {
                    if (line.split(rem).length > 1) {
                        String school;
                        school = findProvinceFree(line.split(rem)[1].trim());
                        if (school.equals("false")) {
                            falseList.add("同学姓名：" + line.split(rem)[0] + "\t学校：" + line.split(rem)[1] + "\n");
                        } else {
                            newStudent(line.split(rem)[0].trim(), line.split(rem)[1].trim(), school);
                        }
                    }
                    line = br.readLine();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "excel文档读取失败=", "错误", JOptionPane.ERROR_MESSAGE);
                flag=false;
            }
            if(flag) {
                if (falseList.size() == 0) {
                    JOptionPane.showMessageDialog(null,
                            "excel文档导入成功", "棒棒哒~~", JOptionPane.ERROR_MESSAGE);
                } else {
                    StringBuilder msg = new StringBuilder();
                    msg.append("不在列表里的学校，请手动添加:\n");
                    for (String s : falseList) {
                        msg.append(s);
                    }
                    JOptionPane.showMessageDialog(null,
                            msg, "非常抱歉~~", JOptionPane.ERROR_MESSAGE);
                    falseList.clear();
                }
            }
        }
    }

    class refresh extends Thread {
        public void run() {
            Timer timerX = new Timer(60, e -> {
                if (isVisible()) {
                    repaint();
                }
            });
            timerX.start();
        }
    }

    class async extends Thread {
        public void run() {
            timer = new Timer(200, e -> {
                if (MapFrame.finishUpd) {
                    timer.stop();
                    cityComboBox.addItem("");
                    MapFrame.province_Button.forEach((k, v) ->
                            cityComboBox.addItem(v.ch_name));
                    repaint();
                }
            });
            timer.start();
        }
    }

    public Selection() {
        setSize(this_with, this_height);//大小
        setLocationRelativeTo(null); // Center the window.
        setLayout(null);
        setTitle("==============每一次键入，都是回忆的蔓延==============");
        this.setIconImage(new ImageIcon("res/logo.jpg").getImage());
        JLabel background = new JLabel();
        background.setLocation(0, 0);
        background.setSize(this_with, this_height);
        try {
            BufferedImage orin_img = (ImageIO.read(new File("res/bgOfSelect.jpg")));
            BufferedImage newImage = new BufferedImage(this_with, this_height, orin_img.getType());
            Graphics g = newImage.getGraphics();
            g.drawImage(orin_img, 0, 0, this_with, this_height, null);
            g.dispose();
            orin_img = newImage;
            background.setIcon(new ImageIcon(orin_img));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "初始化背景失败", "注意", JOptionPane.INFORMATION_MESSAGE);
        }
        jTable.setRowSorter(sorter);
        initButton();
        SelectBox();
        add(background);
    }

    public void sort(String s) {
        sorter.setRowFilter(RowFilter.regexFilter(s));
    }

    private void deleteStudent() {
        int selectRows = jTable.getSelectedRows().length;// 取得用户所选行的行数
        DefaultTableModel tableModel = (DefaultTableModel) jTable.getModel();
        if (selectRows >= 1) {
            int[] selRowIndex = jTable.getSelectedRows();// 用户所选行的序列
            int j = selRowIndex[0];
            for (int i = 0; i < selRowIndex.length; i++)
                tableModel.removeRow(j);
        }
    }

    private void insertStudent() {
        JFileChooser jFileChooser = new JFileChooser(new File("."));
        int status = jFileChooser.showOpenDialog(null);
        if (status == JFileChooser.APPROVE_OPTION) {
            File file = jFileChooser.getSelectedFile();
            boolean flag = true;
            try {
                Runtime.getRuntime().exec("res\\xls.exe " + file);//exec族安全性挨打，要改
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                        "excel文档读取失败", "错误", JOptionPane.ERROR_MESSAGE);
                flag = false;
            }
            if (flag) {
                new add().start();
            }
        }
    }

    private void addStudent() {
        if (nameSelect.getText() != null && select.getText() != null && cityComboBox.getSelectedItem() != null) {
            if (!nameSelect.getText().equals("") && !select.getText().equals("") && cityComboBox.getSelectedItem() != "") {
                addDefaultStudent(newFreeStudent(nameSelect.getText(), select.getText(), cityComboBox.getSelectedItem().toString()));
            }
        }
    }

    public void newStudent(String name, String school, String province) {
        addDefaultStudent(newFreeStudent(name, school, province));
    }

    public void addDefaultStudent(Student stu) {
        ((DefaultTableModel) jTable.getModel()).addRow(
                new Object[]{stu.getNameOfStudent(), stu.getCollege().getName(), stu.getCollege().getProvince()});
        jTable.invalidate();
    }

    public Student newFreeStudent(String stu, String college, String province) {
        Student STU = new Student();
        College addCollege = select.findCollegeByName(college);
        if (addCollege == null) {
            addCollege = new College(college, province);
        }
        STU.SetName(stu);
        STU.setCollege(addCollege);
        return STU;
    }

    private void initButton() {
        MealButton deleteButton = new MealButton("删除所选同学信息", 220, 50);
        deleteButton.setLocation(740, this_height - 135);
        deleteButton.addActionListener(e -> {
            repaint();
            deleteStudent();
        });
        add(deleteButton);

        MealButton cacheButton = new MealButton("缓存当前基本信息", 220, 50);
        cacheButton.setLocation(10, this_height - 135);
        cacheButton.addActionListener(e -> {
            cache();
            repaint();
        });
        add(cacheButton);

        MealButton loadButton = new MealButton("读取缓存文件信息", 220, 50);
        loadButton.setLocation(245, this_height - 135);
        loadButton.addActionListener(e -> deCache());
        add(loadButton);

        MealButton insertButton = new MealButton("批量导入同学信息", 220, 50);
        insertButton.setLocation(505, this_height - 135);
        insertButton.addActionListener(e -> {
            insertStudent();
            repaint();
        });
        add(deleteButton);
        add(insertButton);

        MealButton addButton = new MealButton("添加同学", 85, 30);
        addButton.setLocation(265, 60);
        addButton.addActionListener(e -> {
            repaint();
            addStudent();
        });
        add(addButton);
    }

    private void SelectBox() {
        nullPanel.setBounds(100, 130, 250, 150);
        nullPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                select.setSelected();
            }
        });
        nullPanel.setBackground(new Color(0, 0, 0, 0));
        add(nullPanel);
        PanelArea info1 = new PanelArea();
        info1.setPoint(400, 0, this_with - 200, 50);
        info1.refreshLocation();
        info1.setDocs("新增同学档案", Color.YELLOW, true, 30);
        add(info1);
        PanelArea info2 = new PanelArea();
        info2.setPoint(0, 65, this_with, this_height);
        info2.refreshLocation();
        info2.setDocs("同学姓名：\n录取大学：\n\n\n\n\n\n                    所在城市自动补全 \n 所在城市：\n                  如果没有补全请手动选择"
                , Color.BLUE, false, 20);
        info2.setEditable(true);
        add(info2);

        cityComboBox.setBounds(100, 275, 250, 30);
        add(cityComboBox);
        nameSelect.setBounds(100, 60, 150, 30);
        add(nameSelect);
        select.setLocation(100, 100);
        select.setSize(250, 30);
        select.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                findProvince(select.getText());
            }

            public void removeUpdate(DocumentEvent e) {findProvince(select.getText());
            }

            public void changedUpdate(DocumentEvent e) {
                findProvince(select.getText());
            }
        });
        cityComboBox.addMouseListener(new MouseAdapter() {
            boolean flagOfMouse = true;

            public void mouseEntered(MouseEvent e) {
                flagOfMouse = false;
            }

            public void mouseExited(MouseEvent e) {
                flagOfMouse = true;
            }
        });

        record1();

        jTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
        jTable.setFillsViewportHeight(true);
        jScrollPane.setBounds(360, 60, 600, this_height - 200);
        add(jScrollPane);

        add(select);
        Thread refresh = new refresh();
        Thread changeT = new changeTitle();
        Thread async = new async();
        refresh.start();
        async.start();
        changeT.start();
    }

    private void findProvince(String str) {
        if (str != null && !str.equals("")) {
            for (College col : select.getCollegeArray()) {
                if (col.getName().equals(str)) {
                    cityComboBox.setSelectedItem(col.getProvince());
                }
            }
        }
    }

    private String findProvinceFree(String str) {
        if (str != null && !str.equals("")) {
            for (College col : select.getCollegeArray()) {
                if (col.getName().equals(str)) {
                    return col.getProvince();
                }
            }
        }
        return "false";
    }

    private void record1() {
        File filename = new File("res/stuCache.txt");
        InputStreamReader reader;

        try {
            reader = new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(reader);
            String line;
            line = br.readLine();
            while (line != null) {
                if (line.split(rem).length > 2) {
                    newStudent(line.split(rem)[0], line.split(rem)[1], line.split(rem)[2]);
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "学生缓存信息丢失", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void deCache(){
        ArrayList<String> stuData=new ArrayList<>();
        ArrayList<String> mapData=new ArrayList<>();
        JFileChooser jFileChooser = new JFileChooser(new File("."));
        int status = jFileChooser.showOpenDialog(null);
        if (status == JFileChooser.APPROVE_OPTION) {
            try {
                File file = jFileChooser.getSelectedFile();
                InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(reader);
                String line;
                line = br.readLine();
                while (line != null) {
                    stuData.add(line);
                    line = br.readLine();
                    if(line.contains("##rem##"))
                        break;
                }
                line = br.readLine();
                while (line != null) {
                    mapData.add(line);
                    line = br.readLine();
                }

                file = new File("res/stuCache.txt");
                if (!file.exists()) {
                    if (!file.createNewFile()) {
                        JOptionPane.showMessageDialog(null,
                                "创建缓存文件失败", "注意", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                }
                FileWriter fileWriter = new FileWriter(file.getPath());
                fileWriter.write("本文件是学生信息的临时缓存，方便再次打开程序时继续编辑。\r\n");
                for (String str : stuData) {
                    fileWriter.write(str+"\r\n");
                }
                fileWriter.flush();
                fileWriter.close();
                file = new File("res/mapCache.txt");
                if (!file.exists()) {
                    if (!file.createNewFile()) {
                        JOptionPane.showMessageDialog(null,
                                "创建缓存文件失败", "注意", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                }
                fileWriter = new FileWriter(file.getPath());
                fileWriter.write("本文件是地图信息的临时缓存，方便再次打开程序时继续编辑。\r\n");
                for (String str : mapData) {
                    fileWriter.write(str+"\r\n");
                }
                fileWriter.flush();
                fileWriter.close();
            }catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "缓存文件文档读取失败", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            DefaultTableModel tableModel = (DefaultTableModel) jTable.getModel();
            while (tableModel.getRowCount()!=0) {
                tableModel.removeRow(0);
            }
            record1();
            MapFrame.record1();
        }
    }

    private void cache(){
        ArrayList<String> allData=new ArrayList<>();
        record0();
        cache0();
        File filename = new File("res/stuCache.txt");
        InputStreamReader reader;
        int indexData=0;
        try {
            reader = new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(reader);
            String line;
            line = br.readLine();
            while (line != null) {
                allData.add(line);
                line = br.readLine();
            }
            allData.add("##rem##");
            filename = new File("res/mapCache.txt");
            reader = new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8);
            br = new BufferedReader(reader);
            line = br.readLine();
            while (line != null) {
                allData.add(line);
                line = br.readLine();
            }
            File file = new File("我的缓存0.txt");
            while (file.exists()) {
                file = new File("我的缓存"+(++indexData)+".txt");
            }
            if (!file.createNewFile()) {
                JOptionPane.showMessageDialog(null,
                        "创建缓存文件失败", "注意", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            FileWriter fileWriter = new FileWriter(file.getPath());
            fileWriter.write("本文件是所有信息的临时缓存，方便再次打开程序时继续编辑。\r\n");
            for (String str : allData) {
                fileWriter.write(str + "\r\n");
            }
            fileWriter.flush();
            fileWriter.close();
        }catch (Exception e){
            JOptionPane.showMessageDialog(null,
                    "创建缓存文件失败", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(null,
                "成功创建缓存文件："+"我的缓存"+(indexData)+".txt", "成功~~", JOptionPane.PLAIN_MESSAGE);
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void record0() {
        DefaultTableModel tableModel = (DefaultTableModel) jTable.getModel();
        int cntOfRow = tableModel.getRowCount();
        String[][] members = new String[cntOfRow][3];
        for (int i = 0; i < cntOfRow; i++) {
            for (int j = 0; j < 3; j++) {
                members[i][j] = ((String) tableModel.getValueAt(i, j));
            }
        }
        try {
            File file = new File("res/stuCache.txt");
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    JOptionPane.showMessageDialog(null,
                            "创建缓存文件失败", "注意", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }
            FileWriter fileWriter = new FileWriter(file.getPath());
            fileWriter.write("本文件是学生信息的临时缓存，方便再次打开程序时继续编辑。\r\n");
            for (String[] str : members) {
                fileWriter.write(str[0] + rem + str[1] + rem + str[2] + "\r\n");
            }
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "写入缓存文件失败", "注意", JOptionPane.INFORMATION_MESSAGE);
        }
        cache0();
    }

    public void cache0() {
        String[] colorData = new String[MapFrame.province_Button.size()];
        MapFrame.province_Button.forEach((k, v) ->
                colorData[index[0]++] = k + "," + v.currentColor.getRed() + "," + v.currentColor.getGreen() + "," + v.currentColor.getBlue() + "," + v.currentColor.getAlpha());
        index[0]=0;
        try {
            File file = new File("res/mapCache.txt");
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    JOptionPane.showMessageDialog(null,
                            "创建缓存文件失败", "注意", JOptionPane.INFORMATION_MESSAGE);
                }
                return;
            }
            FileWriter fileWriter = new FileWriter(file.getPath());
            fileWriter.write("本文件是地图信息的临时缓存，方便再次打开程序时继续编辑。\r\n");
            for (String str : colorData) {
                fileWriter.write(str + "\r\n");
            }
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "写入缓存文件失败", "注意", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public ArrayList<College> getAllStudentData() {
        if (!allCollege.isEmpty()) {
            for (College col : allCollege) {
                col.clear();
            }
        }
        ArrayList<College> members = new ArrayList<>();
        DefaultTableModel tableModel = (DefaultTableModel) jTable.getModel();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Student STU = new Student();
            STU.SetName((String) tableModel.getValueAt(i, 0));
            College addCollege = select.findCollegeByName((String) tableModel.getValueAt(i, 1));
            if (addCollege == null) {
                addCollege = new College((String) tableModel.getValueAt(i, 1)
                        , (String) tableModel.getValueAt(i, 2));
            }
            allCollege.add(addCollege);
            STU.setCollege(addCollege);
            if (!members.contains(addCollege))
                members.add(addCollege);
        }
        return members;
    }

}
