import javax.swing.*;

public class Student extends JButton {
    private String name;
    private College college;
   // public int UID;
    //private static int cnt=0;

    public Student() {
       // cnt++;
        //UID=cnt;
    }

    public String getNameOfStudent() {
        return name;
    }

    public void setCollege(College c) {
        college = c;
        c.addStudent(this);
    }

    public void SetName(String s) {
        name = s;
    }

    public College getCollege() {
        return college;
    }
}
