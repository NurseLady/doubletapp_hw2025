package com.doubletapp_hw

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doubletapp_hw.ui.theme.Dobletapp_hwTheme


class SecondActivity : ComponentActivity() {
    private val LifecycleTag = "SecondActivityLifecycle"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = intent.extras
        val counter = args?.getInt("counter") ?: 0

        setContent {
            Dobletapp_hwTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SecondScreen(counter * counter)
                }
            }
        }
        Log.d(LifecycleTag, "onCreate")
    }

    override fun onStart() {
        super.onStart()
        Log.d(LifecycleTag, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(LifecycleTag, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(LifecycleTag, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(LifecycleTag, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(LifecycleTag, "onDestroy")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(LifecycleTag, "onRestart")
    }
}

@Composable
fun SecondScreen(counter: Int) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = counter.toString(),
            fontSize = 150.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            (context as? Activity)?.finish()
        }) {
            Text("Go to FirstActivity")
        }
    }
}