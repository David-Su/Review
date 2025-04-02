# Kotlin协程原理解析

## 协程介绍

* 协程可以用同步的代码编写方式编写异步的代码。  

  通过网络请求获取数据并渲染到TextView  

    **协程方式** 
    ```kotlin
    val token = getUserToken()
    val nickName = getUserNickName(token)
    textView.text = nickName
    ```
    **普通方式**
    ```kotlin
    getUserToken() { token ->
        getUserNickName(token) { nickName ->
            textView.text = nickName
        }
    }
    ```
* 协程是编程语言的概念，线程是操作系统层面的概念。
* 协程的实现包括了代码转换，线程管理，其底层执行方式依然是Java的线程。有点像对线程池再做一层封装。
* 结构化并发  
协程只能运行在指定的协程域，一个协程域可以运行多个协程。当某个协程域的所有的协程运行完成，这个协程域的状态才为完成。

## 调度器

* DEFAULT
* IO
* MAIN

## 挂起
在协程中，挂起的意思是切换到其他调度器执行,执行完成再回到原来的调度器执行（注意是原来的调度器，但不一定是原来的线程了。一个调度器可以有多个线程）。我们可以用suspend关键字修饰一个函数，表示该函数可能会被挂起。真正实现挂起的代码（比如withContext函数）必须在suspend函数执行，但是suspend方法不一定要执行挂起代码。

```kotlin
//能挂起的函数
private suspend fun withContextSuspend(): String {
    return withContext(Dispatchers.Default) {
        "withContextSuspend"
    }
}
//不会挂起的函数
private suspend fun withContextSuspend(): String {
    return ""
}
```
我们编写的逻辑代码只有在调用Kotlin提供的suspend函数后才能实现真正的挂起。比如如withContext函数，Kotlin编译器会在编译时对这些特有的函数

## 协程作用域CoroutineScope
用于运行一个新协程的领域类，其本身包含了协程运行的一个全局上下文。
```kotlin
public interface CoroutineScope {
    public val coroutineContext: CoroutineContext
}
```
可通过CoroutineScope的launch，async等方法开启协程。由于launch需要传入一个扩展函数类型的lambda表达式，在这个lambda内可以直接调用CoroutineScope的方法，所以看起来就像协程运行在一个领域内。这个函数类型是被suspend修饰的函数，所以内部是可以调用其他suspend函数。
```kotlin
GlobalScope.launch {
    // TODO: do something 
}

public fun CoroutineScope.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job {
    ...
}
```
## Contiuation
### SuspendLambda
### CancellableContinuationImpl

## CoroutineContext协程运行上下文
### CoroutineContext相加
```java
    public operator fun plus(context: CoroutineContext): CoroutineContext =
        //如果右边的Context是空Context，直接返回左边的Context
        // fast path -- avoid lambda creation
        if (context === EmptyCoroutineContext) this else
            context.fold(this) { acc, element ->
                //左边的Context移除掉右边Context的Key对应的元素
                val removed = acc.minusKey(element.key)
                //如果左边的Context只有右边的Context的Key对应的内容，说明右边Contextx包含左边Context的全部内容，直接返回右边的Context
                if (removed === EmptyCoroutineContext) element 
                //左边的Context有其他内容。来到这里的话，如果左右Context都有拦截器，则左边Context的拦截器已经被移除了。以下逻辑会生成一个复合Context，并保证拦截器一定在右边（此拦截器已右边Context的拦截器为优先）
                 // make sure interceptor is always last in the context (and thus is fast to get when present)
                else {
                    val interceptor = removed[ContinuationInterceptor]
                    //左边Context没有拦截器，此时左右Context组合成一个复合Context
                    if (interceptor == null) CombinedContext(removed, element)
                    //左边Context有拦截器，生成一个复合Context，将拦截器放到右边，其余内容复合起来放到左边
                     else {
                        val left = removed.minusKey(ContinuationInterceptor)
                        if (left === EmptyCoroutineContext) CombinedContext(element, interceptor) 
                        else
                            CombinedContext(CombinedContext(left, element), interceptor)
                    }
                }
            }
```
### CoroutineContext
#### Job
#### CoroutineDispatcher
#### CoroutineName,CoroutineExceptionHandler...

## 测试案例

## cps转换
### suspend CoroutineScope.() -> T
这种类型对应开启协程所传入的一个函数类型的对象block
```kotlin
public fun CoroutineScope.launch(
    ...
    block: suspend CoroutineScope.() -> Unit
): Job {
    ...
}
```
#### 转换步骤
* block对象会转换成一个继承SuspendLambda的内部类对象
* 该内部类对象生成状态机逻辑
#### 示例
源码
```kotlin
class CoroutineDemo {

    init {
        GlobalScope.launch {
            val result1 = getResult1()
            val result2 = getResult2()
            val result3 = getResult3()
            val total = result1 + result2 + result3
        }
    }

    suspend fun getResult1(): String {
        delay(1000L)
        return "getResult1"
    }

    suspend fun getResult2(): String {
        return "getResult2"
    }

    suspend fun getResult3() = suspendCancellableCoroutine<String> { cont ->
        cont.resume("getResult3")
    }
}
```
cps转换后的代码(经过简化)
```java
class CoroutineDemo$1 extends SuspendLambda {
    Object L$0;
    Object L$1;
    int label;
    final CoroutineDemo this$0;

    CoroutineDemo$1(final CoroutineDemo this$0, final Continuation<? super CoroutineDemo$1> continuation) {
        super(2, (Continuation) continuation);
        this.this$0 = this$0;
    }

    public final Object invokeSuspend(Object o) {
        //函数挂起的标记，此标记为协程内部使用，表示函数真正被挂起
        final Object coroutine_SUSPENDED = IntrinsicsKt.getCOROUTINE_SUSPENDED();

        switch (this.label) {
            case 0: {
                this.label = 1;
                Object result1 = this$0.getResult1(this);
                if (result1 == coroutine_SUSPENDED) {
                    return coroutine_SUSPENDED;
                } else {
                    //执行 case 1 的逻辑
                    invokeSuspend(result1);
                }
                break;
            }
            case 1: {
                String result1 = (String) o;
                this.L$0 = result1;
                this.label = 2;
                Object result2 = this$0.getResult2(this);
                if (result2 == coroutine_SUSPENDED) {
                    return coroutine_SUSPENDED;
                } else {
                    //执行 case 2 的逻辑
                    invokeSuspend(result2);
                }
                break;
            }
            case 2: {
                final String result2 = (String) o;
                this.L$1 = result2;
                this.label = 3;
                Object result3 = this$0.getResult3(this);
                if (result3 == coroutine_SUSPENDED) {
                    return coroutine_SUSPENDED;
                } else {
                    //执行 case 3 的逻辑
                    invokeSuspend(result3);
                }
                break;
            }
            case 3: {
                String result1 = (String) this.L$0;
                String result2 = (String) this.L$1;
                String result3 = (String) o;
                String total = result1 + result2 + result3;
                break;
            }

        }
        return Unit.INSTANCE;
    }
}
```


### suspend方法
#### 转换步骤
* 转换成一个普通的java方法
* 增加一个Continuatio参数
* 返回值改为Object
* 当该方法本身有调用其他的suspend方法时
  * 生成一个该方法对应的内部类
  * 方法逻辑改为状态机逻辑

转换的思路跟suspend CoroutineScope.() -> T转换的思路是相似的，不同的是suspend CoroutineScope.() -> T的状态机逻辑在invokeSuspend中，而suspend方法的状态机逻辑在对应的java方法的方法体中。
#### 示例
源码
```kotlin
class CoroutineDemo {

    suspend fun getResult(): Int {
        val result = getResultA() + getResultB()
        return result
    }

    suspend fun getResultA(): Int {
        return 1
    }

    suspend fun getResultB(): Int {
        return 2
    }
}
```
cps转换后的代码(经过简化)
```java
public class CoroutineDemo {

    public final Object getResult(final Continuation continuation) {
        CoroutineDemo$getResult$1 getResultContinuation;

        //如果不是CoroutineDemo$getResult$1，则continuation为调用getResult的一个外部挂起点
        //新建一个CoroutineDemo$getResult$1并持有这个continuation
        //当这个方法对应的状态机执行完成了，通过continuation恢复外部挂起点
        if (!(continuation instanceof CoroutineDemo$getResult$1)) {
            getResultContinuation = new CoroutineDemo$getResult$1(this, continuation);
        }
        //如果continuation就是CoroutineDemo$getResult$1，则表示继续执行getResult的状态机逻辑
        else {
            getResultContinuation = (CoroutineDemo$getResult$1) continuation;
        }

        //函数挂起的标记，此标记为协程内部使用，表示函数真正被挂起
        final Object coroutine_SUSPENDED = IntrinsicsKt.getCOROUTINE_SUSPENDED();

        switch (getResultContinuation.label) {
            case 0: {
                getResultContinuation.L$0 = this;
                getResultContinuation.label = 1;
                final Object resultA = this.getResultA(getResultContinuation);
                if (resultA == coroutine_SUSPENDED) {
                    return coroutine_SUSPENDED;
                } else {
                    //执行 case 1 的逻辑
                    getResult(getResultContinuation);
                }
                break;
            }
            case 1: {
                final CoroutineDemo coroutineDemo = (CoroutineDemo) getResultContinuation.L$0;
                final int resultA = (int) getResultContinuation.result;
                getResultContinuation.I$0 = resultA;
                getResultContinuation.label = 2;
                final Object resultB = coroutineDemo.getResultB(getResultContinuation);
                if (resultB == coroutine_SUSPENDED) {
                    return coroutine_SUSPENDED;
                } else {
                    //执行 case 2 的逻辑
                    getResult(getResultContinuation);
                }
                break;
            }
            case 2: {
                final int i$0 = getResultContinuation.I$0;
                return i$0 + (int) getResultContinuation.result;
            }
        }
        return null;
    }

    public final Object getResultA(final Continuation<? super Integer> continuation) {
        return 1;
    }

    public final Object getResultB(final Continuation<? super Integer> continuation) {
        return 2;
    }
}
```


## 协程的运行过程
以最简单的开启协程的方式举例
```kotlin
GlobalScope.launch {
    //todo
}
```
### 开启协程
#### 开启协程用的是CoroutineScope的launch方法
```kotlin
public fun CoroutineScope.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job {
    // 1
    val newContext = newCoroutineContext(context)
    // 2
    val coroutine = if (start.isLazy)
        LazyStandaloneCoroutine(newContext, block) else
        StandaloneCoroutine(newContext, active = true)
    // 3
    coroutine.start(start, coroutine, block)
    return coroutine
}
```
##### 1：newCoroutineContext(context)将传入的Context与当前协程的Context合并
```kotlin
public actual fun CoroutineScope.newCoroutineContext(context: CoroutineContext): CoroutineContext {
    // 1
    val combined = foldCopies(coroutineContext, context, true)
    // 2
    val debug = if (DEBUG) combined + CoroutineId(COROUTINE_ID.incrementAndGet()) else combined
    // 3
    return if (combined !== Dispatchers.Default && combined[ContinuationInterceptor] == null)
        debug + Dispatchers.Default else debug
}
```
1：一般情况下，就是将传入的Context与当前协程的Context相加  
2：非DEBUG模式，直接使用combined   
3：确保了返回的Context一定有拦截器（一般的调度器都是拦截器，如：Dispatchers.Default），如果没有拦截器就给combined加一个Dispatchers.Default
##### 2：默认情况下，创建一个新的StandaloneCoroutine协程实例
该实例会关联父协程的作用域（此处没有父协程，所以忽略这一层），并且提供后续步骤所需要的上下文。
```kotlin
private open class StandaloneCoroutine(
    parentContext: CoroutineContext,
    active: Boolean
) : AbstractCoroutine<Unit>(parentContext, initParentJob = true, active = active) {
    override fun handleJobException(exception: Throwable): Boolean {
        handleCoroutineException(context, exception)
        return true
    }
}
```
##### 3：coroutine.start(start, coroutine, block)默认情况下会调用CoroutineStart.DEFAULT的invoke方法
```kotlin
//StandaloneCoroutine的start方法
public abstract class AbstractCoroutine<in T>(
    parentContext: CoroutineContext,
    initParentJob: Boolean,
    active: Boolean
) : JobSupport(active), Job, Continuation<T>, CoroutineScope {
    public fun <R> start(start: CoroutineStart, receiver: R, block: suspend R.() -> T) {
        start(block, receiver, this)
    }
}
//StandaloneCoroutine的start方法会调用CoroutineStart.DEFAULT的invoke方法
public enum class CoroutineStart {
    DEFAULT,
    ...
    public operator fun <R, T> invoke(block: suspend R.() -> T, receiver: R, completion: Continuation<T>): Unit =
        when (this) {
            DEFAULT -> block.startCoroutineCancellable(receiver, completion)
            ATOMIC -> block.startCoroutine(receiver, completion)
            UNDISPATCHED -> block.startCoroutineUndispatched(receiver, completion)
            LAZY -> Unit // will start lazily
        }
}

```
#### 进入block.startCoroutineCancellable(receiver, completion)
注意这里的receiver和completion都为刚刚创建的StandaloneCoroutine协程实例
```kotlin
internal fun <R, T> (suspend (R) -> T).startCoroutineCancellable(
    receiver: R, 
    completion: Continuation<T>,
    onCancellation: ((cause: Throwable) -> Unit)? = null
) = runSafely(completion) {
    // 1
    createCoroutineUnintercepted(receiver, completion)
    // 2
    .intercepted()
    // 3
    .resumeCancellableWith(Result.success(Unit), onCancellation)
}
```
##### 1：createCoroutineUnintercepted(receiver, completion)创建SuspendLambda对象
```kotlin
public actual fun <R, T> (suspend R.() -> T).createCoroutineUnintercepted(
    receiver: R,
    completion: Continuation<T>
): Continuation<Unit> {
    // 1
    val probeCompletion = probeCoroutineCreated(completion)
    return if (this is BaseContinuationImpl)
        // 2
        create(receiver, probeCompletion)
    else {
        createCoroutineFromSuspendFunction(probeCompletion) {
            (this as Function2<R, Continuation<T>, Any?>).invoke(receiver, it)
        }
    }
}
```
1：probeCoroutineCreated为调试所用的api，正式环境会直接返回completion。所以probeCompletion就是completion。  
2：this即(suspend R.() -> T)类型的当前对象，也就是通过launch{}开启协程所传入的一个函数类型对象。前面介绍CPS转换的时候说过，(suspend R.() -> T)类型在编译的时候会转换成SuspendLambda的子类，SuspendLambda的基类是BaseContinuationImpl所以会走create方法。

在BaseContinuationImpl中create并没有实现，具体的实现在CPS转换后生成的类中
```java
internal abstract class BaseContinuationImpl(
    public val completion: Continuation<Any?>?
) {
    public open fun create(value: Any?, completion: Continuation<*>): Continuation<Unit> {
        throw UnsupportedOperationException("create(Any?;Continuation) has not been overridden")
    }
}
```

以下是某个(suspend R.() -> T)类型经过CPS转换后的类，可以看到create方法就是调用了基类的两个参数的构造。
```java
final class MainActivity$onCreate$1 extends SuspendLambda implements Function2 {
   int I$0;
   Object L$0;
   Object L$1;
   int label;
   final MainActivity this$0;

   MainActivity$onCreate$1(MainActivity var1, Continuation var2) {
      super(2, var2);
      this.this$0 = var1;
   }

   public final Continuation create(Object var1, Continuation var2) {
      return (Continuation)(new MainActivity$onCreate$1(this.this$0, var2));
   }

   ...

}
```

继续往上跟踪，SuspendLambda的两个参数构造又网上调用了ContinuationImpl的一个参数的构造。
```kotlin
internal abstract class SuspendLambda(
    public override val arity: Int,
    completion: Continuation<Any?>?
) : ContinuationImpl(completion), FunctionBase<Any?>, SuspendFunction {
    ...
}
```

再继续往上追踪，ContinuationImpl的一个参数的构造会调用自身两个参数的构造并将传入的completion的Context保存起来。
```kotlin
internal abstract class ContinuationImpl(
    completion: Continuation<Any?>?,
    private val _context: CoroutineContext?
) : BaseContinuationImpl(completion) {

    constructor(completion: Continuation<Any?>?) : this(completion, completion?.context)

    public override val context: CoroutineContext
        get() = _context!!

    ...
}
```

##### 2：intercepted()生成DispatchedContinuation
```kotlin
internal abstract class ContinuationImpl(
    completion: Continuation<Any?>?,
    private val _context: CoroutineContext?
) : BaseContinuationImpl(completion) {
    public fun intercepted(): Continuation<Any?> =
        intercepted
            ?: (context[ContinuationInterceptor]?.interceptContinuation(this) ?: this)
                .also { intercepted = it }
}
```
##### 3：resumeCancellableWith使用调度器执行逻辑代码

![图片替换文字](https://raw.githubusercontent.com/David-Su/Review/31bbd0e02fdd559ebf84dce6dc3da61f86addd89/Android/%E9%99%84%E4%BB%B6/coroutine_launch.svg)
