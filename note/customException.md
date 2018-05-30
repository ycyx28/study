# 自定义异常
在实际开发中，为了开发效率和美化代码，让开发更多的关注业务代码的编写，减少代码中的if判断，自定义异常处理不失为一种好办法，但是一般的异常处理都会打印异常
链，会损耗部分性能，为了使用异常能有有if else这样的性能，可以重写fillInStackTrace，接下来就简单减少下怎么去重写和自定义一个自己的异常处理类。

## 继承RuntimeException
常见的，继承RuntimeException来自定义自己业务中的异常处理类，如 XxxException,构造方法根据不同业务按需提供。

``` java
public class XxxException extends RuntimeException{

	private static final long serialVersionUID = 1L;
  
  protected String errorCode;
  
  public XxxException(String errorCode){
    super(errorCode);
    this.errorCode = errorCode;
  }
  
   public XxxException(String errorCode,String message){
      super(message);
      this.errorCode = errorCode;
   }
   
  public XxxException(String errorCode,String message,Throwable cause){
    super(message,cause);
    this.errorCode = errorCode;
  }
  
  public XxxException(String errorCode,Throwable cause) {
    super(cause);
    this.errorCode=errorCode;
	}
   
  public String getErrorCode() {
    return errorCode;
	}
  
  /** 
	 * 重写fillInStackTrace
	 * 优点：会有更好的性能
	 * 缺点：没有异常链
	 */
	@Override
	public Throwable fillInStackTrace() {
		return this;
	}
    
}

```


实际项目中使用的时候，直接调用即可，然后方法每个方法提供一个统一的处理异常的方法，可以使用注解AOP来实现，这样业务层的代码就显得很干净。

``` java
public XxxResponse xxxMethod(){
  try{
    throw new XxxException("触发Xxx异常");
    }catch(XxxException e){
     //TODO ,异常处理
    }catch(Exception e1){
      //TODO ,异常处理
    }
}

```
这里的try catch可以用aoo进行封装,优化完的代码就只有如下结构了

``` java
@XxxHandler
public XxxResponse xxxMethod(){
   throw new XxxException("触发Xxx异常");
}
```
AOP处理这里就不做详细说明了。
