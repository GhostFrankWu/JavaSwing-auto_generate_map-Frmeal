import java.text.Collator;
import java.util.ArrayList;
import java.util.Locale;

/***
 * class contains 1.College in which province
 * 2.name of College
 * 3.students in College
 *
 * data of STU is only upd when make a new mealMap
 */
public class College {
    private String province, name;
    private final ArrayList<Student> students=new ArrayList<>();

    public College(String name, String province) {
        this.name = name;
        this.province = province;
    }

    public void addStudent(Student s){
        students.add(s);
    }

    public ArrayList<Student> getStudents(){
        return students;
    }

    public void clear() {
        students.clear();
    }

    public int compByName(College p) {//use Chinese to judge if p=this (
        Collator c = Collator.getInstance(Locale.CHINA);
        return c.compare(this.getName(), p.getName());
    }

    public String getName() {
        return name;
    }

    public String getProvince() {
        return province;
    }
}
