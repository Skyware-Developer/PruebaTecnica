package com.example.pruebatecnica

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi

class PostAdapter(private val context: Context, private val dataSource: ArrayList<Post>) :
    BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    companion object {
        private val LABEL_COLORS = hashMapOf(
            "Seen" to R.color.colorSeen,
            "Unseen" to R.color.colorUnSeen
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        // 1
        if (convertView == null) {

            // 2
            view = inflater.inflate(R.layout.list_item_post, parent, false)

            // 3
            holder = ViewHolder()
            holder.thumbnailImageView = view.findViewById(R.id.post_list_thumbnail) as ImageView
            holder.titleTextView = view.findViewById(R.id.post_list_number) as TextView
            holder.subtitleTextView = view.findViewById(R.id.post_list_title) as TextView
            holder.detailTextView = view.findViewById(R.id.post_list_seen) as TextView

            // 4
            view.tag = holder
        } else {
            // 5
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        // 6
        val titleTextView = holder.titleTextView
        val subtitleTextView = holder.subtitleTextView
        val detailTextView = holder.detailTextView
        val thumbnailImageView = holder.thumbnailImageView

        val post = getItem(position) as Post

        titleTextView.text = context.getString(R.string.list_item_postN).plus(post.postId)
        subtitleTextView.text = post.title

        thumbnailImageView.setOnClickListener(View.OnClickListener {
            addRemove_favorite(post.postId, post.favorite)
            if(post.favorite){
                thumbnailImageView.setImageResource(R.drawable.not_favorite)
                post.favorite = !post.favorite
            }
            else {
                thumbnailImageView.setImageResource(R.drawable.favorite)
                post.favorite = !post.favorite
            }

        })

        if (post.favorite) thumbnailImageView.setImageResource(R.drawable.favorite)
        else thumbnailImageView.setImageResource(R.drawable.not_favorite)

        if (post.seen){
            detailTextView.text = context.getString(R.string.list_item_postSeen)
            detailTextView.setTextColor(context.getColor(R.color.colorSeen))
        }
        else {
            detailTextView.text = context.getString(R.string.list_item_postUnseen)
            detailTextView.setTextColor(context.getColor(R.color.colorUnSeen))
        }



        return view
    }


    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return dataSource.size
    }

    fun addRemove_favorite(id :String,status: Boolean){
        val dbHelper = DatabaseHandler(context)
        val db = dbHelper.writableDatabase

        var cv = ContentValues()
        cv.put("FAVORITE", !status)

        db.update("POSTS", cv, "id=".plus(id), null)

        db.close()
    }

    private class ViewHolder {
        lateinit var titleTextView: TextView
        lateinit var subtitleTextView: TextView
        lateinit var detailTextView: TextView
        lateinit var thumbnailImageView: ImageView
    }



}