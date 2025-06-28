## this() 和 super() 为什么不能同时出现  
```java
package base;

/**
 * 讲一下为什么this和super不能在一起
 * 除了Object类之外，任何类在new 的时候都会默认调用父类无参构造器，但是你可以显式指出调用哪一个，
 * 如果你不指出就是调用默认的，而且必须需要调一个
 */
public class ThisAndSuper {

    public static void main(String[] args) {

    }

    static class Parent{
        int a = 1 ;
        Parent(){
            this.a = 100;
        }
        Parent(int a){
            this.a = a;
        }
    }
    static class Child extends Parent{
        int b = 2 ;
        Child(){
            this.a = 100;
        }
        Child(int a){
            this(1,5);  //这里为什么不可以呢，因为这里会调用两次父类构造器，下面的super会调用一次，这个this(1,5)
                                // 会调用下面三号构造器，下面的三号构造器，还会调用一次父类构造器哦，所以父类构造器被执行了两次
            super();
        }
        //这是三号构造器
        Child(int a, int b){
            // 这里被省略了 super() ,只是他没出来
            this();
        }
    }
}

```