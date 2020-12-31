package top.gtf35.apkhelper

import androidx.compose.animation.ColorPropKey
import androidx.compose.animation.DpPropKey
import androidx.compose.animation.core.transitionDefinition
import androidx.compose.animation.core.tween
import androidx.compose.animation.transition
import androidx.compose.desktop.AppManager
import androidx.compose.desktop.Window
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import org.zeroturnaround.exec.ProcessExecutor
import org.zeroturnaround.exec.stream.LogOutputStream
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDropEvent
import java.io.File

private val path = mutableStateOf("")
private val countdownInt = mutableStateOf(0)
private val realStarted = mutableStateOf(false)
private val cmdStr = mutableStateOf("")
private val width = DpPropKey()
private val color = ColorPropKey()

/** 创建补间动画 */
val transitionDefinition = transitionDefinition<Boolean> {

    state(false) {
        this[width] = 200.dp
        this[color] = Color.LightGray
    }

    state(true) {
        this[width] = 500.dp
        this[color] = Color(34, 34, 34)
    }

    transition(fromState = false, toState = true) {
        width using tween(durationMillis = 1500)
        color using tween(durationMillis = 1500)
    }

    transition(fromState = true, toState = false) {
        width using tween(durationMillis = 1500)
        color using tween(durationMillis = 1500)
    }
}

fun main() {
    Window(
        // 无边框
        // undecorated = true,
        // 标题
        title = R.title,
        // 大小
        size = IntSize(500, 300),
        // icon
        icon = R.icAppRounded(),
        // 禁用调整大小
        resizable = false
    ) {
        createMain()
        initFileDrop()
    }
}

/**
 * 创建主窗口
 */
@Composable
private fun createMain() {
    MaterialTheme {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            createMsgText()
            createSmallWindow()
        }
    }
}

/**
 * 创建左上角的提示文本
 */
@Composable
private fun BoxScope.createMsgText() {
    val msgTextModifier = Modifier
        .align(Alignment.TopStart)
        .padding(20.dp)
    val typography = MaterialTheme.typography
    Column(msgTextModifier) {
        Spacer(Modifier.preferredHeight(10.dp))
        if (path.value.isBlank()) {
            Text(R.titleDsp, style = typography.body2)
        } else {
            if (realStarted.value) {
                Text(R.isInstalling + " " + File(path.value).name, style = typography.body2)
            } else {
                Text(R.selectPathHint + " " + File(path.value).name, style = typography.body2)
            }
        }
        Spacer(Modifier.preferredHeight(10.dp))
        if (path.value.isNotBlank()) {
            Text(R.startInstall, style = typography.body2)
        }
    }
}

/**
 * 创建右下角的小窗口
 */
@Composable
private fun BoxScope.createSmallWindow() {
    val state = transition(
        definition = transitionDefinition,
        initState = !realStarted.value,
        toState = realStarted.value
    )

    val smallBoxModifier = Modifier.align(Alignment.BottomEnd)
        .preferredHeight(200.dp)
        .preferredWidth(state[width])
        .clip(shape = RoundedCornerShape(R.shapeDp, 0, 0, 0))
        .background(color = state[color])
        .clickable { if (path.value.isNotBlank() and !realStarted.value) doInstall() }
    Box(
        smallBoxModifier,
    ) {
        val downloadIcon = imageResource(if (path.value.isBlank()) "drawable/downloadIcon.png" else "drawable/ok.png")
        val downloadIconModifier = Modifier
            .align(Alignment.Center)
            .preferredHeight(100.dp)
            .preferredWidth(100.dp)

        if (!realStarted.value) {
            Image(downloadIcon, modifier = downloadIconModifier)
        } else {
            Column {
                Text(R.cmdTitle + " * " + cmdStr.value, style = typography.body1, color = Color.White, modifier = Modifier.padding(10.dp))
                Spacer(Modifier.preferredHeight(30.dp))
                if (countdownInt.value > 0)
                    Text(countdownInt.value.toString() + R.countDownClose, style = typography.body2, color = Color.White)
            }
        }
    }
}

/**
 * 初始化拖拽的事件
 */
private fun initFileDrop() {
    val target = object : DropTarget() {
        @Synchronized
        override fun drop(evt: DropTargetDropEvent) {
            try {
                evt.acceptDrop(DnDConstants.ACTION_REFERENCE)
                val droppedFiles = evt
                    .transferable.getTransferData(
                        DataFlavor.javaFileListFlavor) as List<*>
                droppedFiles.first()?.let {
                    val pathStr = (it as File).absolutePath
                    // 只安装 apk
                    if (pathStr.toLowerCase().endsWith(".apk")) path.value = pathStr
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
    AppManager.windows.first().window.contentPane.dropTarget = target
}

/**
 * 执行安装的实际逻辑
 */
@Suppress("BlockingMethodInNonBlockingContext")
private fun doInstall() {
    // 异常处理，在 adb 返回值不是 0 的时候，会抛出异常，在这里统一处理
    val handler = CoroutineExceptionHandler { _, exception ->
        CoroutineScope(Dispatchers.IO).launch {
            println("CoroutineExceptionHandler got $exception")
            cmdStr.value += "${R.installFinishFaild}\n"

            for (i in (10 downTo 1)) {
                countdownInt.value = i
                delay(1000)
            }
            resetAll()
        }
    }

    // 标记已经开始安装，切换 UI 到安装状态
    realStarted.value = true

    // 在协程里安装，其中的安装语句均为阻塞的
    CoroutineScope(Dispatchers.IO).launch(handler) {
        // 通过在系统执行「adb version」来判断系统有没有安装 adb，没安装使用内置的
        val adbCheckResult = ServiceShellUtils.execCommand("adb version", false, true, null)
        val useInnerAdb = adbCheckResult.result < 0

        println("use inner adb：$useInnerAdb")
        cmdStr.value += if (useInnerAdb) "${R.useInnerAbd}\n" else "${R.useSystemAbd}\n"

        // 只自带了 Windows 平台的，其余平台需要自己在系统安装
        if (useInnerAdb and !ServiceShellUtils.isWindows) {
            cmdStr.value += R.otherSystemAdb
            throw Exception("need adb")
        }

        // 执行安装流程「adb install -r apk_path」
        // 如果使用内置的 adb 需要设置工作目录
        ProcessExecutor().apply {
            if (useInnerAdb) directory(File(System.getProperty("user.dir") + File.separator + "bin" ))
            val cmdMain = if (useInnerAdb and ServiceShellUtils.isWindows) {
                // 必须使用全路径（绝对路径）
                File(directory, "adbs.exe").absolutePath
            } else {
                "adb"
            }
            command(cmdMain, "install", "-r" , path.value)
            redirectOutput(object : LogOutputStream() {
                override fun processLine(line: String?) {
                    println("i:$line")
                    cmdStr.value += "$line\n"
                }
            })
            redirectError(
                object : LogOutputStream() {
                    override fun processLine(line: String?) {
                        println("e:$line")
                        cmdStr.value += "$line\n"
                    }
                }
            )
            exitValues(0)
            readOutput(true)
            execute().outputUTF8()
        }

        // 结束后倒计时复位
        cmdStr.value += "${R.installFinishSuccess}\n"

        for (i in (4 downTo 1)) {
            countdownInt.value = i
            delay(1000)
        }
        resetAll()
    }
}

/**
 * 重置所有的参数来复位 UI 到初始化状态
 */
private fun resetAll() {
    countdownInt.value = 0
    cmdStr.value = ""
    path.value = ""
    realStarted.value = false
}
