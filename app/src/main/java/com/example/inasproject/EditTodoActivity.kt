package com.example.inasproject

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.inasproject.databinding.ActivityEditTodoBinding
import com.example.inasproject.entity.Todo
import com.example.inasproject.usecase.TodoUseCase
import kotlinx.coroutines.launch

class EditTodoActivity : AppCompatActivity() {
    private lateinit var activityBinding: ActivityEditTodoBinding
    private lateinit var todoItemId: String
    private lateinit var todoUseCase: TodoUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        activityBinding = ActivityEditTodoBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        todoItemId = intent.getStringExtra("todo_item_id").toString()
        todoUseCase = TodoUseCase()
        registerEvent()
    }

    override fun onStart() {
        super.onStart()
        loadData()
    }

    fun registerEvent() {
        activityBinding.tombolEdit.setOnClickListener {
            lifecycleScope.launch {
                val title = activityBinding.title.text.toString()
                val description = activityBinding.description.text.toString()
                val payload = Todo(
                    id = todoItemId,
                    title = title,
                    description = description,
                )

                try {
                    todoUseCase.updateTodo(payload)
                    displayMessage("Berhasil memperbarui data")
                    back()
                } catch (exc:Exception) {
                    displayMessage("Gagal memperbarui data task: ${exc.message}")
                }
            }
        }
    }

    fun loadData() {
        lifecycleScope.launch {
            val data = todoUseCase.getTodo(todoItemId)
            if (data == null) {
                displayMessage("Data task yang akan diedit tidak tersedia di server")
                back()
            }

            activityBinding.title.setText(data?.title)
            activityBinding.description.setText(data?.description)
        }
    }

    fun back() {
        val intent = Intent(this, InasActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun displayMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}