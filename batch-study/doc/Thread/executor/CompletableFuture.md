## CompletableFuture 是 Java 8 引入的一个类，属于 java.util.concurrent 包，用于支持异步编程。
###  二、解决了什么问题？
| 问题            | CompletableFuture 的解决方式          |
| ------------- | -------------------------------- |
| Future 阻塞等待结果 | 使用回调函数自动触发，不用阻塞                  |
| 多个异步任务不能组合    | 提供 `thenCombine`、`allOf` 等方法     |
| 没有异常处理机制      | 提供 `exceptionally`, `handle` 等机制 |
| 回调地狱、线程手写多    | 提供线程池自动管理，支持链式操作                 |
