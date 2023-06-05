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
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class Item(val name: String, var isChecked: Boolean)

class ShowListActivity : AppCompatActivity() {

    private lateinit var pseudo: String
    private lateinit var listes: ArrayList<Liste>
    private lateinit var items: ArrayList<Item>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter
    private lateinit var selectedListe: Liste

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_list)

        //ActionBar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "ShowListActivity"

        //Chargement des listes associés au pseudo
        pseudo = intent.getStringExtra("pseudo") ?: ""
        listes = loadListesUtilisateur(pseudo)

        //Récupération de l'indice de la liste et chargement de la liste et des items
        val selectedListeIndex = intent.getIntExtra("selectedListeIndex", -1)
        selectedListe = listes[selectedListeIndex]
        items = selectedListe.items

        //Affichage du recyclerview
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ItemAdapter(items)
        recyclerView.adapter = adapter

        //Initialisation des composants visuels de création d'item
        val addButton = findViewById<Button>(R.id.btnItemOk)
        val newItemEditText = findViewById<EditText>(R.id.newItem)

        //Ajout d'un item quand on clique sur "ok"
        addButton.setOnClickListener {
            val itemName = newItemEditText.text.toString().trim()
            if (itemName.isNotEmpty()) {
                val newItem = Item(itemName, false)
                items.add(newItem)
                newItemEditText.text.clear()
                adapter.notifyDataSetChanged()
                saveListesUtilisateur()
            }
        }
    }

    //ActionBar : chargement
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_bar, menu)
        return true
    }

    //ActionBar : action lorsqu'on sélectionne les items
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when (item?.itemId) {
            R.id.configuration -> {
                Toast.makeText(this, "Préférences", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return true
    }

    //Fonction de chargement des listes
    private fun loadListesUtilisateur(pseudo: String): ArrayList<Liste> {
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
}


class ItemAdapter(private val items: List<Item>) :
    RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)}

    override fun getItemCount(): Int {
        return items.size
    }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val itemNameTextView: TextView = itemView.findViewById(R.id.itemNameTextView)
            private val itemCheckBox: CheckBox = itemView.findViewById(R.id.itemCheckBox)

            fun bind(item: Item) {
                itemNameTextView.text = item.name
                itemCheckBox.isChecked = item.isChecked

                itemCheckBox.setOnCheckedChangeListener { _, isChecked ->
                    item.isChecked = isChecked
                }
            }
        }
}
