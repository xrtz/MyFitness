package com.example.myfitness.presentation.activity

import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.myfitness.MyFitnessApp
import com.example.myfitness.data.remote.TokenProvider
import com.example.myfitness.di.ViewModelFactory
import com.example.myfitness.domain.repository.FoodRepository
import com.example.myfitness.domain.usecase.SyncPendingDaysUseCase
import com.example.myfitness.presentation.navigation.Navigation
import com.example.myfitness.ui.theme.MyFitnessTheme
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var foodRepository: FoodRepository

    private val syncPendingUseCase by lazy { SyncPendingDaysUseCase(foodRepository) }
    private lateinit var connectivityManager: ConnectivityManager

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            lifecycleScope.launch {
                try { syncPendingUseCase.execute() } catch (_: Exception) { }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as MyFitnessApp).appComponent.inject(this)
        super.onCreate(savedInstanceState)

        connectivityManager = getSystemService(ConnectivityManager::class.java)

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

    override fun onResume() {
        super.onResume()
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, networkCallback)
    }

    override fun onPause() {
        super.onPause()
        try { connectivityManager.unregisterNetworkCallback(networkCallback) } catch (_: Exception) { }
    }
}
