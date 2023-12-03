package com.example.tasklist

import android.util.Log
import java.util.Collections

typealias TaskListener = (tasks: MutableList<Task>) -> Unit

class TaskDataSource {
    private val taskList = emptyList<Task>().toMutableList()

    private val listeners = mutableSetOf<TaskListener>()

    fun getTaskList() : MutableList<Task> = taskList

    fun deleteTask(task : Task){
        val indexDeleteTask = taskList.indexOfFirst{ it.id == task.id}

        if(indexDeleteTask != -1) {
            taskList.removeAt(indexDeleteTask)
            notifyChanges()
        }
    }

    fun swapTasks(fromPosition: Int, toPosition: Int){
        Collections.swap(taskList, fromPosition, toPosition)
        notifyChanges()
    }

    fun addTask(task : Task) {
        taskList.add(task)
        notifyChanges()
    }

    fun addListener(listener: TaskListener){
        listeners.add(listener)
        listener.invoke(taskList)
    }

    private fun notifyChanges(){
        listeners.forEach{ it.invoke(taskList)}
    }
}
