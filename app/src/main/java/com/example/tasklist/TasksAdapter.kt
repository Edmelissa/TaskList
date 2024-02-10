package com.example.tasklist

import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tasklist.databinding.TaskItemBinding

class TasksAdapter(private val actionListener: TaskActionListener) :
    RecyclerView.Adapter<TasksAdapter.TasksViewHolder>(),
    View.OnClickListener
{
    var tasks: MutableList<Task> = emptyList<Task>().toMutableList()
        set(newTask){
            field = newTask
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : TasksViewHolder{
        val inflater = LayoutInflater.from(parent.context)
        val binding = TaskItemBinding.inflate(inflater, parent, false)

        binding.root.setOnClickListener(this)
        binding.imageViewMenuImage.setOnClickListener(this)

        return TasksViewHolder(binding)
    }

    override fun getItemCount(): Int = tasks.size

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int){
        val task = tasks[position]
        val context = holder.itemView.context

        with(holder.binding){
            holder.itemView.tag = task
            imageViewMenuImage.tag = task

            textViewTaskName.text = task.name
            Glide.with(context)
                .load(task.imageUrl)
                .circleCrop()
                .placeholder(R.drawable.ic_error_image_task)
                .error(R.drawable.ic_error_image_task)
                .into(imageViewTaskImage)
        }
    }

    class TasksViewHolder(val binding: TaskItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onClick(v: View) {
        when (v.id){
            R.id.imageViewMenuImage -> showPopupMenu(v)
        }
    }

    private fun showPopupMenu(view: View){
        val popupMenu = PopupMenu(view.context, view)
        val task = view.tag as Task
        popupMenu.menu.add(0, ID_REMOVE, Menu.NONE, "Удалить")
        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                ID_REMOVE -> {
                    actionListener.onTaskDelete(task)
                }
            }
            return@setOnMenuItemClickListener true
        }
        popupMenu.show()
    }

    fun moveItems(fromPosition: Int, toPosition: Int) {
        actionListener.onTaskSwap(fromPosition, toPosition)
    }

    companion object{
        private const val ID_REMOVE = 1
    }
}

interface TaskActionListener{
    fun onTaskDelete(task: Task)

    fun onTaskSwap(currentPosition : Int, targetPosition: Int)
}