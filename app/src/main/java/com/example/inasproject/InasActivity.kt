package com.example.inasproject

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inasproject.adapter.TodoAdapter
import com.example.inasproject.databinding.ActivityInasBinding
import com.example.inasproject.usecase.TodoUseCase
import kotlinx.coroutines.launch

class InasActivity : AppCompatActivity() {
    private lateinit var activityBinding: ActivityInasBinding
    private lateinit var todoAdapter: TodoAdapter
    private lateinit var todoUseCase: TodoUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        activityBinding = ActivityInasBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        todoUseCase = TodoUseCase()

        setupRecyclerView()
        initializedData()
    }

    fun setupRecyclerView() {
        todoAdapter = TodoAdapter(mutableListOf())
        activityBinding.container.adapter = todoAdapter
        activityBinding.container.layoutManager = LinearLayoutManager(this)

    }

    fun initializedData() {
        activityBinding.container.visibility = View.GONE
        activityBinding.loading.visibility = View.VISIBLE

        lifecycleScope.launch {
            val data = todoUseCase.getTodo()
            activityBinding.container.visibility = View.VISIBLE
            activityBinding.loading.visibility = View.GONE
            todoAdapter.updateDataSet(data)
        }
    }

}