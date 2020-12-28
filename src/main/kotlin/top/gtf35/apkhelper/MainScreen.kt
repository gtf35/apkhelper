package top.gtf35.apkhelper

import androidx.compose.desktop.Window
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

fun main() {
    Window(
        // 无边框
        // undecorated = true,
        // 标题
        title = R.title,
        // 大小
        size = IntSize(500, 300)
    ) {
        createMain()
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
        Text(R.title, style = typography.body1)
        Spacer(Modifier.preferredHeight(10.dp))
        Text(R.titleDsp, style = typography.body2)
    }
}

@Composable
private fun BoxScope.createSmallWindow() {
    val smallBoxModifier = Modifier.align(Alignment.BottomEnd)
        .preferredHeight(200.dp)
        .preferredWidth(300.dp)
        .clip(shape = RoundedCornerShape(R.shapeDp, 0, 0, 0))
        .background(color = Color.LightGray)
    Box(
        smallBoxModifier
    ) {
        val downloadIcon = imageResource("drawable/downloadIcon.png")
        val downloadIconModifier = Modifier
            .align(Alignment.Center)
            .preferredHeight(100.dp)
            .preferredWidth(100.dp)

        Image(downloadIcon, modifier = downloadIconModifier)
    }
}