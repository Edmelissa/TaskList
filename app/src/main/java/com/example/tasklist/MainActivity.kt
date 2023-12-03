package com.example.tasklist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tasklist.databinding.ActivityMainBinding
import java.util.Collections

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var taskDataSource: TaskDataSource
    private lateinit var adapter: TasksAdapter

    private var taskName = ""
    private var taskImage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //создание объекта ресурсов
        taskDataSource = TaskDataSource()

        adapter = TasksAdapter(
            object : TaskActionListener{
                override fun onTaskDelete(task: Task) {
                    taskDataSource.deleteTask(task)
                }
            }
        )

        val layoutManager = LinearLayoutManager(this)

        //получаем начальный список задач
        adapter.tasks = taskDataSource.getTaskList()

        //прицепляем адаптер и менеджер макета
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter

        //создание декоратора для Drag&Drop
        val dividerItemDecoration = DividerItemDecoration(this , layoutManager.orientation)
        binding.recyclerView.addItemDecoration(dividerItemDecoration)

        val callback = DragManageAdapter(adapter, this,
            ItemTouchHelper.UP.or(ItemTouchHelper.DOWN), ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT))
        val helper = ItemTouchHelper(callback)
        helper.attachToRecyclerView(binding.recyclerView)

        //накидываем слушатель на ресурсы
        taskDataSource.addListener(taskListener)

        //обработка нажатия на кнопку
        binding.buttonAddTask.setOnClickListener {
            val intent = Intent(this@MainActivity, CreateTaskActivity::class.java)
            launcher.launch(intent)
        }
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult -> if (result.resultCode == RESULT_OK) {
        taskName = result.data?.getStringExtra("create_task_name").toString()
        taskImage = result.data?.getStringExtra("create_task_image").toString()

        taskDataSource.addTask(
            Task(
                id = (taskDataSource.getTaskList().size + 1).toLong(),
                name = taskName,
                imageUrl = taskImage
            )
        )
    }
    }

    private val taskListener: TaskListener = {
        adapter.tasks = it
    }
}