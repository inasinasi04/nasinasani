package com.example.inasproject

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.inasproject.databinding.ActivityCreateTodoBinding
import com.example.inasproject.entity.Todo
import com.example.inasproject.usecase.TodoUseCase
import kotlinx.coroutines.launch

class CreateTodoActivity : AppCompatActivity() {
    private lateinit var activityBinding: ActivityCreateTodoBinding
    private lateinit var todoUseCase: TodoUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        activityBinding = ActivityCreateTodoBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

        todoUseCase = TodoUseCase()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        registerEvent()
    }

    fun registerEvent() {
        activityBinding.tombolTambah.setOnClickListener {
            saveTodotoFirestore()
        }
    }

    fun saveTodotoFirestore() {
        val title = activityBinding.title.text.toString()
        val description = activityBinding.description.text.toString()

        if (title == "" || description == "") {
            Toast.makeText(this@CreateTodoActivity, "Judul dan deskripsi tidak boleh kosong", Toast.LENGTH_LONG).show()
            return
        }

        val todo = Todo(
            id = "",
            title = title,
            description = description,
        )

        lifecycleScope.launch {
            try {
                todoUseCase.createTodo(todo)
                Toast.makeText(this@CreateTodoActivity, "Sukses menambahkan data", Toast.LENGTH_LONG).show()
                toTodoListPage()
            } catch (exc: Exception) {
                Toast.makeText(this@CreateTodoActivity, exc.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun toTodoListPage() {
        val intent = Intent(this, InasActivity::class.java)
        startActivity(intent)
        finish()
    }
}