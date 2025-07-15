package base.record;

public class RecordDemo {
    public static void main(String[] args) {
        Student student = new Student(44,"name");

        System.out.println(student.age());

        Student sss = Student.of(44, "sss");
    }
}
