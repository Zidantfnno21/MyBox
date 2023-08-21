package com.example.mybox.ui.mainScreen

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mybox.R
import com.example.mybox.data.Result
import com.example.mybox.data.model.CategoryModel
import com.example.mybox.databinding.ActivityMainBinding
import com.example.mybox.ui.InsideActivity
import com.example.mybox.ui.SecondActivity
import com.example.mybox.ui.ViewModelFactory
import com.example.mybox.utils.BOX_ID
import com.example.mybox.utils.Event
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var mainAdapter : MainScreenAdapter
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainAdapter = MainScreenAdapter { box->
            val intent = Intent(this@MainActivity, InsideActivity::class.java)
            intent.putExtra(BOX_ID, box.Id)
            startActivity(intent)
        }
        binding.rvMainPage.apply {
            setHasFixedSize(false)
            smoothScrollToPosition(0)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = mainAdapter
        }

        initAction()

        viewModel.getBox().observe(this) {boxList->
            if (boxList != null) {
                when(boxList) {
                        is Result.Loading -> {
                            showLoading(true)
                        }
                    is Result.Success -> {
                        showLoading(false)
                        val list =  boxList.data
                        mainAdapter.submitList(list)
                    }
                    is Result.Error -> {
                        showLoading(false)
                        Toast.makeText(this@MainActivity , "Whooopss! Failed to get ur BOX :(" , Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        viewModel.snackbarText.observe(this) {
            showSnackBar(it)
        }

        binding.fAB1.setOnClickListener {
            startActivity(Intent(this@MainActivity, SecondActivity::class.java))
        }
    }
    private fun showSnackBar(eventMessage: Event<Int>) {
        val message = eventMessage.getContentIfNotHandled() ?: return
        Snackbar.make(
            findViewById(R.id.relativeLayoutMain) ,
            getString(message) ,
            Snackbar.LENGTH_SHORT
        ).setAction("Undo"){
            viewModel.insert(viewModel.undo.value?.getContentIfNotHandled() as CategoryModel)
        }.show()
    }

    private fun showLoading(loading: Boolean) {
        binding.progressBar.isVisible = loading
    }

    private fun initAction() {
        val itemTouchHelper = ItemTouchHelper(object  : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView : RecyclerView ,
                viewHolder : RecyclerView.ViewHolder
            ) : Int {
                return makeMovementFlags(0, ItemTouchHelper.ACTION_STATE_SWIPE)
            }

            override fun onMove(
                recyclerView : RecyclerView ,
                viewHolder : RecyclerView.ViewHolder ,
                target : RecyclerView.ViewHolder
            ) : Boolean {
                return false
            }

            override fun onSwiped(viewHolder : RecyclerView.ViewHolder , direction : Int) {
                val box = (viewHolder as MainScreenAdapter.ListViewHolder).getBox
                viewModel.deleteBox(box)
            }

        })
        itemTouchHelper.attachToRecyclerView(binding.rvMainPage)
    }
}
