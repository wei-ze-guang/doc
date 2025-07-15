package base.record;

/**
 * JDK 会自动生成：
 *
 * 所有字段的 final 属性
 * 构造器
 * get 方法（方法名和字段名相同）
 * equals()、hashCode()、toString()
 * ✅ 内置不可变性（immutable），避免对象被意外修改
 * ✅ 自动重写 equals/hashCode，天然适合做 Map key、去重等操作
 * Record 是final类，不能被继承，也不能有final字段
 * @param age
 * @param name
 * | 场景              | 示例                                                     |
 * | --------------- | ------------------------------------------------------ |
 * | 定义响应对象 DTO      | `record UserDTO(String name, int age)`                 |
 * | VO / 展示模型       | `record StudentVO(String name, String dep)`            |
 * | 参数传输对象          | `record LoginRequest(String u, String p)`              |
 * | 配合 Java Pattern | `if (obj instanceof Student(String name, int age)) {}` |
 * | 单元测试 / 枚举简化     | 生成测试数据等
 * ❗️注意事项
 * record 是 隐式 final 的，不能继
 * 成员变量是 private final，只读
 * 可以定义构造器、静态方法、嵌套类等
 * 支持注解
 * 不能使用 @Entity（不适合做 JPA 持久化类）|
 * 没有set 方法 ，有get方法，但是不是叫做get
 */
public record Student(int age, String name) {

    // 1️⃣ 紧凑构造器（隐式参数 & 自动赋值）
    // 他会自动赋值，覆盖原来的构造器
    public Student{
        if(age < 0) throw new IllegalArgumentException("Age cannot be negative");
    }

    // 2️⃣ 显式构造器（你必须自己赋值）会覆盖默认构造器，这里会冲突先注释掉
//    public Student(int age,String name){
//        this.age = age;
//        this.name = name;
//        if(age < 0) throw new IllegalArgumentException("Age cannot be negative");
//    }
    //可以添加静态方法
    public static Student of(int age, String name) {
        return new Student(age, name);
    }

    //实例方法
    public boolean isDelete(){
        return age == 0;
    }

}

/**
 * | 类型               | 是否推荐用 `record` | 原因说明                        |
 * | ---------------- | -------------- | --------------------------- |
 * | ✅ DTO            | ✔️ 非常适合        | 传输数据只读，自动生成方法               |
 * | ✅ VO             | ✔️ 很适合         | 展示层模型，不修改数据                 |
 * | ❌ Entity         | ❌ 不推荐          | JPA/MyBatis 需要 set 方法和无参构造器 |
 * | ✅ API 请求体        | ✔️ 非常适合        | Spring 接收 JSON 请求，字段不需要变    |
 * | ❌ Form 表单对象      | ⚠️ 视情况         | 如果需要动态校验、修改值，不适合            |
 * | ✅ 响应模型 Result<T> | ✔️ 非常适合        | 结构固定、不可变，天然适配 record        |
 *
 * ✅ 3. 为什么 Entity 不适合用 record？
 * ❌ 不兼容 ORM（比如 JPA、MyBatis）：
 * ORM 要求：无参构造器（record 没有）
 * ORM 要求：字段可变（record 是 final 的）
 * 通常需要 @Entity, @Id, @Column 等注解，这些需要普通类字段
 *
 * ✅ 4. 为什么 DTO/VO 非常适合用 record？
 * DTO 本质是数据载体，用于系统内部或系统间的数据传输
 * 它们不该有行为，也不该被修改 ✅
 * record 自动生成：
 * 所有 getter（字段名即方法名）
 * equals, hashCode, toString
 * 全参构造器
 * 无需手动写 getter/setter，干净利落！
 *
 * ✅ 6. MapStruct：支持 record
 * ✅ 7. OpenAPI / Swagger：支持，但需注意
 * SpringDoc OpenAPI 3 ✅ 支持 record
 * 字段名必须准确，@Schema 注解使用无问题
 *
 * | 框架 / 工具           | 支持 record | 说明说明                                  |
 * | ----------------- | --------- | ------------------------------------- |
 * | Spring MVC        | ✅ 完全支持    | @RequestBody / @ResponseBody / 配置等无障碍 |
 * | Jackson           | ✅ 完全支持    | 自动反序列化到构造器                            |
 * | Gson / FastJSON   | ✅ 支持良好    | 字段名匹配即可                               |
 * | Lombok            | ✅ 不再需要    | record 自带 getter/toString/equals      |
 * | MyBatis 查询        | ✅ 支持只读查询  | 可做查询结果返回类型                            |
 * | MyBatis 插入更新      | ❌ 不支持     | 无 setter、字段 final                     |
 * | JPA / Hibernate   | ❌ 不支持     | 无无参构造器，字段 final，无法反射注入                |
 * | Swagger / OpenAPI | ✅ 支持良好    | 支持注解 + 显示字段                           |
 * | MapStruct         | ✅ 1.5+支持  | 支持类 ↔ record 相互转换                     |
 */
