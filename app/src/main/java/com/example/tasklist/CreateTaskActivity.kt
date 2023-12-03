package com.example.tasklist

import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.GradientDrawable.Orientation
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.tasklist.databinding.ActivityNewTaskBinding

private lateinit var taskDataSource: TaskDataSource
private lateinit var taskName : String
private lateinit var taskImage : String

class CreateTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        changeSizeButtonAddImage()

        taskDataSource = TaskDataSource()

        with(binding) {
            buttonCreateAddTask.setOnClickListener {
                clickOnButtonCreateAddTask()
            }

            buttonAddImage.setOnClickListener {
                launcherGallery.launch("image/*")
            }
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.GetContent(),
        ActivityResultCallback {
            binding.buttonAddImage.background = null

            Log.d("", it.toString())

            Glide.with(binding.buttonAddImage.context)
                .load(it)
                .circleCrop()
                .placeholder(R.drawable.ic_error_image_task)
                .error(R.drawable.ic_error_image_task)
                .into(binding.buttonAddImage)

            binding.editTextCreateTaskImage.setText(it.toString())
        }
    )

    private fun changeSizeButtonAddImage(){
        val orientation = resources.configuration.orientation

        with(binding) {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                buttonAddImage.layoutParams.width = resources.getDimension(R.dimen.portrait_layout_size_button_add_image).toInt()
                buttonAddImage.layoutParams.height = resources.getDimension(R.dimen.portrait_layout_size_button_add_image).toInt()
            } else {
                buttonAddImage.layoutParams.width = resources.getDimension(R.dimen.landscape_layout_size_button_add_image).toInt()
                buttonAddImage.layoutParams.height = resources.getDimension(R.dimen.landscape_layout_size_button_add_image).toInt()
            }
            buttonAddImage.requestLayout();
        }
    }

    private fun clickOnButtonCreateAddTask(){
        with(binding) {
            taskName = editTextCreateTaskName.text.toString().trim()
            taskImage = editTextCreateTaskImage.text.toString()

            if (taskName == "") {
                createAlertDialog(resources.getString(R.string.error_null_text))
            } else {
                val intent = Intent()
                intent.putExtra("create_task_name", taskName)
                intent.putExtra("create_task_image", taskImage)
                setResult(RESULT_OK, intent)

                finish()
            }
        }

    }

    private fun createAlertDialog(text : String){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder
            .setMessage(text)
            .setTitle(R.string.error_title)
            .setPositiveButton(R.string.error_OK, { dialog, _ ->  dialog.cancel() })
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}