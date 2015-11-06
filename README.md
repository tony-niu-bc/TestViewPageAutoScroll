# TestViewPageAutoScroll
基于 ViewPage 实现的轮播图

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
