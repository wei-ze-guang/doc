package sort;

import java.util.*;

public class comparable {

    public static void main(String[] args) {
        List<Person> list1 = new ArrayList<Person>();

        List<String> listStr = new LinkedList<String>();

        for(int i = 0; i <30; i++){
            list1.add(new Person(i,String.valueOf(i)));
        }
        /**
         * Comparable 接口一般用来我们自己写的类，比如下面的Person，这是我们主写的
         * 上面的List<Person> 就是我们列表存的就是这个Person，Collections.sort()如果不传入其他排序方法就会使用这个
         */
        Collections.sort(list1);
        System.out.println(list1);
        System.out.println("上面第一次排序，因为Person实现了Comparator接口，使用默认排序");
        /**
         * 加入现在我需要临时换替换他实现的接口的话可以这样子做
         */
        Collections.sort(list1, new Comparator<Person>() {
            public int compare(Person o1, Person o2) {
                return o1.age - o2.age;
            }
        });
        System.out.println(list1);
        System.out.println("使用临时的接口");

        /**
         * 像这个Person是我们自己写的，但是就像字符串呢，他也实现了默认的排序，但是他怎么排序的我们不知道
         * 我们临时修改他的就排序接口
         */

        for (int i = 0; i < list1.size(); i++) {
            listStr.add(String.valueOf(i));
        }
        Collections.sort(listStr,new Comparator<String>() {
            public int compare(String o1, String o2) {
                return o1.length() - o2.length();
            }
        });
        System.out.println(listStr);

    }

    static class Person implements Comparable<Person> {

        Integer age;

        String name;

        @Override
        public String toString() {
            return "Person [age=" + age + ", name=" + name + "]";
        }

        public Person(Integer age, String name) {
            this.age = age;
            this.name = name;
        }
        @Override
        public int compareTo(Person o) {
            return -(this.age - o.age);
        }
    }
}


