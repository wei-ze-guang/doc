package alibaba;

import java.util.Objects;

/**
 * 集合类如果使用自己的对象作为key，那么需要重写equals和hashCode
 */
public class Equals {
}

/**
 * 如果使用person作为集合的key，必须重写，String因为重写了，所以我们可以直接使用
 */
class Person{
    private String name;
    private int age;
    @Override
    public boolean equals(Object obj) {
            if(this == obj) return true;  //同一个对象
            if(obj == null || (this.getClass() != obj.getClass())) return false;//类型不对

            Person person = (Person)obj;  //转类型

            return person.age == this.age && Objects.equals(name, person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }
}
