package com.example.pruebatecnica

import android.app.Activity
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import kotlinx.android.synthetic.main.activity_pop_up_window.*

class PopUpWindow : AppCompatActivity() {

    private var postId = ""
    private var popupText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        setContentView(R.layout.activity_pop_up_window)

        val bundle = intent.extras
        postId = bundle?.getString("postId", "0") ?: ""

        try {
            val parsedInt = postId.toInt()

            var post = get_data()[parsedInt]
            popup_window_title.text = post.title
            popup_window_text.text = post.body
            popup_window_button.text = "Post created by ".plus(post.userId)

        } catch (nfe: NumberFormatException) {
            // not a valid int
        }

        // Set the Status bar appearance for different API levels
        if (Build.VERSION.SDK_INT in 19..20) {
            setWindowFlag(this, true)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if (Build.VERSION.SDK_INT >= 21) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // If you want dark status bar, set darkStatusBar to true
                this.window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

                this.window.statusBarColor = Color.TRANSPARENT
                setWindowFlag(this, false)
            }
        }
        // Fade animation for the Popup Window
        popup_window_view_with_border.alpha = 0f
        popup_window_view_with_border.animate().alpha(1f).setDuration(500).setInterpolator(
            DecelerateInterpolator()
        ).start()

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

    private fun setWindowFlag(activity: Activity, on: Boolean) {
        val win = activity.window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        } else {
            winParams.flags =
                winParams.flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS.inv()
        }
        win.attributes = winParams
    }

}
