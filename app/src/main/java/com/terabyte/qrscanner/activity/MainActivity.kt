package com.terabyte.qrscanner.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.terabyte.qrscanner.R
import com.terabyte.qrscanner.ui.theme.QRScannerTheme
import com.terabyte.qrscanner.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MainViewModel

    private val launcherScanUsingCamera = registerForActivityResult(
        ScanContract()
    ) { result ->
        viewModel.onScannedUsingCamera(result)
    }

    private val launcherScanUsingGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode== RESULT_OK) {
            if(it.data==null || it.data?.data==null) {
                toastNothingWasChosen()
            }
            else {
                viewModel.onImagePickedFromGalleryToScan(applicationContext, it.data!!.data!!)
            }
        }
        else {
            toastNothingWasChosen()
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QRScannerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { paddingVals ->
                    MainActivityContent(this, paddingVals)
                }
            }
        }
    }

    @Composable
    private fun MainActivityContent(activity: MainActivity, paddingVals: PaddingValues, viewModel: MainViewModel = viewModel()) {
        this.viewModel = viewModel

        viewModel.liveDataToastNothingScannedCamera.observe(activity) {
            if(it) Toast.makeText(activity, "Nothing was scanned.", Toast.LENGTH_SHORT).show()
        }
        viewModel.liveDataToastNothingScannedGallery.observe(activity) {
            if(it) Toast.makeText(activity, "Nothing was scanned.", Toast.LENGTH_SHORT).show()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingVals.calculateTopPadding(),
                    bottom = paddingVals.calculateBottomPadding()
                )
        )  {
            QRInfoCard()
            LazyColumnScanHistory()
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CardScanNewCamera(
                    viewModel = viewModel,
                    launcherFromCamera = launcherScanUsingCamera,
                )
                CardScanNewGallery(
                    viewModel = viewModel,
                    launcherFromGallery = launcherScanUsingGallery
                )
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    private fun QRInfoCard() {
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
                    .padding(top = 8.dp, start = 4.dp, end = 4.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = if(viewModel.currentQRInfo.value.info.isEmpty()) "Nothing has been scanned yet." else viewModel.currentQRInfo.value.info,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .weight(1f)
                )
                IconButton(
                    onClick = {
                        if(viewModel.currentQRInfo.value.info.isNotEmpty()) {
                            viewModel.onCopyButtonClickedListener(applicationContext, viewModel.currentQRInfo.value.info) {
                                Toast.makeText(this@MainActivity, "Copied!", Toast.LENGTH_SHORT).show()
                            }
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

    @Preview(showBackground = true)
    @Composable
    private fun LazyColumnScanHistory() {
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

    @Composable
    private fun CardScanNewCamera(viewModel: MainViewModel, launcherFromCamera: ActivityResultLauncher<ScanOptions>) {
        Card(
            modifier = Modifier
                .width(120.dp)
                .height(120.dp)
                .padding(8.dp)
                .clickable {
                    launcherFromCamera.launch(viewModel.getScanOptions())
                }
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

    @Composable
    private fun CardScanNewGallery(viewModel: MainViewModel, launcherFromGallery: ActivityResultLauncher<Intent>) {
        Card(
            modifier = Modifier
                .width(120.dp)
                .height(120.dp)
                .padding(8.dp)
                .clickable {
                    val intent = Intent()
                    intent.type = "image/*"
                    intent.action = Intent.ACTION_GET_CONTENT
                    val intentChooser = Intent.createChooser(intent, "Choose image with QR code:")
                    launcherScanUsingGallery.launch(intentChooser)
                }
        ) {
            Text(
                "From gallery",
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_gallery),
                contentDescription = "gallery",
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(bottom = 16.dp, top = 8.dp)
            )
        }
    }

    private fun toastNothingWasChosen() {
        Toast.makeText(this, "No image was picked.", Toast.LENGTH_SHORT).show()
    }
}





