package com.lecezar.rezygen

import AiChatApp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainActivity : ComponentActivity() {
    val viewModel = viewModels<TestViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TestViewModel() as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AiChatApp()
        }
    }
}

class TestViewModel : ViewModel() {
    override fun onCleared() {
        println("Main ViewModel cleared")
        super.onCleared()
    }
}
