###背景
Android插件化作为每个合格的Android程序员都必须会的技术，被各大厂广泛使用。随着各大厂对移动互联网的垄断，我们渐渐发现app集成的功能越来越多。比如如下几个app（携程、淘宝、支付宝）：
<img src="http://img.blog.csdn.net/20180303170318440?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvY29saW5hbmRyb2lk/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast" width="30%" height="30%" /> <img src="http://img.blog.csdn.net/20180303170440044?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvY29saW5hbmRyb2lk/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast" width="30%" height="30%" /> <img src="http://img.blog.csdn.net/20180303170456493?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvY29saW5hbmRyb2lk/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast" width="30%" height="30%" />
可以看到每一个app都被集成了无数的功能入口，就拿淘宝来说，“天猫”、“外卖”、“飞猪”、“拍卖”，这任何一个入口都其实是一个app，只不过被集成到“淘宝”这个入口里了。如果没有插件化技术，很难想象淘宝app的size会有多大。很可能有几个GB！！

再来看看支付宝，可以发现支付宝中提供了很多第三方app的入口，而点击这些入口跳转的也都是native页面。应用市场上的支付宝app一共只有二三十MB，而如果这些app都集成到支付宝中，那支付宝的size就不是二三十MB了，那就是二三十GB了！！

本篇blog的主题是介绍Android插件化技术，并且会提供一个仿支付宝插件化技术的demo，告诉你**支付宝是如何把一个第三方app作为插件集成到自己的app里的**。

###插件化好处
1. 宿主和插件分开编译
编译时只需要编译宿主app，插件app是在编译好后下发到宿主app里的。
2. 并发开发
宿主app什么时候发布版本跟插件app什么时候开发完没有关系，宿主app只要开发完并且为插件app提供一个入口就可以了。
3. 动态更新插件
插件app在开发完后下发到宿主app里，点击相应的入口就可以跳转到最新版的插件app了。
4. 按需下载模块
5. 解决方法数或变量数爆棚

###随便一个app都能集成到支付宝吗？ 
答案是：不能！
我们来思考，支付宝要跳转到一个插件的Activity，而**插件是没有被安装的，它没有上下文，也就没有生命周期，那么插件Activity的生命周期就要由宿主app来控制**。为此，我们需要建立一套**标准**。
话不多说，我们直接开始撸代码。

###插件app的activity没有在宿主app中注册，该怎么办？
插桩，一个空的Activity，专门用来加载插件app中的activity，这个Activity叫ProxyActivity，后面我会具体去讲这个空Activity该如何实现。我们只需要在宿主app里注册这个Activity就可以了。
<img src="http://img-blog.csdn.net/20180315104103339?watermark/2/text/Ly9ibG9nLmNzZG4ubmV0L2NvbGluYW5kcm9pZA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70" width="80%" height="80%" />

###加载插件app中的Activity
1. 实际场景中插件apk肯定是由服务端下发后，保存到SD卡的某个文件夹下。这里将编译好的插件apk放到手机外置SD卡的根目录中，我们来演示宿主app如何去加载插件app中的Activity。
![这里写图片描述](http://img.blog.csdn.net/20180303233806234?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvY29saW5hbmRyb2lk/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

2. 加载该Activity的**类**
接下来我们来看下FluginManager的loadPath方法如何实现。如果要实现这个功能，首先想到的肯定是用反射。
<img src="http://img.blog.csdn.net/20180303233501752?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvY29saW5hbmRyb2lk/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast" width="60%" height="60%" />
可是你别忘记了，插件app根本就没有安装，这里是无法找到这个Class的。我们需要DexClassLoader来完成Activity类的加载。
![这里写图片描述](http://img.blog.csdn.net/20180304002129545?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvY29saW5hbmRyb2lk/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)PluginManager的getDexClassLoader的实现如下：
![这里写图片描述](http://img.blog.csdn.net/20180304001108954?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvY29saW5hbmRyb2lk/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

3. 加载该Activity的**资源文件**
讲完了如何加载Activity，我们来讲下如何加载Activity中用到的资源文件。我们在日常开发中需要资源文件时，我们是通过getResources()来获取。例如加载一个图片:

```
getResources().getDrawable()
```
可现在我们需要获取的是另外一个app的资源，所以这里就需要自己实现一个getResources()方法。
![这里写图片描述](http://img.blog.csdn.net/20180304002329239?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvY29saW5hbmRyb2lk/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)PluginManager的getResources方法实现如下：
![这里写图片描述](http://img.blog.csdn.net/20180304001934172?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvY29saW5hbmRyb2lk/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

至此，一个插件app的activity加载功能就实现完成了，下面我们来看如何跳转。

###跳转到插件app中的Activity
![这里写图片描述](http://img.blog.csdn.net/20180306000031199?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvY29saW5hbmRyb2lk/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
由于我们需要读取SD卡中的插件apk，这里别忘记加上SD卡的读写权限
![这里写图片描述](http://img.blog.csdn.net/20180306000335943?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvY29saW5hbmRyb2lk/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
这就可以实现跳转了。下面我们来看下效果
<img src="http://img.blog.csdn.net/20180306000747334?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvY29saW5hbmRyb2lk/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70" width="30%" height="30%" />
奇怪，为什么我们跳转到插件app的activity是空白的？我们来看下插件app的activity应该长什么样子。
<img src="http://img.blog.csdn.net/20180306000922342?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvY29saW5hbmRyb2lk/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70" width="30%" height="30%" />
当然这里我只在插件app的主activity里放了一张图片，并没有写复杂的布局。可是为什么我们跳过来的是空白页呢？我们再看下ProxyActivity的代码：
```
package com.ctrip.pluginapplication

import android.content.res.Resources
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * 壳！专门用来加载插件Activity
 * @author Zhenhua on 2018/3/3.
 * @email zhshan@ctrip.com ^.^
 */
class ProxyActivity : AppCompatActivity() {

    /**
     * 要跳转的activity的name
     */
    private var className = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /**
         * step1：得到插件app的activity的className
         */
        className = intent.getStringExtra("className")
        /**
         * step2：通过反射拿到class，
         * 但不能用以下方式，因为插件app没有被安装！
         */
//        classLoader.loadClass(className)
//        Class.forName(className)


    }

    override fun getClassLoader(): ClassLoader {
        //不用系统的ClassLoader，用dexClassLoader加载
        return PluginManager.getInstance().getDexClassLoader() as? ClassLoader
                ?: super.getClassLoader()
    }

    override fun getResources(): Resources {
        //不用系统的resources，自己实现一个resources
        return PluginManager.getInstance().getResources() ?: super.getResources()
    }
}
```
我们发现，我们这里还没有在ProxyActivity里写逻辑啊，我们只是得到了插件app的主activity的name，这时activity还没有生命周期。?我们接着来实现。我们需要让ProxyActivity控制插件app的activity的生命周期，所以我们需要得到插件app的activity的实例，然后去控制其生命周期：
```
package com.ctrip.pluginapplication

import android.content.res.Resources
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ctrip.standard.AppInterface

/**
 * 壳！专门用来加载插件Activity
 * @author Zhenhua on 2018/3/3.
 * @email zhshan@ctrip.com ^.^
 */
class ProxyActivity : AppCompatActivity() {

    /**
     * 要跳转的activity的name
     */
    private var className = ""
    private var appInterface: AppInterface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /**
         * step1：得到插件app的activity的className
         */
        className = intent.getStringExtra("className")
        /**
         * step2：通过反射拿到class，
         * 但不能用以下方式
         * classLoader.loadClass(className)
         * Class.forName(className)
         * 因为插件app没有被安装！
         * 这里我们调用我们重写过多classLoader
         */
        var activityClass = classLoader.loadClass(className)
        var constructor = activityClass.getConstructor()
        var instance = constructor.newInstance()

        appInterface = instance as?AppInterface
        appInterface?.attach(this)
        var bundle = Bundle()
        appInterface?.onCreate(bundle)

    }

    override fun onStart() {
        super.onStart()
        appInterface?.onStart()
    }

    override fun onResume() {
        super.onResume()
        appInterface?.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        appInterface?.onDestroy()
    }

    override fun getClassLoader(): ClassLoader {
        //不用系统的ClassLoader，用dexClassLoader加载
        return PluginManager.getInstance().getDexClassLoader() as? ClassLoader
                ?: super.getClassLoader()
    }

    override fun getResources(): Resources {
        //不用系统的resources，自己实现一个resources
        return PluginManager.getInstance().getResources() ?: super.getResources()
    }
}
```
这时我们就可以成功跳转了。ok，插件化实现完成。我们来看下效果。
<img src="https://images2018.cnblogs.com/blog/1269107/201803/1269107-20180308000156592-867821103.gif" width="40%" height="30%" />
这里**[附上demo（点击下载）](https://github.com/colinNaive/PluginApplication)**，如有任何疑问可留言提问，博主每天都会查看。

~~~~~~~~华丽丽的分割线：在插件app中实现更多功能~~~~~~~
之前我们的插件app的activity其实就只是加载了一个imageView，我们现在来实现这样一个功能：“点击ImageView，弹出一个toast”。
代码如下：
![这里写图片描述](//img-blog.csdn.net/20180315112844158?watermark/2/text/Ly9ibG9nLmNzZG4ubmV0L2NvbGluYW5kcm9pZA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
我们来看下效果。。
然而，点击竟然crash了。我们来贴下错误日志：
![这里写图片描述](//img-blog.csdn.net/20180315113742547?watermark/2/text/Ly9ibG9nLmNzZG4ubmV0L2NvbGluYW5kcm9pZA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
原来我们在插件Activity中不能用自己的上下文，我们应该用that！！
<img src="https://img-blog.csdn.net/20180315114849699?watermark/2/text/Ly9ibG9nLmNzZG4ubmV0L2NvbGluYW5kcm9pZA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70" width="40%" height="30%" />
代码已经[**更新到github**](https://github.com/colinNaive/PluginApplication)上，欢迎下载体验。