## jitwatch使用流程 可以用来看即时编译的
- 添加参数 -XX:+UnlockDiagnosticVMOptions -XX:+LogCompilation -XX:LogFile=jit.log
```xml
<loop idx='1428' inner_loop='1' >
</loop>
</loop_tree>
<loop_tree>
<loop idx='1428' inner_loop='1' >
</loop>
</loop_tree>
<loop_tree>
<loop idx='1428' inner_loop='1' >
</loop>
</loop_tree>
<loop_tree>
<loop idx='1428' inner_loop='1' >
</loop>
</loop_tree>
<loop_tree>
<loop idx='1428' inner_loop='1' >
</loop>
</loop_tree>
<regalloc attempts='1' success='1'/>
<code_cache total_blobs='513' nmethods='166' adapters='256' free_code_cache='248579200'/>
<task_done success='1' nmsize='2424' count='7236' backedge_count='658' inlined_bytes='82' stamp='0.077'/>
</task>
</compilation_log>
<hotspot_log_done stamp='0.118'/>
</hotspot_log>
```