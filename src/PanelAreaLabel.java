import java.awt.*;
import java.util.ArrayList;

public class PanelAreaLabel {
    private int x1, x2, y1, y2;
    public int currentHeight = 0, lines = 0;
    private final char area_name;
    private boolean isFull = false, isCenter = false;
    private static final int ori_height = 75;
    private static Color provinceColor = Color.red, schoolColor = Color.lightGray, studentColor = Color.yellow;
    public final ArrayList<RealPanel> panels = new ArrayList<>();
    private String TTFPath;
    public boolean finish = false;

    public void getUpd(int n, int y) {
        for (int i = n + 1; i < panels.size(); i++) {
            panels.get(i).upLocation(y);
        }
    }

    public static void changeColors(Color a, Color b, Color c) {
        provinceColor = a;
        schoolColor = b;
        studentColor = c;
    }

    public void reFormat() {
        boolean flag = false;
        if (currentHeight > y2 - y1 - 15) {
            finish = false;
            for (RealPanel pa : panels) {
                flag = flag || !pa.getLarge();
            }
            if (!flag) {
                for (RealPanel pa : panels) {
                    pa.forceReformat();
                }
            }
        } else {
            finish = true;
            for (RealPanel pa : panels) {
                flag = flag || pa.judgeIfTextOutOfBounds();
            }
            if (!flag) {
                for (RealPanel pa : panels) {
                    pa.getLarge();
                }
            }
        }
    }

    public void forceReformat() {
        for (RealPanel panel : panels) {
            panel.forceReformat();
        }
    }

    public static Color getProvinceColor() {
        return provinceColor;
    }

    public static Color getSchoolColor() {
        return schoolColor;
    }

    public static Color getStudentColor() {
        return studentColor;
    }

    public static void changeColors(Color a, int i) {
        if (i == 1) {
            provinceColor = a;
        } else if (i == 2) {
            schoolColor = a;
        } else {
            studentColor = a;
        }
    }

    public void removeData() {
        currentHeight = 0;
        for (RealPanel pa : panels) {
            pa.removeData();
        }
        panels.clear();
        isFull = false;
        lines = 0;
    }

    public PanelAreaLabel(char area_name) {
        this.area_name = area_name;
        refreshLocByName();
    }

    public void setPoint(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }

    public void setDoc(String str, Color col, boolean bold, int fontSize) {
        RealPanel pane = new RealPanel(x2 - x1, y2 - y1, panels.size(), area_name);
        panels.add(pane);
        pane.setDocs(str, col, bold, fontSize);
        pane.setTTF();
    }

    public void setNewDoc(String str, Color col) {
        panels.get(0).setDocsX(str, col);
    }

    public void insertData(ArrayList<String> insertDataList) {
        lines += insertDataList.size() / 2 + 1;
        RealPanel pane = new RealPanel(x2 - x1, ori_height, panels.size(), area_name);
        if (insertDataList.get(0).contains("新疆") || insertDataList.get(0).contains("青海")
                || insertDataList.get(0).contains("西藏")) {
            pane.isNMGLeft = true;
        }
        currentHeight += ori_height;
        panels.add(pane);
        pane.insertData(insertDataList);
        switch (area_name) {
            case 'D':
            case 'C':
                if (panels.size() >= 2 || lines >= 5) {
                    isFull = true;
                }
                break;
            case 'A':
                if (panels.size() >= 7 || lines >= 23) {
                    isFull = true;
                }
                break;
            case 'B':
                if(panels.size() >= 4 ||lines>=9){
                    isFull=true;
                }
            default:
                if (panels.size() >= 5 || lines >= 12) {
                    isFull = true;
                }
        }
    }


    public void setCenter() {
        isCenter = true;
    }

    public void refreshLocByName() {
        //setTTF("ttf/normal.ttf");
        int offsetR = 10;
        switch (area_name) {
            case 'A':
                setPoint(MapFrame.province_Button.get("hb").getX() + 140, MapFrame.province_Button.get("hb").getY() - 30,
                        MapFrame.this_with - MapFrame.border, MapFrame.this_height + offsetR);
                //最大的那一片区域
                break;
            case 'B':
                setPoint(MapFrame.province_Button.get("jl").getX() + 100, 0,
                        MapFrame.this_with - MapFrame.border, MapFrame.province_Button.get("hb").getY() - 30 + offsetR);
                //黑龙江右边的区域
                break;
            case 'C':
                setPoint(MapFrame.province_Button.get("gx").getX() + 50, MapFrame.province_Button.get("tw").getY() + 30,
                        MapFrame.province_Button.get("hb").getX() + 140, MapFrame.this_height + offsetR - 20);
                //广东下方区域
                break;
            case 'D':
                setPoint(MapFrame.province_Button.get("yn").getX() - 50, MapFrame.province_Button.get("yn").getY() + 130,
                        MapFrame.province_Button.get("gx").getX() + 40, MapFrame.this_height + offsetR - 20);
                //云南下方的区域
                break;
            case 'E':
                setPoint(0, MapFrame.province_Button.get("sc").getY() + 35,
                        MapFrame.province_Button.get("yn").getX() - 50, MapFrame.this_height + offsetR);
                //西藏下方的区域
                break;
            case 'F':
                setPoint(MapFrame.province_Button.get("yn").getX() - 50, 0,
                        MapFrame.province_Button.get("bj").getX() + 90, MapFrame.province_Button.get("ll").getY() + offsetR + 70);
                //内蒙古上方区域
                break;
            case 'G':
                setPoint(0, 0,
                        MapFrame.province_Button.get("yn").getX() - 95, MapFrame.province_Button.get("xj").getY() + 50);
                setCenter();
                setTTF("ttf/normal.ttf");
                //新疆上方的区域
                break;
            case 'H':
                setPoint(MapFrame.this_with - MapFrame.border - 200, 0,
                        MapFrame.this_with - 200, MapFrame.this_height);
                setCenter();
                setTTF("ttf/master");
                break;
            //最右侧区域
            case 'I':
                setPoint(0, MapFrame.province_Button.get("ll").getY() + offsetR + 50,
                        MapFrame.province_Button.get("yn").getX() - 70, MapFrame.province_Button.get("sc").getY() + 35);
                //新疆右侧备用区域
                break;
            default:
                break;
        }
    }

    public void setTTF(String path) {
        TTFPath = path;
    }

    public boolean isFull() {
        return isFull;
    }

    public Point getLocation() {
        return new Point(x1, y1);
    }

    public int getWidth() {
        return x2 - x1;
    }

    public int getHeight() {
        return y2 - y1;
    }

    public boolean isCenter() {
        return isCenter;
    }

    public String getTTFPath() {
        return TTFPath;
    }
}