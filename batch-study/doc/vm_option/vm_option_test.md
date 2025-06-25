## 测试OOM
- 添加参数测试堆出现OOM   
  - Xms10m：初始堆大小 10MB
  - Xmx10m：最大堆大小 10MB
```diff
-Xms10m -Xmx10m
```  
```java
Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
```  
---  
- 测试虚拟栈的调用超过栈允许的最大深度
  - -Xss180k 默认每线程栈大小是 1M（JDK 8），改小到 180k 可以更快复现 OOM。
```java
Exception in thread "main" java.lang.StackOverflowError  //这个异常的方法调用链太深
```  
---  
- 测试栈溢出 
- -Xss180k  
  - 每个站的内存大小为180k  
```java
    static class ThreadOom extends Thread{

        static List<String> list = new ArrayList<String>();

        private void digui(){

            while(true){
                list.add(list.size()+"");
            }
        }
        @Override
        public void run() {
            digui();
        }
    }
    //出现下面异常
Exception: java.lang.OutOfMemoryError thrown from the UncaughtExceptionHandler in thread "Thread-0"
```  
---  


