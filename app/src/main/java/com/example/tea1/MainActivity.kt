package com.example.tea1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText



class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        //Initialisation des variables
        val pseudoSaisie = findViewById<EditText>(R.id.pseudoSaisie)
        val btnOk = findViewById<Button>(R.id.okBtn)

        //Bouton OK
        btnOk.setOnClickListener{
            val pseudo = pseudoSaisie.text.toString().trim()
            val sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("currentPseudo", pseudo)
            editor.apply()

            val intent = Intent(this, ChoixListActivity::class.java)
            intent.putExtra("pseudo", pseudo)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.configuration -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

