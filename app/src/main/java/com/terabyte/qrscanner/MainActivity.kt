package com.terabyte.qrscanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.terabyte.qrscanner.ui.theme.QRScannerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QRScannerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { paddingVals ->
                    MainActivityContent(paddingVals)
                }
            }
        }
    }
}

@Composable
fun MainActivityContent(paddingVals: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = paddingVals.calculateTopPadding(),
                bottom = paddingVals.calculateBottomPadding())
    ) {
        QRInfoCard()
        LazyColumnScanHistory()
        CardsScanNew()
    }
}

@Preview(showBackground = true)
@Composable
fun QRInfoCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .heightIn(max = 250.dp)
    ) {
        Text(
            text = "QR information:",
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = "Some QR infojjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj",
                fontSize = 18.sp,
                modifier = Modifier
                    .weight(1f)
            )
            IconButton(
                onClick = {

                },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_copy),
                    contentDescription = "copy",
                )
            }
        }

    }
}


@Preview(showBackground = true)
@Composable
fun LazyColumnScanHistory() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f)
    ) {
        Text(
            text = "Previous scan results:",
            fontSize = 18.sp,
            modifier = Modifier
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {

        }
    }
}

@Preview(showBackground = true)
@Composable
fun CardsScanNew() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        CardScanNew()
        CardScanNew()
    }
}

@Preview(showBackground = true)
@Composable
fun CardScanNew() {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(120.dp)
            .padding(8.dp)
    ) {
        Text(
            "From camera",
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_camera),
            contentDescription = "camera",
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(bottom = 16.dp, top = 8.dp)
        )
    }
}
