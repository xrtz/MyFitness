package com.example.myfitness.presentation.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.myfitness.MyFitnessApp
import com.example.myfitness.data.remote.TokenProvider
import com.example.myfitness.di.ViewModelFactory
import com.example.myfitness.presentation.navigation.Navigation
import com.example.myfitness.ui.theme.MyFitnessTheme
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as MyFitnessApp).appComponent.inject(this)
        super.onCreate(savedInstanceState)

        if (TokenProvider.token == null) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return
        }

        enableEdgeToEdge()
        setContent {
            MyFitnessTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Navigation(
                        modifier         = Modifier.padding(innerPadding),
                        viewModelFactory = viewModelFactory,
                        onLogout         = {
                            TokenProvider.clear()
                            startActivity(Intent(this@MainActivity, AuthActivity::class.java))
                            finish()
                        }
                    )
                }
            }
        }
    }
}
