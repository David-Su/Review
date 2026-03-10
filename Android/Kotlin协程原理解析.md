# Kotlin协程原理解析

## 协程介绍
**Kotlin协程有以下特点**：
* 协程可以用同步的代码编写方式编写异步的代码。  

  举个日常开发的场景：通过网络请求获取数据并渲染到TextView。通过这个例子可以很直观的感受到协程的优点，在处理异步的场景中，协程的代码更为直观优雅。

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

首先先介绍一下协程中的一些基础概念，然后再通过具体例子解析开启协程的原理。
### 调度器
协程的运行需要调度器，调度器会调度线程执行协程中的任务。
* **DEFAULT**  
默认的调度器，开启协程如果不传入一个调度器的话，默认会用DEFAULT。执行时会使用内部的线程池执行，线程池的线程数等于 CPU 核心数，所以适合CPU密集型计算的任务。
* **IO**  
执行时会使用内部的线程池执行，线程数可以进行动态扩展，适合IO 密集型任务（IO任务主要是等待，不会占用太多CPU资源）。
* **MAIN**  
一个单线程执行任务的调度器。在安卓中，代表在主线程执行任务。
### 挂起
在协程中，挂起是指一个协程的执行可以在不阻塞线程的情况下暂停和恢复。我们可以用suspend关键字修饰一个函数，表示该函数可能会被挂起。suspend函数不一定会实现真正的挂起，只有释放了当前执行中的线程才是真正的挂起（如使用withContext、delay等）。suspend函数一定要放在suspend函数中执行。

真正的挂起
```kotlin
GlobalScope.launch(Dispatchers.Main) {
    //开始挂起
    withContext(Dispatchers.IO) {
        ...
    }
}
```
不能真正挂起
```kotlin
GlobalScope.launch(Dispatchers.Main) {
    suspend fun test(): String {
        return ""
    }
    //相当于执行了一个普通方法
    test()
}
```
### 协程作用域CoroutineScope
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
### Continuation
Continuation表示续体，怎么理解呢，协程运行中当调用调用某个suspend函数进行挂起的时候这个Continuation会参与调度，当调度完成后会通过Continuation的resumeWith恢复挂起点的重新运行。

**Continuation的继承树**   
![](https://raw.githubusercontent.com/David-Su/David-Su.github.io/757a0c1120e7682293bf002d2c66075d71c3c36b/assets/images/Coroutine-SuspendLambda%E7%BB%A7%E6%89%BF%E5%85%B3%E7%B3%BB.drawio.svg)

在后续讲解CPS转换的时候，suspend lambda会转换成横一个SuspendLambda的子类，而suspend方法则会转换成ContinuationImpl的子类。这里可以提前了解一下。

### CoroutineContext协程运行上下文
CoroutineContext代表着协程运行的上下文。CoroutineContext可以相加所以它是一个复合的概念，一般来说不同的CoroutineContext会继承CoroutineContext.Element实行相加，在需要使用的场景中通过Key从一个复合的CoroutineContext中取出对应的Element。

常见的Element有：
* CoroutineDispatcher
* Job
* CoroutineName
* CoroutineExceptionHandler

#### CoroutineContext相加
CoroutineContext可以进行相加，生成一个CombinedContext。如果左右两边的Context的存在同名的Key，右边的会覆盖左边的。对于拦截器会进行特殊处理，拦截器或者包含拦截器的Element始终会在CombinedContext的右边，方便查找（因为查找会优先查找右边）。
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

### cps转换
Kotlin是基于JVM的语言，为了使协程这种基于语言层面的并发设计模式能正常跑在JVM上面，Kotlin编译器会对相关的代码进行CPS转换，进行转换的情况有以下的几种：
#### suspend CoroutineScope.() -> T
这种类型对应开启协程所传入的一个函数类型的对象block
```kotlin
public fun CoroutineScope.launch(
    ...
    block: suspend CoroutineScope.() -> Unit
): Job {
    ...
}
```
**转换步骤**  
* block对象会转换成一个继承SuspendLambda的内部类对象
* 该内部类对象生成状态机逻辑

**以下为示例**
```kotlin
//源码
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
```java
//cps转换后的代码(经过简化)
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


#### suspend方法
**转换步骤**
* 转换成一个普通的java方法
* 增加一个Continuatio参数
* 返回值改为Object
* 当该方法本身有调用其他的suspend方法时
  * 生成一个该方法对应的内部类
  * 方法逻辑改为状态机逻辑

转换的思路跟suspend CoroutineScope.() -> T转换的思路是相似的，不同的是suspend CoroutineScope.() -> T的状态机逻辑在invokeSuspend中，而suspend方法的状态机逻辑在对应的java方法的方法体中。

**以下为示例**
```kotlin
//源码
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
```java
//cps转换后的代码(经过简化)
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
## 开启协程
以最简单的开启协程的方式举例
```kotlin
GlobalScope.launch {
    //todo
}
```
### 开启协程用的是CoroutineScope的launch方法
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
#### 1：newCoroutineContext(context)将传入的Context与当前协程的Context合并
```kotlin
public actual fun CoroutineScope.newCoroutineContext(context: CoroutineContext): CoroutineContext {
    // 1.1
    val combined = foldCopies(coroutineContext, context, true)
    // 1.2
    val debug = if (DEBUG) combined + CoroutineId(COROUTINE_ID.incrementAndGet()) else combined
    // 1.3
    return if (combined !== Dispatchers.Default && combined[ContinuationInterceptor] == null)
        debug + Dispatchers.Default else debug
}
```
1.1：一般情况下，就是将传入的Context与当前协程的Context相加  
1.2：非DEBUG模式，直接使用combined   
1.3：确保了返回的Context一定有拦截器（一般的调度器都是拦截器，如：Dispatchers.Default），如果没有拦截器就给combined加一个Dispatchers.Default
#### 2：默认情况下，创建一个新的StandaloneCoroutine协程实例
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
#### 3：coroutine.start(start, coroutine, block)默认情况下会调用CoroutineStart.DEFAULT的invoke方法
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
### 进入block.startCoroutineCancellable(receiver, completion)
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
#### 1：createCoroutineUnintercepted(receiver, completion)创建SuspendLambda对象
```kotlin
public actual fun <R, T> (suspend R.() -> T).createCoroutineUnintercepted(
    receiver: R,
    completion: Continuation<T>
): Continuation<Unit> {
    // 1.1
    val probeCompletion = probeCoroutineCreated(completion)
    return if (this is BaseContinuationImpl)
        // 1.2
        create(receiver, probeCompletion)
    else {
        createCoroutineFromSuspendFunction(probeCompletion) {
            (this as Function2<R, Continuation<T>, Any?>).invoke(receiver, it)
        }
    }
}
```
1.1：probeCoroutineCreated为调试所用的api，正式环境会直接返回completion。所以probeCompletion就是completion。  
1.2：this即(suspend R.() -> T)类型的当前对象，也就是通过launch{}开启协程所传入的一个函数类型对象。前面介绍CPS转换的时候说过，(suspend R.() -> T)类型在编译的时候会转换成SuspendLambda的子类，SuspendLambda的基类是BaseContinuationImpl所以会走create方法。

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

#### 2：intercepted()生成DispatchedContinuation
ContinuationInterceptor是一个CoroutineContext.Element，也就是context内的组成元素。
context[ContinuationInterceptor]这种形式的代码可以从CoroutineContext获取到其中的ContinuationInterceptor。接着调用这个ContinuationInterceptor的interceptContinuation方法并把this作为参数传入。
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
这个ContinuationInterceptor就是开启协程传入的Dispatchers.Default，interceptContinuation的具体实现在Dispatchers.Default的基类CoroutineDispatcher中，可见这个方法返回了一个DispatchedContinuation。
```kotlin
public abstract class CoroutineDispatcher :
    AbstractCoroutineContextElement(ContinuationInterceptor), ContinuationInterceptor {
    public final override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T> = DispatchedContinuation(this, continuation)
}
```
#### 3：resumeCancellableWith使用调度器执行逻辑代码
intercepted()中返回的DispatchedContinuation会执行其resumeCancellableWith方法.在Dispatchers.Default的实现中,isDispatchNeeded直接是返回true,所以会走if分支.接下来我们看下dispatch方法做了什么.
```kotlin
inline fun resumeCancellableWith(
    result: Result<T>,
    noinline onCancellation: ((cause: Throwable) -> Unit)?
) {
    val state = result.toState(onCancellation)
    if (dispatcher.isDispatchNeeded(context)) {
        _state = state
        resumeMode = MODE_CANCELLABLE
        dispatcher.dispatch(context, this)
    } else {
        executeUnconfined(state, MODE_CANCELLABLE) {
            if (!resumeCancelled(state)) {
                resumeUndispatchedWith(result)
            }
        }
    }
}
```
dispatch的实现在SchedulerCoroutineDispatcher,SchedulerCoroutineDispatcher是Dispatchers.Default的基类.可以看到dispatch被一个CoroutineScheduler对象接管了.
```kotlin
internal open class SchedulerCoroutineDispatcher(
    private val corePoolSize: Int = CORE_POOL_SIZE,
    private val maxPoolSize: Int = MAX_POOL_SIZE,
    private val idleWorkerKeepAliveNs: Long = IDLE_WORKER_KEEP_ALIVE_NS,
    private val schedulerName: String = "CoroutineScheduler",
) : ExecutorCoroutineDispatcher() {

    // This is variable for test purposes, so that we can reinitialize from clean state
    private var coroutineScheduler = createScheduler()

    private fun createScheduler() =
        CoroutineScheduler(corePoolSize, maxPoolSize, idleWorkerKeepAliveNs, schedulerName)

    override fun dispatch(context: CoroutineContext, block: Runnable): Unit = coroutineScheduler.dispatch(block)
}
```
继续走进CoroutineScheduler的dispatch方法。这个方法主要是将block封装成一个task并交由CoroutineScheduler调度执行。调度的机制跟Java的线程池类似，这里不再展开。重点关注3.1中封装的task。
```kotlin
fun dispatch(block: Runnable, taskContext: TaskContext = NonBlockingContext, tailDispatch: Boolean = false) {
    trackTask()
    //3.1
    val task = createTask(block, taskContext)
    //获取当前线程的Worker
    val currentWorker = currentWorker()
    //尝试加入Worker的任务队列
    val notAdded = currentWorker.submitToLocalQueue(task, tailDispatch)
    //notAdded != null表示的是加入任务队列失败
    if (notAdded != null) {
        //尝试加入全局任务队列
        if (!addToGlobalQueue(notAdded)) {
            // Global queue is closed in the last step of close/shutdown -- no more tasks should be accepted
            throw RejectedExecutionException("$schedulerName was terminated")
        }
    }
    //以上代码用于确保将任务加入到队列，以下代码取保有足够的工作线程
    val skipUnpark = tailDispatch && currentWorker != null
    if (task.mode == TASK_NON_BLOCKING) {
        if (skipUnpark) return
        signalCpuWork()
    } else {
        // Increment blocking tasks anyway
        signalBlockingWork(skipUnpark = skipUnpark)
    }
}
```
可以看到createTask返回的是一个Task的子类TaskImpl，而Task本身是个Runnable。当这个Task被执行时，会执行它所持有的block的run方法，这个block就是resumeCancellableWith方法中传入
CoroutineScheduler的dispatch方法的参数this。所以block就是DispatchedContinuation。
```kotlin
fun createTask(block: Runnable, taskContext: TaskContext): Task {
    val nanoTime = schedulerTimeSource.nanoTime()
    if (block is Task) {
        block.submissionTime = nanoTime
        block.taskContext = taskContext
        return block
    }
    return TaskImpl(block, nanoTime, taskContext)
}

internal class TaskImpl(
    @JvmField val block: Runnable,
    submissionTime: Long,
    taskContext: TaskContext
) : Task(submissionTime, taskContext) {
    override fun run() {
        try {
            block.run()
        } finally {
            taskContext.afterTask()
        }
    }
}
```
这个block也就是DispatchedContinuation并不是直接继承Runnable，而是通过继承DispatchedTask间接继承Runnable，以下是看DispatchedTask的run方法。在协程正常执行的情况下代码会走到3.1处，resume是Continuation的扩展方法，最终会执行Continuation的resumeWith。在这段代码里continuation这个对象是在DispatchedContinuation构造的时候传入的，也就是本次示例中的MainActivity$onCreate$1。
```kotlin
public final override fun run() {
    assert { resumeMode != MODE_UNINITIALIZED } // should have been set before dispatching
    val taskContext = this.taskContext
    var fatalException: Throwable? = null
    try {
        val delegate = delegate as DispatchedContinuation<T>
        val continuation = delegate.continuation
        withContinuationContext(continuation, delegate.countOrElement) {
            val context = continuation.context
            val state = takeState() // NOTE: Must take state in any case, even if cancelled
            val exception = getExceptionalResult(state)
            /*
                * Check whether continuation was originally resumed with an exception.
                * If so, it dominates cancellation, otherwise the original exception
                * will be silently lost.
                */
            val job = if (exception == null && resumeMode.isCancellableMode) context[Job] else null
            if (job != null && !job.isActive) {
                val cause = job.getCancellationException()
                cancelCompletedResult(state, cause)
                continuation.resumeWithStackTrace(cause)
            } else {
                if (exception != null) {
                    continuation.resumeWithException(exception)
                } else {
                    //3.1
                    continuation.resume(getSuccessfulResult(state))
                }
            }
        }
    } catch (e: Throwable) {
        // This instead of runCatching to have nicer stacktrace and debug experience
        fatalException = e
    } finally {
        val result = runCatching { taskContext.afterTask() }
        handleFatalException(fatalException, result.exceptionOrNull())
    }
}
```

resume是Continuation的扩展方法，相当于调用了resumeWith。
```kotlin
/**
 * Resumes the execution of the corresponding coroutine passing [value] as the return value of the last suspension point.
 */
@SinceKotlin("1.3")
@InlineOnly
public inline fun <T> Continuation<T>.resume(value: T): Unit = resumeWith(Result.success(value))
```

<a id = "jump1"></a>
resumeWith是BaseContinuationImpl中的方法，BaseContinuationImpl也是所有开启协程传入的lambda通过cps转换后生成的类的基类（在本例中就是MainActivity$onCreate$1）。可以看到方法中开启了一个循环，先执行自身的invokeSuspend方法获取一个结果，再调用上游的Continuation的invokeSuspend。
```kotlin
internal abstract class BaseContinuationImpl(
    public val completion: Continuation<Any?>?
) : Continuation<Any?>, CoroutineStackFrame, Serializable {

    public final override fun resumeWith(result: Result<Any?>) {
        var current = this
        var param = result
        while (true) {
            probeCoroutineResumed(current)
            with(current) {
                val completion = completion!!
                val outcome: Result<Any?> =
                    try {
                        val outcome = invokeSuspend(param)
                        if (outcome === COROUTINE_SUSPENDED) return
                        Result.success(outcome)
                    } catch (exception: Throwable) {
                        Result.failure(exception)
                    }
                releaseIntercepted()
                if (completion is BaseContinuationImpl) {
                    current = completion
                    param = outcome
                } else {
                    completion.resumeWith(outcome)
                    return
                }
            }
        }
    }

    ...
}
```

## withContext切换调度
在suspend方法中，可通过withContext并传入一个suspend lamda进行一个调度器的切换。以下是withContext这里我忽略了相同调度器的情况，重点看withConetext切换另外的调度器的情况。
### withContext源码
```kotlin
public suspend fun <T> withContext(
    context: CoroutineContext,
    block: suspend CoroutineScope.() -> T
): T {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
            //1
    return suspendCoroutineUninterceptedOrReturn sc@ { uCont ->
        // compute new context
        //2
        val oldContext = uCont.context
        // Copy CopyableThreadContextElement if necessary
        //3
        val newContext = oldContext.newCoroutineContext(context)
        ...
        // SLOW PATH -- use new dispatcher
        //4
        val coroutine = DispatchedCoroutine(newContext, uCont)
        block.startCoroutineCancellable(coroutine, coroutine)
        coroutine.getResult()
    }
}

```
#### 1：获取外部Continuation
withContext本身就是一个suspend方法，所以通过cps转换的时候参数本来就会新增一个Continuation，suspendCoroutineUninterceptedOrReturn相当于让开发者可以在代码编写的时候就用到这个Continuation。
#### 2：获取外部Continuation的Context
从传入的Continuation中获取Context，即挂起点的Context。
#### 3：合并Context
外部Continuation的Context与传入的Context合并，调度器会优先使用传入的Context中的调度器。
#### 4：开始调度
在介绍GlobalScope.launch的时候，同样会使用block调用startCoroutineCancellable构建自身并持有着一个上游的Continuation，区别只是GlobalScope.launch使用的是StandaloneCoroutine，withContext用的则是DispatchedCoroutine。而startCoroutineCancellable最终会通过调度执行到block所代表的BaseContinuationImpl的resumeWith[点击跳转回顾](#jump1)，resumeWith最终又会执行上游Continuation的resumeWith。所以接下来我们来看一下DispatchedCoroutine和ScopeCoroutine。

### DispatchedCoroutine与ScopeCoroutine
DispatchedCoroutin继承ScopeCoroutine，ScopeCoroutine继承AbstractCoroutine，ScopeCoroutine和DispatchedCoroutin都重写了afterCompletion和afterResume，所以以DispatchedCoroutin为准就好。当DispatchedCoroutine与ScopeCoroutine的resumeWith被调用后，都会执行afterResume，这是他们共同的基类AbstractCoroutine的逻辑。  

从这里可分析出，使用withContext切换了调度器执行之后，当调度器执行完block内的逻辑后会通过DispatchedCoroutine的afterResume重新开始挂起点处的调度。而GlobalScope.launch所对应StandaloneCoroutine因为他本身就是协程的入口，不存在挂起点的概念，所以并不需要切回原调度器，这里需要注意的一点就是afterCompletion是JobSupport的方法并且只是个空实现。

AbstractCoroutine
```kotlin
public abstract class AbstractCoroutine<in T>(
    parentContext: CoroutineContext,
    initParentJob: Boolean,
    active: Boolean
) : JobSupport(active), Job, Continuation<T>, CoroutineScope {

    /**
     * Completes execution of this with coroutine with the specified result.
     */
    public final override fun resumeWith(result: Result<T>) {
        val state = makeCompletingOnce(result.toState())
        if (state === COMPLETING_WAITING_CHILDREN) return
        afterResume(state)
    }

    protected open fun afterResume(state: Any?): Unit = afterCompletion(state)

    ...
}
```
StandaloneCoroutine
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
DispatchedCoroutine
```kotlin
internal class DispatchedCoroutine<in T> internal constructor(
    context: CoroutineContext,
    uCont: Continuation<T>
) : ScopeCoroutine<T>(context, uCont) {
    @JvmField
    public val _decision = atomic(UNDECIDED)

    private fun trySuspend(): Boolean {
        _decision.loop { decision ->
            when (decision) {
                UNDECIDED -> if (this._decision.compareAndSet(UNDECIDED, SUSPENDED)) return true
                RESUMED -> return false
                else -> error("Already suspended")
            }
        }
    }

    private fun tryResume(): Boolean {
        _decision.loop { decision ->
            when (decision) {
                UNDECIDED -> if (this._decision.compareAndSet(UNDECIDED, RESUMED)) return true
                SUSPENDED -> return false
                else -> error("Already resumed")
            }
        }
    }

    override fun afterCompletion(state: Any?) {
        // Call afterResume from afterCompletion and not vice-versa, because stack-size is more
        // important for afterResume implementation
        afterResume(state)
    }

    override fun afterResume(state: Any?) {
        if (tryResume()) return // completed before getResult invocation -- bail out
        // Resume in a cancellable way because we have to switch back to the original dispatcher
        uCont.intercepted().resumeCancellableWith(recoverResult(state, uCont))
    }

    internal fun getResult(): Any? {
        if (trySuspend()) return COROUTINE_SUSPENDED
        // otherwise, onCompletionInternal was already invoked & invoked tryResume, and the result is in the state
        val state = this.state.unboxState()
        if (state is CompletedExceptionally) throw state.cause
        @Suppress("UNCHECKED_CAST")
        return state as T
    }
}
```
