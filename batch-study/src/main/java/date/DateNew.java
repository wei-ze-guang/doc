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
