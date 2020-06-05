package com.example.pruebatecnica

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import android.widget.Toast


class ItemListActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_list)
        initialize()

        val btnRefresh = findViewById<Button>(R.id.itemListBtnRefresh)
        val btnDeleteAll = findViewById<Button>(R.id.itemListBtnDelete)

        val clickListener: View.OnClickListener = View.OnClickListener { view ->
            when (view.id) {
                R.id.itemListBtnRefresh -> {
                    //REFRESH
                    //When it initializes again will execute the query for retrieve all rows from the
                    //POSTS table
                    refresh()
                    Toast.makeText(
                        this,
                        this.getString(R.string.list_item_refresPressed),
                        Toast.LENGTH_LONG
                    ).show()
                }

                R.id.itemListBtnDelete -> {
                    //DELETE ALL ROWS
                    delete_all_posts()
                    Toast.makeText(
                        this,
                        this.getString(R.string.list_item_btnDeletePressed),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        listView.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, PopUpWindow::class.java)
            var post =get_data()[position]
            intent.putExtra("postId", post.postId)
            startActivity(intent)
        }


        btnRefresh.setOnClickListener(clickListener)
        btnDeleteAll.setOnClickListener(clickListener)


    }

    fun refresh() {
        listView = findViewById<ListView>(R.id.itemListView)

        var listItems = get_data()

        val adapter = PostAdapter(this, listItems)

        //val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems)
        listView.adapter = adapter
    }

    fun initialize() {
        listView = findViewById<ListView>(R.id.itemListView)

        var listItems = get_data()
        var limit = 0
        while (limit <= 10) {
            limit++
            // BE AWARE, THIS MIGHT PRODUCE A CRASH LOOP
            listItems = get_data()
        }

        val adapter = PostAdapter(this, listItems)

        //val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems)
        listView.adapter = adapter
    }

    fun get_data(): ArrayList<Post> {
        val dbHelper = DatabaseHandler(this)
        val db = dbHelper.writableDatabase

        val cursor = db.query("POSTS", null, null, null, null, null, "")
        val items = ArrayList<Post>()
        with(cursor) {
            while (moveToNext()) {
                // private val SQL_CREATE_ENTRIES = "CREATE TABLE POSTS (ID Integer PRIMARY KEY, USER_ID TEXT, TITLE TEXT, BODY TEXT, FAVORITE BOOLEAN, SEEN BOOLEAN)"
                var id = cursor.getString(cursor.getColumnIndex("ID"))
                var user_id = cursor.getString(cursor.getColumnIndex("USER_ID"))
                var title = cursor.getString(cursor.getColumnIndex("TITLE"))
                var body = cursor.getString(cursor.getColumnIndex("BODY"))
                var favorite = cursor.getString(cursor.getColumnIndex("FAVORITE")) == "1"
                var seen = cursor.getString(cursor.getColumnIndex("SEEN")) == "1"
                items.add(Post(id, user_id, title, body, favorite, seen))
            }
        }
        db.close()
        return items
    }

    fun delete_all_posts() {
        val dbHelper = DatabaseHandler(this)
        val db = dbHelper.writableDatabase

        db.execSQL("DELETE FROM POSTS")
        db.close()
    }


}
