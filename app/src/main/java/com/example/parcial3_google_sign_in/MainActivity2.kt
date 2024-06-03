package com.example.parcial3_google_sign_in

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity2 : AppCompatActivity() {

    private val GOOGLE_SIGN_IN = 100

    private lateinit var btn_Iniciar: Button
    private lateinit var btn_Acceder: Button
    private lateinit var btn_Google:Button
    private lateinit var et_Email: EditText
    private lateinit var et_Contrasena: EditText
    private lateinit var ly_Inicio: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        btn_Iniciar = findViewById(R.id.btn_Iniciar)
        btn_Acceder = findViewById(R.id.btnAcceder)
        btn_Google=findViewById(R.id.btn_Google)
        et_Email = findViewById(R.id.et_Email)
        et_Contrasena = findViewById(R.id.et_Contrasena)
        ly_Inicio = findViewById(R.id.ly_Inicio)

        //setup
        setup()
        session()
    }

    override fun onStart() {
        super.onStart()
        ly_Inicio.visibility = View.VISIBLE

    }

    private fun session(){
        val pref=getSharedPreferences(getString(R.string.prefs_file),Context.MODE_PRIVATE)
        val email=pref.getString("email",null)
        val provider=pref.getString("provider",null)

        if (email != null && provider != null){
            ly_Inicio.visibility = View.INVISIBLE
            showHome(email, ProviderType.valueOf(provider))
        }
    }

    private fun setup(){
        title="Inicio de sesion"

        btn_Iniciar.setOnClickListener {
            if (et_Email.text.isNotEmpty() && et_Contrasena.text.isNotEmpty()){
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(et_Email.text.toString(),et_Contrasena.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful){
                        showHome(it.result?.user?.email ?:"", ProviderType.BASIC)
                    }else{
                        showAlert()
                    }
                }
            }

        }

        btn_Acceder.setOnClickListener {
            if (et_Email.text.isNotEmpty() && et_Contrasena.text.isNotEmpty()){
                FirebaseAuth.getInstance().signInWithEmailAndPassword(et_Email.text.toString(),et_Contrasena.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful){
                        showHome(it.result?.user?.email ?:"", ProviderType.BASIC)
                    }else{
                        showAlert()
                    }
                }
            }
        }

        btn_Google.setOnClickListener {
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

            val googleClient=GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)

        }
    }

    private fun showAlert(){
        val buider =AlertDialog.Builder(this)
        buider.setTitle("Error")
        buider.setMessage("Se a producido un error autenticando el usuario")
        buider.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = buider.create()
        dialog.show()
    }

    private fun showHome(email: String, provider: ProviderType){
        val homeIntent=Intent(this, HomeActivity::class.java).apply {
            putExtra("email",email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==GOOGLE_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account=task.getResult(ApiException::class.java)

                if (account != null) {
                    val credential=GoogleAuthProvider.getCredential(account.idToken,null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful){
                            showHome(account.email?:"",ProviderType.GOOGLE)
                        }else{
                            showAlert()
                        }
                    }
                }
            }catch (e: ApiException){
                showAlert()
            }

        }
    }

}