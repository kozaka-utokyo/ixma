package ixma

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon.Companion.Text
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.singleWindowApplication

fun main() = singleWindowApplication {
    Image(
        painter = painterResource("duck.jpg"),
        contentDescription = "Sample",
        modifier = Modifier.fillMaxSize()
    )
}

