package SyntacticSugar;


import java.util.Arrays;

/**
 * 这个包下面练习语法糖
 */
public class Main {
    public static void main(String[] args) {

        Enum day = SyntacticSugar.Day.valueOf("SUN");
        System.out.println(day);

        Day[] values = Day.values();
        System.out.println(Arrays.toString(values));

        System.out.println(day.ordinal());
        System.out.println(day.name());

        Enum t = Day.MON;

    }

    /**
     * EnumSet,EnumMap 明天要学
     */
}
