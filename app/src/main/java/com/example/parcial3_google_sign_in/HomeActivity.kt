package com.example.parcial3_google_sign_in

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

enum class ProviderType{
    BASIC,
    GOOGLE
}

class HomeActivity : AppCompatActivity() {
    private lateinit var txt_Email: TextView
    private lateinit var txt_Proveedor: TextView
    private lateinit var btn_Cerrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        txt_Email = findViewById(R.id.txt_Email)
        txt_Proveedor = findViewById(R.id.txt_Proveedor)
        btn_Cerrar = findViewById(R.id.btn_Cerrar)

        //Setup
        val bundle=intent.extras
        val email=bundle?.getString("email")
        val provider = bundle?.getString("provider")
        setup(email?:"",provider?:"")

        //guardado de datos
        val prefs: SharedPreferences.Editor? =getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs?.putString("email",email)
        prefs?.putString("provider", provider)
        prefs?.apply()
    }

    private fun setup(email:String, provider:String){
        title="Inicio"
        txt_Email.text = email
        txt_Proveedor.text =provider

        btn_Cerrar.setOnClickListener {
            //Borrar datos
            val prefs: SharedPreferences.Editor =getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }
    }
}