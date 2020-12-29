package top.gtf35.apkhelper

import java.awt.image.BufferedImage
import javax.imageio.ImageIO

object R {
    val title = "安装器"
    val titleDsp = "拖动到这里安装"
    val selectNone = "尚未选择"
    val selectPathHint = "已选："
    val startInstall = "点击👉按钮开始安装"
    val isInstalling = "正在安装"

    val shapeDp = 10

    private var icon: BufferedImage? = null
    fun icAppRounded(): BufferedImage {
        if (icon != null) {
            return icon!!
        }
        try {
            val imageRes = "drawable/icon.png"
            val img = Thread.currentThread().contextClassLoader.getResource(imageRes)
            val bitmap: BufferedImage? = ImageIO.read(img)
            if (bitmap != null) {
                icon = bitmap
                return bitmap
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
    }
}