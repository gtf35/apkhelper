package top.gtf35.apkhelper

import java.awt.image.BufferedImage
import javax.imageio.ImageIO

object R {
    const val title = "安装器"
    const val titleDsp = "拖动到这里安装"
    const val selectPathHint = "已选："
    const val startInstall = "点击👉按钮开始安装"
    const val isInstalling = "正在安装"
    const val useInnerAbd = "使用内置 ADB"
    const val useSystemAbd = "使用系统 ADB"
    const val installFinishSuccess = "恭喜，安装完成"
    const val installFinishFaild = "安装失败"
    const val countDownClose = "s后关闭"
    const val cmdTitle = "终端"
    const val otherSystemAdb = "非 Windows 系统请自行安装 ADB 工具"

    const val shapeDp = 10

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