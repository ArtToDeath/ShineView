# ShineView-仿ios11亮度调节控件
![image](https://github.com/ArtToDeath/ShineView/blob/master/1535526792965.gif)
 - 初始状态
 - 按下（放大效果）
 - 拖动（图标缩放，进度填充0~100）
 - 松开（缩小回弹至初始状态）

使用图像混合模式的DSC_OUT模式，Dsc是深颜色部分，Src为浅色部分，不相交区域绘制Dsc。

缩放效果，重写onTouchEvent，记录按下状态，改变绘制矩形大小。

进度填充改变的是Src的绘制高度。

提供填充值变化接口监听。

回弹效果有点生硬
