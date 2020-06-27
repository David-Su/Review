package suk.practice

import android.app.Application
import android.content.Context
import android.util.Log
import dalvik.system.BaseDexClassLoader
import dalvik.system.DexClassLoader
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * @author SuK
 * @time 2020/6/26 0:05
 * @des
 */
class MyApp : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        hotFix()
    }

    /**
     * 注意两点：
     * 1.合并Element数组必须用Array.newInstance，使用其他方式会生成Object[]，这在用反射设置pathList的dexElements的时候会报错。
     * 2.8.0后生成Element数组的ClassLoader和系统类加载器必须是同一个。
     */
    private fun hotFix() {
        //把dex文件放到cache文件夹（其实可以直接放在外存上）
        val ins = assets.open("FixClass.dex")
        val file = File(cacheDir, "FixClass.dex")
        val outs = FileOutputStream(file)
        val buff = ByteArray(1024)
        var length = -1
        while (ins.read(buff).takeIf { it != -1 }?.also { length = it } != null) {
            outs.write(buff, 0, length)
        }

        //从目标loader中获取pathList，并且调用pathList的静态方法makeDexElements生成Element数组
        fun makeDexElements(loader: ClassLoader): Any {
            //获取dexPathList
            val pathList = BaseDexClassLoader::class
                .java
                .getDeclaredField("pathList")
                .also { it.isAccessible = true }
                .get(loader)
            //获取makeDexElements方法
            val dexElements = Class
                .forName("dalvik.system.DexPathList")
                .getDeclaredMethod(
                    "makeDexElements",
                    List::class.java,
                    File::class.java,
                    List::class.java,
                    ClassLoader::class.java
                )
                .also { it.isAccessible = true }
                .invoke(null, listOf(file), null, arrayListOf<IOException>(), classLoader)
            return dexElements
        }

        //从目标loader中获取pathList，进而获取pathList的Element数组
        fun getDexElements(loader: ClassLoader): Any {
            //获取dexPathList
            val pathList = BaseDexClassLoader::class
                .java
                .getDeclaredField("pathList")
                .also { it.isAccessible = true }
                .get(loader)
            //获取dexElements
            val dexElements = Class
                .forName("dalvik.system.DexPathList")
                .getDeclaredField("dexElements")
                .also { it.isAccessible = true }
                .get(pathList)
            return dexElements
        }

        //从目标loader中获取pathList，进而设置pathList的Element数组
        fun setDexElements(loader: Any, target: ClassLoader) {
            //获取dexPathList
            val pathList = BaseDexClassLoader::class
                .java
                .getDeclaredField("pathList")
                .also { it.isAccessible = true }
                .get(target)
            Class
                .forName("dalvik.system.DexPathList")
                .getDeclaredField("dexElements")
                .also { it.isAccessible = true }
                .set(pathList, loader)
        }


        val newDexElements = makeDexElements(classLoader) as Array<*>
        val oldDexElements = getDexElements(classLoader) as Array<*>


        //新旧elements合并，新的在前。

        val elements = java.lang.reflect.Array.newInstance(
            oldDexElements.javaClass.componentType,
            newDexElements.size + oldDexElements.size
        ) as Array<Any>
        System.arraycopy(newDexElements, 0, elements, 0, newDexElements.size)
        System.arraycopy(oldDexElements, 0, elements, newDexElements.size, oldDexElements.size)

        //把合并好的elements设置给PathClassLoader
        setDexElements(elements, classLoader)

        getDexElements(classLoader)
        for (ele in getDexElements(classLoader) as Array<*>) {
            Log.d("hotfix", ele.toString())
        }
    }
}