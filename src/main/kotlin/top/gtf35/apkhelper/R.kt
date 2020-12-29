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
    val useInnerAbd = "使用内置 ADB"
    val useSystemAbd = "使用系统 ADB"
    val installFinishSuccess = "恭喜，安装完成"
    val installFinishFaild = "安装失败"
    val countDownClose = "s后关闭"
    val cmdTitle = "终端"
    val otherSystemAdb = "非 Windows 系统请自行安装 ADB 工具"

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