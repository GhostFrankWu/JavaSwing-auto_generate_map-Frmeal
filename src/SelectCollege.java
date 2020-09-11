import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

public class SelectCollege extends JTextField {
    private final DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
    private final ArrayList<String> items = new ArrayList<>();
    private final ArrayList<College> CollegeArray = new ArrayList<>();
    private final JComboBox<String> jComboBox = SelectCollegeBox();

    public College findCollegeByName(String s){
        for (College college : CollegeArray) {
            if (college.getName().equals(s))
                return college;
        }
        return null;
    }

    private static void setAdjusting(JComboBox<String> cbInput, boolean adjusting) {
        cbInput.putClientProperty("is_adjusting", adjusting);
    }

    public void setSelected(){
        if(jComboBox.getSelectedItem()!=null)
        setText(Objects.requireNonNull(jComboBox.getSelectedItem()).toString());
    }

    public SelectCollege() {
        add(jComboBox, BorderLayout.SOUTH);
        //jComboBox.setSelectedIndex(0);
        getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updateList();
            }

            public void removeUpdate(DocumentEvent e) {
                updateList();
            }

            public void changedUpdate(DocumentEvent e) {
                updateList();
            }

            private void updateList() {
                setAdjusting(jComboBox, true);
                model.removeAllElements();
                String input = getText();
                if (!input.isEmpty()) {
                    for (String item : items) {
                        if (item.toLowerCase().startsWith(input.toLowerCase())) {
                            model.addElement(item);
                        }
                    }
                }
                jComboBox.setPopupVisible(model.getSize() > 0);
                setAdjusting(jComboBox, false);
            }
        });
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                setAdjusting(jComboBox, true);
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (jComboBox.isPopupVisible()) {
                        e.setKeyCode(KeyEvent.VK_ENTER);
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_UP ||
                        e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_TAB) {
                    e.setSource(jComboBox);
                    jComboBox.dispatchEvent(e);
                    if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_TAB) {
                        setText(Objects.requireNonNull(jComboBox.getSelectedItem()).toString());
                        jComboBox.setPopupVisible(false);
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    jComboBox.setPopupVisible(false);
                }
                setAdjusting(jComboBox, false);
            }
        });
    }

    public ArrayList<College> getCollegeArray(){
        return CollegeArray;
    }

    public JComboBox<String> SelectCollegeBox() {
        JComboBox<String> jComboBox = new JComboBox<>(model);
        getProvinceFromData();
        jComboBox.setSize(250, 0);
        jComboBox.setLocation(0, 30);
        CollegeArray.sort(College::compByName);
        for (College college : CollegeArray) {
            jComboBox.addItem(college.getName());
            items.add(college.getName());
        }
        return jComboBox;
    }

    public void getProvinceFromData() {
        File filename = new File("res/data.txt");
        InputStreamReader reader;
        CollegeArray.add(new College("请选择学校","请选择城市"));
        try {
            reader = new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(reader);
            String line;
            line = br.readLine();
            while (line != null) {
                if (line.split(" split ").length > 1) {
                    CollegeArray.add(new College(line.split(" split ")[0], line.split(" split ")[1]));
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "大学信息丢失", "错误", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}
