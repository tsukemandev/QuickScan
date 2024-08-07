package io.github.tsukemendog.qrscanapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import io.github.tsukemendog.qrscanapp.ui.theme.QRScanAppTheme


class MainActivity : ComponentActivity() {

    private var scanResult = mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        initiateQRScan()
        setContent {
            QRScanAppTheme {
                QRScannerScreen(scanResult.value)
            }
        }

    }

    // Register the launcher and result handler
    private val barcodeLauncher = registerForActivityResult<ScanOptions, ScanIntentResult>(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents != null) {
            scanResult.value = result.contents
        }
    }


    fun initiateQRScan() {
        barcodeLauncher.launch(ScanOptions())
    }

}

@Composable
fun QRScannerScreen(text: String) {
    val context = LocalContext.current
    val qrCodeBitmap = generateQRCode(text)

    val activity = context as? MainActivity
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00B8F0)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.icon1), // Replace with your location icon
            contentDescription = "Location Icon",
            tint = Color.Yellow,
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "QR SCANER",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(16.dp))


        Image(
            painter = qrCodeBitmap?.asImageBitmap()?.let { BitmapPainter(it) } ?: painterResource(id = R.drawable.qrimage),
            contentDescription = "QR Code",
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Result",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

/*        Text(
            text = "Lorem ipsum dolor sit amet, consecte\nadipiscing elit, sed diam nonummy nibh\n" +
                    "eu tincidunt ut laoreet dolore magna",
            color = Color.White,
            textAlign = TextAlign.Center,
            fontSize = 14.sp
        )*/


        ClickableText(
            text = AnnotatedString(text),
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(text))
                context.startActivity(intent)
            },
            style = TextStyle(
                color = Color.White,
                fontSize = 26.sp,
                fontFamily = FontFamily.Default,
                textDecoration = TextDecoration.Underline
            ),

        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp).clickable {
            activity?.initiateQRScan()
        }) {
            Icon(
                tint = Color.White,
                imageVector = Icons.Default.Refresh,
                contentDescription = "Restart Icon",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(text = "restart", fontSize = 28.sp, color = Color.White, textDecoration = TextDecoration.Underline,)
        }


    }
}


fun generateQRCode(text: String): Bitmap? {
    val barcodeEncoder = BarcodeEncoder()
    return try {
        val bitMatrix: BitMatrix = barcodeEncoder.encode(text, BarcodeFormat.QR_CODE, 400, 400)
        barcodeEncoder.createBitmap(bitMatrix)
    } catch (e: WriterException) {
        e.printStackTrace()
        null
    }
}
