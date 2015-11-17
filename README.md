# TestViewPageAutoScroll
基于 ViewPage 实现的轮播图

java.lang.IllegalStateException: The application's PagerAdapter changed the adapter's contents without calling PagerAdapter#notifyDataSetChanged! 

After ADT 22 the PagerAdapter has gotten very strict about calling notifyDataSetChanged() before calling getCount().  
It evidently keeps track of what it thinks the count should be and if this is not the same as what getCount() returns it throws this exception. 
So the solution is simply to call notifyDataSetChanged() on the adapter every time the size of the data changes.

在adt22之后，PagerAdapter对于notifyDataSetChanged()和getCount()的执行顺序是非常严格的，系统跟踪count的值，如果这个值和getCount返回的值不一致，就会抛出这个异常。所以为了保证getCount总是返回一个正确的值，那么在初始化ViewPager时，应先给Adapter初始化内容后再将该adapter传给ViewPager，如果不这样处理，在更新adapter的内容后，应该调用一下adapter的notifyDataSetChanged方法。

使用一个小图片平铺到 ImageView 中或 Activity 背景

首先必须在 res/drawable 目录下包含一张图片，如：background.png

方法1：在 res/drawable 中创建一个 xml 文件（如：background_repeat.xml）内容为：

    <bitmap xmlns:android="http://schemas.android.com/apk/res/android"
        android:src="@drawable/background"
        android:tileMode="repeat"
        />
    
然后再 Activity 的 xml 中添加如下内容：

    <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:orientation="vertical"
        android:layout_width="fill_parent"
        android:layout_height="fill_parent"
        android:background="@drawable/background_repeat"
        />
    
方法2：

    ImageView iv = (ImageView)mContentView.findViewById(R.id.background);
    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background);
    BitmapDrawable bd = new BitmapDrawable(getResources(), bitmap);
    bd.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
    
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        iv.setBackground(bd);
    }
    else {
        iv.setBackgroundDrawable(bd);
    }
      
##Demo
![](https://github.com/wzhnsc/TestViewPageAutoScroll/blob/master/gif/TestViewPageAutoScroll.gif)
