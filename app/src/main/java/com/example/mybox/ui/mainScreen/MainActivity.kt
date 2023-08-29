package com.example.mybox.ui.mainScreen

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mybox.R
import com.example.mybox.data.Result
import com.example.mybox.databinding.ActivityMainBinding
import com.example.mybox.ui.ViewModelFactory
import com.example.mybox.ui.addCategoryScreen.AddCategoryActivity
import com.example.mybox.ui.detailScreen.CategoryDetailActivity
import com.example.mybox.utils.BOX_ID
import com.example.mybox.utils.Event
import com.google.android.material.bottomsheet.BottomSheetDialog
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

        mainAdapter = MainScreenAdapter(
            onClick = {
                val intent = Intent(this@MainActivity, CategoryDetailActivity::class.java)
                intent.putExtra(BOX_ID, it.Id)
                startActivity(intent)
            },
            onButtonClick = { clickedCategories->
                val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
                val bottomSheetDialog = BottomSheetDialog(this)
                bottomSheetDialog.setContentView(bottomSheetView)

                val editButton = bottomSheetView.findViewById<Button>(R.id.btEdit)
                val deleteButton = bottomSheetView.findViewById<Button>(R.id.btDelete)

                editButton.setOnClickListener {
                    val intent = Intent(this@MainActivity, AddCategoryActivity::class.java)
                    intent.putExtra(BOX_ID, clickedCategories.Id)
                    startActivity(intent)
                }

                deleteButton.setOnClickListener {
                    viewModel.deleteBox(clickedCategories)
                    bottomSheetDialog.dismiss()
                }

                bottomSheetDialog.show()
            }
        )

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
                        Toast.makeText(this@MainActivity , getString(R.string.failed_fetch) , Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        viewModel.snackbarText.observe(this) {
            showSnackBar(it)
        }

        binding.fAB1.setOnClickListener {
            startActivity(Intent(this@MainActivity, AddCategoryActivity::class.java))
        }
    }
    private fun showSnackBar(eventMessage: Event<Int>) {
        val message = eventMessage.getContentIfNotHandled() ?: return
        Snackbar.make(
            findViewById(R.id.relativeLayoutMain) ,
            getString(message) ,
            Snackbar.LENGTH_SHORT
        ).show()
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
