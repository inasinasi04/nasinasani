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

        return data.documents.map {
            Todo(
                id = it.id,
                title = it.get("title").toString(),
                description = it.get("description").toString()
            )
        }
    }

    suspend fun getTodo(id: String): Todo? {
        val data = db.collection("todo")
            .document(id)
            .get()
            .await()

        if (!data.exists()) return null

        return Todo(
            id = data.id,
            title = data.get("title").toString(),
            description = data.get("descriptiobn").toString()
        )
    }

    suspend fun deleteTodo(id: String) {
        try {
            db.collection("todo")
                .document(id)
                .delete()
                .await()
        } catch (exc: Exception) {
            throw Exception("Gagal menghapus data: ${exc.message}")
        }
    }

    suspend fun updateTodo(todo: Todo) {
        try {
            val payload = hashMapOf(
                "title" to todo.title,
                "description" to todo.description
            )

            db.collection("todo")
                .document(todo.id)
                .set(payload)
                .await()
        } catch (exc: Exception) {
            throw Exception("Gagal menghapus data: ${exc.message}")
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