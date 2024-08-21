package com.terabyte.qrscanner.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.terabyte.qrscanner.R
import com.terabyte.qrscanner.data.QRInfo
import com.terabyte.qrscanner.ui.theme.ColorPrimaryText
import com.terabyte.qrscanner.ui.theme.ColorSecondaryText
import com.terabyte.qrscanner.ui.theme.QRScannerTheme
import com.terabyte.qrscanner.util.ScanHelper
import com.terabyte.qrscanner.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MainViewModel

    private val launcherScanUsingCamera = registerForActivityResult(
        ScanContract()
    ) { result ->
        viewModel.onScannedUsingCamera(result)
    }

    private val launcherScanUsingGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        viewModel.onImagePickedFromGalleryToScan(applicationContext, it)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[MainViewModel::class]

        registerLiveDataObservers()

        setContent {
            QRScannerTheme {
                MainActivityContent()
            }
        }
    }

    @Composable
    private fun MainActivityContent() {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.main_background),
                contentDescription = "background",
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                QRInfoCard()
                LazyColumnScanHistory()
                Text(
                    text = "Scan new QR code:",
                    color = ColorPrimaryText,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CardScanNewCamera()
                    CardScanNewGallery()
                }
            }
        }

    }

    @Preview(showBackground = true)
    @Composable
    private fun QRInfoCard() {
        val qrInfo = remember {
            viewModel.currentQRInfo
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .heightIn(max = 250.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            border = BorderStroke(2.dp, Color.Blue)
        ) {
            Text(
                text = "QR information:",
                color = ColorPrimaryText,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, start = 4.dp, end = 4.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = if (qrInfo.value.info.isEmpty()) "Nothing has been scanned yet." else qrInfo.value.info,
                    fontSize = 18.sp,
                    color = ColorPrimaryText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                )
                if (qrInfo.value.info.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            viewModel.onCopyButtonClickedListener(
                                applicationContext,
                                viewModel.currentQRInfo.value.info
                            ) {
                                Toast.makeText(this@MainActivity, "Copied!", Toast.LENGTH_SHORT)
                                    .show()
                            }
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
    }

    @Composable
    private fun LazyColumnScanHistory() {
        val scanHistory = remember {
            viewModel.scanHistory
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (scanHistory.value == null) {
                //waiting for coroutine IO process
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(50.dp)
                        .height(50.dp),
                    color = Color.Blue
                )
            } else if (scanHistory.value!!.isEmpty()) {
                //no qr codes before
                Text(
                    text = "Here you will se the history of scanned QR codes.",
                    color = ColorPrimaryText,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .padding(start = 32.dp, end = 32.dp)
                )
            } else {
                Text(
                    text = "Previous scan results:",
                    fontSize = 18.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    textAlign = TextAlign.Center
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    itemsIndexed(scanHistory.value!!) { index, item ->
                        if (index % 2 == 0) {
                            ScanHistoryItemRight(qrInfo = item)
                        } else {
                            ScanHistoryItemLeft(qrInfo = item)
                        }
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(16.dp)
                        )
                    }
                }
            }

        }
    }

    @Composable
    private fun CardScanNewCamera() {
        Card(
            modifier = Modifier
                .width(150.dp)
                .height(100.dp)
                .padding(8.dp)
                .clickable {
                    launcherScanUsingCamera.launch(ScanHelper.getScanOptions())
                },
            colors = CardDefaults.cardColors(
                containerColor = Color.Blue
            )
        ) {
            Text(
                "From camera",
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color.White
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_camera),
                contentDescription = "camera",
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(bottom = 16.dp, top = 8.dp),
                tint = Color.White
            )
        }
    }

    @Composable
    private fun CardScanNewGallery() {
        Card(
            modifier = Modifier
                .width(150.dp)
                .height(100.dp)
                .padding(8.dp)
                .clickable {
                    val intent = Intent()
                    intent.type = "image/*"
                    intent.action = Intent.ACTION_GET_CONTENT
                    val intentChooser = Intent.createChooser(intent, "Choose image with QR code:")
                    launcherScanUsingGallery.launch(intentChooser)
                },
            colors = CardDefaults.cardColors(
                containerColor = Color.Blue
            ),
        ) {
            Text(
                "From gallery",
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color.White
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_gallery),
                contentDescription = "gallery",
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(bottom = 16.dp, top = 8.dp),
                tint = Color.White
            )
        }
    }

    @Composable
    fun ScanHistoryItemLeft(qrInfo: QRInfo) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            border = BorderStroke(2.dp, Color.Gray)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(0.8f)
                ) {
                    Text(
                        text = if (qrInfo.info.length < 20) {
                            qrInfo.info
                        } else {
                            qrInfo.info.substring(17) + "..."
                        },
                        maxLines = 1,
                        color = ColorPrimaryText,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Date:" + qrInfo.date,
                        color = ColorSecondaryText,
                        fontSize = 14.sp
                    )
                }
                IconButton(
                    onClick = {
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_copy),
                        contentDescription = "copy"
                    )
                }
            }
        }
    }

    @Composable
    fun ScanHistoryItemRight(qrInfo: QRInfo) {
        Row {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth(0.1f)
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                border = BorderStroke(2.dp, Color.Gray)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(0.8f)
                    ) {
                        Text(
                            text = if (qrInfo.info.length < 20) {
                                qrInfo.info
                            } else {
                                qrInfo.info.substring(17) + "..."
                            },
                            maxLines = 1,
                            color = ColorPrimaryText,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Date: " + qrInfo.date,
                            color = ColorSecondaryText,
                            fontSize = 14.sp
                        )
                    }
                    IconButton(
                        onClick = {
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_copy),
                            contentDescription = "copy"
                        )
                    }
                }
            }
        }

    }

    private fun registerLiveDataObservers() {
        viewModel.liveDataToastNothingScannedCamera.observe(this) {
            if (it) Toast.makeText(this, "Nothing was scanned.", Toast.LENGTH_SHORT).show()
        }
        viewModel.liveDataToastNothingScannedGallery.observe(this) {
            if (it) Toast.makeText(this, "Nothing was scanned.", Toast.LENGTH_SHORT).show()
        }
        viewModel.liveDataToastNothingWasChosenFromGallery.observe(this) {
            if (it) Toast.makeText(this, "No image was picked.", Toast.LENGTH_SHORT).show()
        }
    }
}





