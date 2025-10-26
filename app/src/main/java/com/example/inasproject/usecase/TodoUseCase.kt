package com.example.inasproject.usecase

import com.example.inasproject.entity.Todo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class TodoUseCase {
    private val db: FirebaseFirestore = Firebase.firestore

    suspend fun getTodo(): List<Todo> {
        val data = db.collection("todo")
            .get()
            .await()

        if (data.isEmpty) {
            throw Exception("Data di server kosong")
        }

        return data.documents.map {
            Todo(
                id = it.id,
                title = it.get("title").toString(),
                description = it.get("description").toString()
            )
        }
    }

    suspend fun createTodo(todo: Todo): Todo {
        try {
            val payload = hashMapOf(
                "title" to todo.title,
                "description" to todo.description
            )

            val data = db.collection("todo")
                .add(payload)
                .await()

            return todo.copy(id = data.id)
        } catch (exc: Exception) {
            throw Exception("Gagal menyimpan data ke firestore")
        }
    }
}