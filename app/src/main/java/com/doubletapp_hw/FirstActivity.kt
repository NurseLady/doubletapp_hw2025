package com.doubletapp_hw

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.lifecycle.ViewModel
import com.doubletapp_hw.ui.theme.Dobletapp_hwTheme


class CounterViewModel : ViewModel() {
    var counter = -1

    fun incrementCounter() {
        counter++
        Log.d("Counter", "Cчётчик увеличен")
    }

}class FirstActivity : ComponentActivity() {
    private val LifecycleTag = "FirstActivityLifecycle"
    private val CounterViewModel by viewModels<CounterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LifecycleTag, "onCreate")
        CounterViewModel.incrementCounter()
        setContent {
            Dobletapp_hwTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FirstScreen(CounterViewModel.counter)
                }
            }
        }
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
fun FirstScreen(counter: Int) {
    val context = LocalContext.current
    val intent = Intent(context, SecondActivity::class.java)
    intent.putExtra("counter", counter)

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
            context.startActivity(intent)
        }) {
            Text("Go to SecondActivity")
        }
    }
}