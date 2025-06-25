### 自动装拆箱
- 装箱（boxing）：基本类型 → 包装类对象，例如 int → Integer
- 拆箱（unboxing）：包装类对象 → 基本类型，例如 Integer → int  
```java
Integer a = 100;  // 自动装箱：等价于 Integer.valueOf(100)
int b = a;        // 自动拆箱：等价于 a.intValue()

```  
#### == 和 equals
**大家平时用equals的时候或者==的时候，要记住就是这个就是和java定义一样，但是存在自动装拆箱的时候就我们就要注意。  
不要学到最后蒙蔽了，就是equals一般比较就是值，==比较的时候就是地址引用，如果发生了和这两种情况不一致情况  
特别是equals，大家首先第一步就是取查看这个方法是不是被他重写了**
- 基本类型和包装类中会出现三中比较的情况  
  - 这三种情况分别是,基本类型和基本类型，包装型和包装型，基本类型和包装型
    - equals只能是包装型和包装型之间使用,equals比较的是值  
    - ==可以在上面的三种情况之间使用 == 号的使用需要注意一下区别，他和平时的比较地址有点差别  
#### 学习自动装拆箱和==和equals的时候首先第一步要确认你这个变量到底是基本类型还是对象！！!
**再次强调,一定要明确知道你定义的是基本类型的还是对象**  看下面代码
- 基本类型  
```java
        int a = 444;
        int at = new Integer(444);  //这里会自动拆箱，大家这里先不要管如何自动拆箱先
/**
 * 你这里得到的一定是一个基本类型!，基本类型!，基本类型!，说三遍
 */

```  
---  

- 对象类型  
  - 下面一个Integer源码的一个方法
```java
    @IntrinsicCandidate
    public static Integer valueOf(int i) {
        if (i >= IntegerCache.low && i <= IntegerCache.high)
            return IntegerCache.cache[i + (-IntegerCache.low)];
        return new Integer(i);
    } // 这个是Integer源码的一个方法，他在一种情况下会自动触发，我们不需要手动触发，这里你需要记住的是他返回的是一个对象！！返回一个对象！
```
  - 下面是三个对象实例，记住哈，只要是对象实例，无论说是今天自动装箱还是我们平时自己写的类，只要是一个对象实例，他在java
内存中都会存在一份实实在在的对象，他是占用内存资源的！也就是说每一个对象都有一个不一样的地址！不可能是一个地址两个或者多个对象！
```java
        Integer b = 5555;  //他会自动装箱，这里相当于new了一下，在这里就是触发了上面的哪个源码方法，他返回的是一个对象！！！
        Integer bt = new Integer(555);
        Integer btt = new Integer(555);
        /*
         * 这里得到的结果是对象类型！对象类型！对象类型！说三遍，b，bt，btt都是不一样的，他们分别指向了自己引用的对象，
         */
```  
#### 下面我们先讨论equals和==这两种方法，再往后研究自动装拆箱  
- equals方法  
  - equals方法永远比较的是值！！！！ 是Object的一个方法，所有对象都有这个方法，java给这个方法的定义就是比较的值  
  - 你不要给我乱扯我写了一个类，我重写了equals方法，我把我的类的equals方法改为了猪和狗对比，结果返回一个鸡。纯扯淡！
  - equals 方法只能对象使用！  
```java
        int a = 444;
        int at = new Integer(444);
        a.equals;  // 他是基本类型没有这个方法！
        at.equals;  // 他是基本类型没有这个方法！
```
```java
        Integer b = 5555;
        Integer bt = new Integer(555);
        b.equals(bt);  //他是对象，他是对象，他能使用这个方法
        bt.equals(a);  //他是对象，他是对象，他能使用这个方法，但是这里注意看他的参数 equals的参数是没有要求的，要看类怎么重写他的
```
  - 在使用equals的时候这样子理解就可以了，不需要看他是什么字符串啊还是什么包装类啊，什么自己写的类啊,因为equals比较的是值  
    - 这个值，如何理解呢，我们来看  
```java
        String s="这是值";
        String ss = new String("这是值");
        String sss = new String(s);
        System.out.println(s.equals(ss));  //true
        System.out.println(s.equals(sss));  //true
        System.out.println(ss.equals(ss));  // true

        Integer i = new Integer(888);
        Integer ii = 888;  //注意这里返回的是对象
        Integer iii = new Integer(887);
        System.out.println(i.equals(ii));  //true
        System.out.println(i.equals(iii));  //false
        System.out.println(ii.equals(iii));  //false
```  
- ==的使用
  - == 在java里面是写死了的！ 他没有说让你重写，没有让你重写！！！！你也重写不了
  -  永远记住 == 永远比较的是地址，是地址，是地址，除了基本类型以外，除了基本类型以外！！ 
  - 这里再谈一个话题,java里面我们平时学习的时候从来没提到过基本类型的地址吧！基本类型实际在内存肯定有地址  
但是java他说过基本类型的地址是什么吗，他从来没提到过！！！从来没提到过！！也就说如果 在比较的时候 出现过任何的基本类型
，你让我怎么比呢，我是基本类型，你跟我比地址不是扯淡吗，我都在java里面就没有基本类型地址这个概念，所以啊，记住  
**如果在比较中遇到了基本类型的比较，我跟你比较的就是值，我没有地址这个概念啊，所以我只能跟你比较值啊**  
```java
    @Test
    public void denggdenghao(){
        int a = 555;   //基本变量
        int b = 555;
        int c = new Integer(555);
        int d = new Integer(555);
        String s = "555";
        String ss = new String("555");

        /**
         * 下面三个比较的时候都涉及到了 基本类型a ，所以比较的时候只能比较值，因为我是基本类型，我只有值啊！
         */
        System.out.println(a == b);  //true  两个都是基本类型
        System.out.println(a == c);  //true   虽然你c是对象类型，但是我a是基本类型啊，我只有值！
        System.out.println(a == d);  //true   虽然你c是对象类型，但是a是基本类型啊，我只有值，你得拿值跟我比
        
    }
```  
- 谈完了 == 中涉及 基本类型得时候，是不是还有一种情况呢，就是 == 比较得是两个都不是基本类型
     - 两个都不是基本类型 比较得是地址！！ 比较的是地址，比较得是地址！
     - 下面三个都是对象引用，只要是对象，在内存都是一个他自己得地址
```java
    @Test
    public void dengdengAdderess(){

        Integer a = 555;  //这个a是一个对象类型引用，不是基本类型，不是基本类型
        Integer b = 554;  //这个b是一个对象类型引用，不是基本类型，不是基本类型
        Integer c = new Integer(555);  //这个c是一个对象类型引用，不是基本类型，不是基本类型

        System.out.println(a == b);  //false
        System.out.println(a == c);  //false
        System.out.println(c == b);  //false

    }
```
  - java里面没有提供直接打印对象地址得接口，但是我们可以打印他得逻辑地址，虽然逻辑和实际地址有差别，但是呢  
可以肯定的是实际地址不同的话逻辑地址肯定也不一样，我们把上面得三个地址打印出来看看  
```java
    @Test
    public void dengdengAdderess(){

        Integer a = 555;  //这个a是一个对象类型引用，不是基本类型，不是基本类型
        Integer b = 554;  //这个b是一个对象类型引用，不是基本类型，不是基本类型
        Integer c = new Integer(555);  //这个c是一个对象类型引用，不是基本类型，不是基本类型

        System.out.println(a == b);  //false
        System.out.println(a == c);  //false
        System.out.println(c == b);  //false
         System.out.println(System.identityHashCode(a));  //1289479439
        System.out.println(System.identityHashCode(b));  //6738746
        System.out.println(System.identityHashCode(c));  //2096171631
        // 你会发现上面三个地址不一样 == 就是使用上面得地址比较得

    }
``` 
  - 分析Integer a = 555;  和 Integer b = 554; 这两个和Integer c = new Integer(555);差别是  
    -  Integer c = new Integer(555) 是我们手动 new 得，只要是手动new得对象，所有对象得地址都不一样
    - Integer a = 555; 和 Integer b = 554;  他会触发下面Integer的方法，下面是源码  
```java
    @IntrinsicCandidate
    public static Integer valueOf(int i) {
        // 检查是否是 大于等于-128 而且小于等于127
        if (i >= IntegerCache.low && i <= IntegerCache.high)
            return IntegerCache.cache[i + (-IntegerCache.low)];
        // 如果不是的话new 一个返回
        return new Integer(i);
    } // 这个是Integer源码的一个方法，他在一种情况下会自动触发，我们不需要手动触发，这里你需要记住的是他返回的是一个对象！！返回一个对象！
```
    - 当你Integer a = 555; 实际就是 Integer a = Integer.valueOf(555); 

#### 自动拆箱和装修  
- 拆箱，拆箱很简单吗，就是把一个对象拆箱为一个基本类型，结果就是一个基本类型，实际使用的时候对我们不会产生误区
  - int c = Integer(222); 得到的就是一个int c = 222,对我们编程没误区
- 装箱，装箱就需要特别处理了，Integer a = 100;  // 自动装箱：最后a是一个对象，他会调用上面的Integer的valueOf静态方法  
  - 就是说你使用 Integer a = 500 的时候java自动帮你new 一个对象，说人话就是，你不想写我编译器自己帮你写，就是Integer = new Integer(500);
  - 按照我们今天说的每一个对象在内存都有地址，一点不错，下面的也没错，但是看结果哈，错误肯定没错  
```java
    @Test
    public void t(){
        /**
         * 自己new的对象 ，只要使用new 他肯定是唯一的，肯定是唯一的
         */

        Integer a = 100;
        Integer b = 100;
        System.out.println(a == b);  //true ，这个等下讲话
        Integer big = 500;
        Integer bi = 500;
        System.out.println(big == bi);  //false //这个等下讲

        Integer c = new Integer(100);
        Integer cc = new Integer(100);

        System.out.println(a == c); //false，这两个一定不相等，先不说他如何装箱，这个c是我自己new的，无论你这个c从哪里来的，但是肯定不是我自己new这个

        System.out.println(c == cc); //false  这两个一定不相等，因为都是自己new的

        Integer d = c;
        System.out.println(d == c);  //true  这两个一定相等，就是地址引用！！！  记住这里，等一下讲装箱的时候重点讲这个地址引用

    }
```  
- 重点讲下面的这个
```java

        Integer a = 100;
        Integer b = 100;
        System.out.println(a == b);  //true ，这个等下讲话
        Integer big = 500;
        Integer bi = 500;
        System.out.println(big == bi);  //false //这个等下讲
```    
```java

@Test
public void test1(){
  //  先讲一个事实，是事实，java程序一开始做了一件事，对于Integer来说他把数字 -128 到正数127 都各new对象在内存
  Integer a = new Integer(-128);
  Integer b = new Integer(-127);
  //中间还有很多
  Integer f = new Integer(100);
  Integer c = new Integer(126);
  Integer d = new Integer(127);
  //  解释上面的 就是java自己存在内存里面的,这里我们需要假装看不见，但是他是实际存在的

  Integer aa = new Integer(-128);//这个是我们自己new的，只是-128和上面相同，除了这个几乎没有任何关系了

  Integer aaa = 100;  //如果我们这样子，假设这个数字是-128 到127 结果是拿到上面的，java给你返回的就是 Integer a = new Integer(-128);
  Integer aaaa = 100 ; //看到还是还是在-128到127中间，给你返回的还是 Integer a = new Integer(-128); 这个

  System.out.println(aaa == aaaa);  //true

  //当你的数组不在-128 到127 的时候
  //  他会new一个给你
  Integer t = 500;
  Integer tt = 500;
  System.out.println(t == tt); //false

}
```  
## 在学习这个 时候一定要自己查看Integer类里面的静态方法#valueOf(int i)  
```java
Integer a = null;
int b = a; // ❌ 自动拆箱时会抛出 NullPointerException
```  
| 情况                 | 判断方式       | 结果                           |
| ------------------ | ---------- | ---------------------------- |
| 基本类型 vs 基本类型       | `==`       | 比值 ✅                         |
| 包装类 vs 包装类         | `==`       | -128\~127 是 true，其他是 false ❗ |
| 包装类 vs 包装类         | `equals()` | 比值 ✅                         |
| 包装类 vs 基本类型        | `==`       | 自动拆箱后比值 ✅                    |
| 包装类是 null 时参与运算或比较 | 自动拆箱       | 抛异常 ❌                        |

### 总结equals和 === 
- 我们java实际比较的时候无论使用==或者equals，只有一种情况出现双方比较的是地址
  - 这种情况就是比较的双方都是对象 而且使用的都是 ==   
- 其他情况比较的都是值，都是值！ 这些情况如下面  
  - 使用 == 。除了上面说的双方是对象在之外，就是比较的是值 
  - 使用equals， 无论双方是基本还是还是实例类型，比较的就是值   
### 说人话就是，我怎么知道比较的是值还是地址呢，如果你使用了 == 而且双方都是对象，那么我们比较地址，其他情况一律比较值




