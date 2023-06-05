package com.example.tea1

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


data class Liste(val nom: String, val items: ArrayList<Item>)

class ChoixListActivity : AppCompatActivity() {

    private lateinit var newListName: String
    private lateinit var pseudo: String
    private lateinit var listes: ArrayList<Liste>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ListeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choix_liste)

        //ActionBar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "ChoixListActivity"


        pseudo = intent.getStringExtra("pseudo") ?: "" //Dernier pseudo sélectionné
        listes = loadListesUtilisateur()

        //Affichage du recyclerview
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ListeAdapter(listes) { selectedListeIndex ->
            showListActivity(selectedListeIndex) //Renvoie sur l'activité "ShowListActivity"
        }
        recyclerView.adapter = adapter

        //Initialisation des composants visuels de création de liste
        val addButton = findViewById<Button>(R.id.btnListOk)
        val newEditText = findViewById<EditText>(R.id.newList)

        //Ajout d'une liste quand on clique sur "ok"
        addButton.setOnClickListener {
            newListName = newEditText.text.toString().trim()
            val newListe = Liste(newListName, ArrayList())
            listes.add(newListe)
            adapter.notifyDataSetChanged()  //Mise à jour de l'adapter
            saveListesUtilisateur()  //Sauvegarde des listes
        }
    }

    //Fonction de chargement des listes
    private fun loadListesUtilisateur(): ArrayList<Liste> {
        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val listesJson = sharedPreferences.getString("listesUtilisateur_$pseudo", null)
        return if (listesJson != null) {
            val typeToken = object : TypeToken<ArrayList<Liste>>() {}.type
            Gson().fromJson(listesJson, typeToken)
        } else {
            ArrayList()
        }
    }

    //Fonction de sauvegarde des listes
    private fun saveListesUtilisateur() {
        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val listesJson = Gson().toJson(listes)
        editor.putString("listesUtilisateur_$pseudo", listesJson)
        editor.apply()
    }

    //ActionBar : chargement
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_bar, menu)
        return true
    }

    //ActionBar : action
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when (item?.itemId) {
            R.id.configuration -> { Toast.makeText(this, "Préférences", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return true
    }

    //Fonction qui renvoie vers l'activité "ShowListActivity" si on sélectionne une liste, passe l'indice de la liste en paramètre
    private fun showListActivity(selectedListeIndex: Int) {
        val intent = Intent(this, ShowListActivity::class.java)
        intent.putExtra("selectedListeIndex", selectedListeIndex.toString())
        startActivity(intent)
    }
}

//Adapter
class ListeAdapter(private val listes: List<Liste>, private val onItemClick: (Int) -> Unit) :
    RecyclerView.Adapter<ListeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_liste, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val liste = listes[position]
        holder.bind(liste)
        holder.itemView.setOnClickListener { onItemClick(position) }
    }

    override fun getItemCount(): Int {
        return listes.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(liste: Liste) {
            itemView.findViewById<TextView>(R.id.nomListeTextView).text = liste.nom
        }
    }
}
