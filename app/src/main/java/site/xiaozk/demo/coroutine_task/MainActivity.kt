package site.xiaozk.demo.coroutine_task

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import site.xiaozk.demo.coroutine_task.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            viewModel.mainTask.collect {
                when (it.status) {
                    TaskStatus.Failed -> {
                        binding.taskStatus.text = it.cause?.localizedMessage
                    }
                    else -> {
                        binding.taskStatus.text = it.status.name
                    }
                }
            }
        }
        binding.startTask.setOnClickListener {
            lifecycleScope.launch {
                viewModel.startMainTask()
            }
        }
    }
}