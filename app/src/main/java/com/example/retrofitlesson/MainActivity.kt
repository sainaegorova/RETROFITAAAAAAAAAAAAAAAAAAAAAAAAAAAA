package com.example.retrofitlesson

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retrofitlesson.adapter.ProductAdapter
import com.example.retrofitlesson.databinding.ActivityMainBinding
import com.example.retrofitlesson.retrofit.AuthRequest
import com.example.retrofitlesson.retrofit.MainApi
import com.example.retrofitlesson.retrofit.ProductAPI
import com.example.retrofitlesson.retrofit.User
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: ProductAdapter
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tv1.text  = "Гость"
        adapter = ProductAdapter()
        binding.rcView.layoutManager = LinearLayoutManager(this)
        binding.rcView.adapter = adapter

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://dummyjson.com").client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()
        val mainAPI = retrofit.create(MainApi::class.java)

        var user: User? = null

        binding.sv.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(text: String?): Boolean {
                CoroutineScope(Dispatchers.IO).launch {
                    val list = text?.let { mainAPI.getProductsByNameAuth(user?.accessToken ?: "", it) }
                    runOnUiThread {
                        binding.apply {
                            adapter.submitList(list?.products)
                        }
                    }
                }
                return true
            }
            override fun onQueryTextChange(text: String?): Boolean {
                return true
            }
        })
    }
}