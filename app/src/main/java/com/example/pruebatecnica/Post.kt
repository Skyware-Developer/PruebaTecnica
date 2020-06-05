package com.example.pruebatecnica

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException


class Post(
    val postId: String,
    val userId: String,
    val title: String,
    val body: String,
    var favorite: Boolean,
    val seen: Boolean
) {

    companion object {
        fun asycPosts(context: Context) {

            val queue = Volley.newRequestQueue(context)
            val url = "https://jsonplaceholder.typicode.com/posts"
            var jsonArray: JSONArray?
            val JsonArrayRequest = JsonArrayRequest(
                Request.Method.GET, url, JSONArray(),
                Response.Listener { response ->
                    writeData(context, response)
                },
                Response.ErrorListener { error ->
                    Log.e("Error", error.message.toString())
                }
            )

            queue.add(JsonArrayRequest)

        }

        fun writeData(context: Context, jsonArray: JSONArray) {
            try{
                for (i in 0 until jsonArray.length()){
                    val item = jsonArray.getJSONObject(i)
                    val id = item.getString("id")
                    val userId = item.getString("userId")
                    val title = item.getString("title")
                    val body = item.getString("body")
                    val favorite = false
                    val seen = false
                    val post = Post(id, userId, title, body, favorite, seen)
                    addPost(post,context)
                }
            }catch (e:JSONException){
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }

        fun addPost(post: Post, context: Context) {
            val dbHelper = DatabaseHandler(context)
            val db = dbHelper.writableDatabase

            // Create a new map of values, where column names are the keys
            val values = ContentValues().apply {
                put("ID", post.postId)
                put("USER_ID", post.userId)
                put("TITLE", post.title)
                put("BODY", post.body)
                put("FAVORITE", post.favorite)
                put("SEEN", post.seen)
            }
            // Insert the new row, returning the primary key value of the new row
            val newRow = db.insertWithOnConflict("POSTS",null,values,SQLiteDatabase.CONFLICT_REPLACE)
            //val newRowId = db?.insert("POSTS", null, values)

        }


    }


}