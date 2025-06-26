## 语法糖相关
- 枚举
```java
public enum Day {
    /**
     * 注意这里有一个顺序，从0开始，按照写的时候顺序排序
     */
    MON("工作日"),
    SUN("休息hi");

    private final String desc;

    /**
     * 这个是私有构造函数
     * @param desc
     */
    private Day(String desc) {
        this.desc = desc;
    }
}

```  
编译后  
```java
public final class Day extends java.lang.Enum<Day> {
public static final Day MON = new Day("MON", 0, "工作日");
public static final Day SUN = new Day("SUN", 1, "休息");

    private static final Day[] VALUES = { MON, SUN };

    private final String desc;

    private Day(String name, int ordinal, String desc) {
        super(name, ordinal);
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public static Day[] values() {
        return VALUES.clone();
    }

    public static Day valueOf(String name) {
        for (Day d : VALUES) {
            if (d.name().equals(name)) {
                return d;
            }
        }
        throw new IllegalArgumentException("No enum constant " + name);
    }
}


```  
- 他会多出来一些方法  
### 补充一点知识，如果要使用Map或者Set存储写好的枚举，看下面  
| 集合类型               | 存储结构         | 查询效率    | 内存占用 |
| ------------------ | ------------ | ------- | ---- |
| `HashMap<Enum, V>` | 哈希表          | 较快（有冲突） | 高    |
| `EnumMap<Enum, V>` | 数组 + ordinal | 极快      | 低    |
| `HashSet<Enum>`    | 哈希表          | 较快      | 高    |
| `EnumSet<Enum>`    | 位图           | 极快      | 极低   |
