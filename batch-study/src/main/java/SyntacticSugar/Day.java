package SyntacticSugar;

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

