# sentinel的资源定义
## 1、API方式定义资源
这是一个简单的文档demo
```
	Entry entry = null;
	try {
        entry = SphU.entry(RESOURCE_NAME, EntryType.IN);
	    #此处内容为定义的资源
	}catch(BlockException e){
	     #定义资源限制后的处理方法
	}finally{
        if (entry != null) {
            entry.exit();
        }	
	}
```
介绍一下资源变量：RESOURCE_NAME 资源名称用于定义该资源的唯一 **标识**

## 2、注解方式定义资源
`@SentinelResource`注解的方式定义资源，该注解的参数
- `value`: 资源的唯一标识，资源的名称
- `entryType`: entry 类型，可选项(默认为 EntryType.OUT)
- `blockHandler / blockHandlerClass`: `blockHandler`是在所定义资源中出现`BlockException`情况时进行的异常处理逻辑（方法名），`blockHandlerClass`是定义`BlockException`异常处理逻辑，这个处理逻辑位于代码的位置（类名），这里处理逻辑必须是静态方法。
- `fallback / fallbackClass`: `fallback`是定义资源中出现任何异常（包括`BlockException`）时进行的异常处理逻辑（方法名），`fallbackClass`是定义`BlockException`异常处理逻辑，这个处理逻辑位于代码的职位（类名），这里处理逻辑必须是静态方法。由于`fallback`和`blockHandler`功能重叠，当`fallback`和`blockHandler`都进行配置，`BlockException`只会进入`blockHandler`。
- `defaultFallback / fallbackClass / exceptionsToIgnore`:`defaultFallback`是定义资源中出现任何异常（包括`BlockException`）时进行的异常处理逻辑（方法名），`exceptionsToIgnore`用于指定那些异常被排除掉。由于`fallback`和`defaultFallback`的功能一样，当两者一起使用的时候默认`defaultFallback`有效。


