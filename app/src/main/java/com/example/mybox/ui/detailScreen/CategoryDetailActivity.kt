package com.example.mybox.ui.detailScreen

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.mybox.R
import com.example.mybox.data.Result
import com.example.mybox.databinding.ActivityCategoryDetailBinding
import com.example.mybox.ui.ViewModelFactory
import com.example.mybox.ui.addItemScreen.AddItemActivity
import com.example.mybox.ui.detailItemScreen.DetailItemActivity
import com.example.mybox.utils.BOX_ID
import com.example.mybox.utils.DETAIL_ID
import com.example.mybox.utils.Event
import com.google.android.material.snackbar.Snackbar

class CategoryDetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCategoryDetailBinding
    private lateinit var detailAdapter : CategoryDetailAdapter
    private val viewModel by viewModels<CategoryDetailViewModel> {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.floatingActionButton2.setOnClickListener {
            val intent = Intent(this@CategoryDetailActivity, AddItemActivity::class.java)
            startActivity(intent)
        }

        detailAdapter = CategoryDetailAdapter (
            onClick = {
                val intent = Intent(this@CategoryDetailActivity, DetailItemActivity::class.java)
                intent.putExtra(DETAIL_ID, it.id)
                intent.putExtra(BOX_ID, it.categoryId)
                startActivity(intent)
            }
        )

        binding.rvDetailPage.apply {
            setHasFixedSize(false)
            smoothScrollToPosition(0)
            adapter = detailAdapter
            layoutManager = GridLayoutManager(this@CategoryDetailActivity , 2)
        }

        viewModel.snackbarText.observe(this) {
            showSnackBar(it)
        }

        getData()

        initAction()

    }

    private fun getData() {
        val categoryId = intent.getStringExtra(BOX_ID)
        viewModel.getDetailBox(categoryId = categoryId!!.toInt()).observe(this) { result->
            if (result != null) {
                when(result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                        val list = result.data
                        detailAdapter.submitList(list)
                    }
                    is Result.Error -> {
                        showLoading(false)
                        Toast.makeText(this@CategoryDetailActivity, getString(R.string.failed_fetch) , Toast.LENGTH_SHORT).show()
                    }
                }
            }
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
                val detailBox = (viewHolder as CategoryDetailAdapter.ListViewHolder).getDetailBox
                viewModel.deleteDetailBox(detailBox)
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.rvDetailPage)
    }
}