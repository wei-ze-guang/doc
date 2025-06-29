## 时间 JDK8后的新版  
| 项目     | 建议                                                |
| ------ | ------------------------------------------------- |
| 格式化    | 用 `DateTimeFormatter` 替代 `SimpleDateFormat`（线程安全） |
| 时间戳    | 用 `Instant`，不要再用 `System.currentTimeMillis()`     |
| 时区     | 使用 `ZonedDateTime` 或 `OffsetDateTime`             |
| 自定义工具类 | 可以封装为 `TimeUtils` 统一处理格式/转换/差值计算等                 |
```java
package date;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateNew {
    public static void main(String[] args) {
        // 当前时间
        LocalDateTime now = LocalDateTime.now();
        System.out.println(now);

// 当前时间戳
        Instant timestamp = Instant.now();
        System.out.println(timestamp);

// 当前日期
        LocalDate today = LocalDate.now();
        System.out.println(today);

// 字符串转时间
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime time = LocalDateTime.parse("2025-06-29 22:30:00", fmt);
        System.out.println(time);

// 时间加减
        LocalDate tomorrow = today.plusDays(1);
        System.out.println(tomorrow);

// 格式化时间
        String str = time.format(fmt);
        System.out.println(str);

    }
}

```