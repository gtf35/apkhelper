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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDropEvent
import java.io.File

private val path = mutableStateOf("")
private val realStarted = mutableStateOf(false)
private val cmdStr = mutableStateOf("")
private val width = DpPropKey()
private val color = ColorPropKey()

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
        icon = R.icAppRounded()
    ) {
        createMain()
        initFileDrop()
    }
}

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

@Composable
private fun BoxScope.createSmallWindow() {
    //5
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
                Spacer(Modifier.preferredHeight(20.dp))
                Text(cmdStr.value, style = typography.body2, color = Color.White)
            }
        }
    }
}

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
                    path.value = (it as File).absolutePath
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
    AppManager.windows.first().window.contentPane.dropTarget = target
}

private fun doInstall() {
    realStarted.value = true
    CoroutineScope(Dispatchers.IO).launch {

        delay(1500)

        cmdStr.value = ""
        path.value = ""
        realStarted.value = false
    }
}
