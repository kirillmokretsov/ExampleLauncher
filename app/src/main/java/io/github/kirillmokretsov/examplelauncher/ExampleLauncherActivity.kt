package io.github.kirillmokretsov.examplelauncher

import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val TAG = "ExampleLauncherActivity"

class ExampleLauncherActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example_launcher)

        recyclerView = findViewById(R.id.app_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        setupAdapter()

    }

    private fun setupAdapter() {
        val startupIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val activities = packageManager.queryIntentActivities(startupIntent, 0)
        activities.sortWith { a, b ->
            String.CASE_INSENSITIVE_ORDER.compare(
                a.loadLabel(packageManager).toString(),
                b.loadLabel(packageManager).toString()
            )
        }

        recyclerView.adapter = ActivityAdapter(activities)

        Log.i(TAG, "Found ${activities.size}")
    }

    private class ActivityHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private val root = view
        private val titleTextView: TextView = root.findViewById(R.id.app_title)
        private val iconView: ImageView = root.findViewById(R.id.app_icon)
        private lateinit var resolveInfo: ResolveInfo

        init {
            titleTextView.setOnClickListener(this)
        }

        fun bindActivity(resolveInfo: ResolveInfo) {
            this.resolveInfo = resolveInfo
            val packageManager = root.context.packageManager
            titleTextView.text = resolveInfo.loadLabel(packageManager).toString()
            iconView.setImageDrawable(resolveInfo.loadIcon(packageManager))
        }

        override fun onClick(view: View) {
            val activityInfo = resolveInfo.activityInfo

            val intent = Intent(Intent.ACTION_MAIN).apply {
                setClassName(activityInfo.applicationInfo.packageName, activityInfo.name)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val context = view.context
            context.startActivity(intent)
        }

    }

    private class ActivityAdapter(val activities: List<ResolveInfo>) :
        RecyclerView.Adapter<ActivityHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.item_application, parent, false)
            return ActivityHolder(view)
        }

        override fun onBindViewHolder(holder: ActivityHolder, position: Int) {
            val resolveInfo = activities[position]
            holder.bindActivity(resolveInfo)
        }

        override fun getItemCount(): Int = activities.size

    }
}