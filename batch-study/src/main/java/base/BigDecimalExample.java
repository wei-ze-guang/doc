package base;

import java.math.BigDecimal;

public class BigDecimalExample {
    public static void main(String[] args) {
        // 正确的浮点数表示方式
        BigDecimal number1 = new BigDecimal("0.1");
        BigDecimal number2 = new BigDecimal("0.2");
        BigDecimal result = number1.add(number2);  // 结果是 0.3

        System.out.println("Result: " + result); // 输出：Result: 0.3

        // 错误的浮点数计算（会出现精度问题）
        double num1 = 0.1;
        double num2 = 0.2;
        double wrongResult = num1 + num2;  // 结果是 0.30000000000000004

        System.out.println("Wrong result: " + wrongResult); // 输出：Wrong result: 0.30000000000000004
    }
}
