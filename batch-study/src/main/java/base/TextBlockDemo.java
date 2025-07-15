package base;

import org.junit.Test;

/**
 * 文本块，三个双引号
 */
public class TextBlockDemo {

    @Test
    public void test() {
        String text = """
                select *  from student
                where name like '%a%'
                and Sage = '韦泽光'
                """;

        System.out.println(text);

        var t = """
                select *  from student
                where name like '%a%'
                """;
        System.out.println(t instanceof String);
    }
}
