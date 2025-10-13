package com.example.inasproject

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.lifecycleScope
import com.example.inasproject.databinding.ActivityMainBinding
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var credentialManager: CredentialManager
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        credentialManager = CredentialManager.create(this)
        auth = Firebase.auth

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupEvents()
    }

    private fun setupEvents() {
        binding.btnLogin.setOnClickListener {
            lifecycleScope.launch {
                val request = prepareRequest()
                loginByGoogle(request)
            }
        }
    }

    private fun prepareRequest(): GetCredentialRequest {
        val serverClientId = "794838543675-776q0usavm9f5f2qmsbb90smf7ndvd71.apps.googleusercontent.com"

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
            .build()

        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }

    private suspend fun loginByGoogle(request: GetCredentialRequest) {
        try {
            val result = credentialManager.getCredential(
                context = this,
                request = request
            )

            val credential = result.credential
            val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data)
            firebaseLoginCallback(googleIdToken.idToken)

        } catch (exc: NoCredentialException) {
            Toast.makeText(this, "Login gagal: ${exc.message}", Toast.LENGTH_LONG).show()
        } catch (exc: Exception) {
            Toast.makeText(this, "Login gagal: ${exc.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun firebaseLoginCallback(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login berhasil!", Toast.LENGTH_LONG).show()
                    toTodoListPage()
                } else {
                    Toast.makeText(this, "Login gagal!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun toTodoListPage() {
        val intent = Intent(this, InasActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()

        if (auth.currentUser != null) {
            toTodoListPage()
        }
    }
}