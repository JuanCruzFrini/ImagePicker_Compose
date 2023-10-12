package com.cucu.imagepickercompose

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.cucu.imagepickercompose.ui.theme.ImagePickerComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ImagePickerComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Row {
                        PickImageFromGallery(Modifier.weight(1f))
                        PickImageFromCamera(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun PickImageFromGallery(modifier: Modifier) {

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val bitmap = remember {
        mutableStateOf<Bitmap?>(null)
    }
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
    }

    Column(
        modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        imageUri?.let { uri ->
            if (Build.VERSION.SDK_INT < 28){
                bitmap.value = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                bitmap.value = ImageDecoder.decodeBitmap(source)
            }

            bitmap.value?.let { bitmap1 ->
                Image(
                    bitmap = bitmap1.asImageBitmap(),
                    contentDescription = "",
                    modifier = Modifier.size(400.dp).padding(20.dp)
                )
            }
        }
            Spacer(modifier = Modifier.height(12.dp))
            
            Button(onClick = { launcher.launch("image/*") }) {
                Text(text = "Pick image from gallery")
            }

    }
}

@Composable
fun PickImageFromCamera(modifier: Modifier) {
    var bitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) {
      bitmap = it
    }

    Column(
        modifier
            .fillMaxHeight()
            .padding(vertical = 20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "",
                modifier = Modifier.size(400.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = { launcher.launch() }) {
            Text(text = "Pick image from camera")
        }
    }
}