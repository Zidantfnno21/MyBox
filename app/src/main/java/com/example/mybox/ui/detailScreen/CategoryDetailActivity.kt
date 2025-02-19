package com.example.mybox.ui.detailScreen

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mybox.R
import com.example.mybox.data.Result
import com.example.mybox.data.model.CategoryModel
import com.example.mybox.databinding.ActivityCategoryDetailBinding
import com.example.mybox.ui.addCategoryScreen.AddCategoryActivity
import com.example.mybox.ui.addItemScreen.AddItemActivity
import com.example.mybox.ui.detailItemScreen.DetailItemActivity
import com.example.mybox.utils.BOX
import com.example.mybox.utils.BOX_ID
import com.example.mybox.utils.DETAIL_ID
import com.example.mybox.utils.Event
import com.example.mybox.utils.PreferencesHelper
import com.example.mybox.utils.makeToast
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject


class CategoryDetailActivity : AppCompatActivity() {
    @Inject
    lateinit var preferencesHelper: PreferencesHelper
    private lateinit var binding : ActivityCategoryDetailBinding
    private lateinit var detailAdapter : CategoryDetailAdapter
    private val viewModel : CategoryDetailViewModel by viewModels()
    private lateinit var category: CategoryModel
    private lateinit var categoryId: String
    private val uid: String by lazy {
        try {
            preferencesHelper.getSavedUsername()
        } catch (e: Exception) {
            e.printStackTrace()
            "username" // Provide a default value or handle the exception as per your requirements
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        enableEdgeToEdge()

        category = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(BOX, CategoryModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(BOX)
        } ?: throw IllegalArgumentException("CategoryModel not found in the intent")

        categoryId = category.id.toString()


        binding.imageButtonBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        
        binding.imageButtonMore.setOnClickListener {view->
            val popup = PopupMenu(this, view)
            val inflater: MenuInflater = popup.menuInflater
            inflater.inflate(R.menu.menu_category_detail, popup.menu)

            popup.setOnMenuItemClickListener {menuItem->
                when (menuItem.itemId) {
                    R.id.itemEdit -> {
                        val intent = Intent(this, AddCategoryActivity::class.java)
                        intent.putExtra(BOX_ID, category)
                        startActivity(intent)

                        true
                    }
                    R.id.itemDelete -> {
                        val dialog = Dialog(this , R.style.RoundShapeTheme)
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        dialog.setContentView(R.layout.delete_custom_dialog)

                        val title = dialog.findViewById<TextView>(R.id.deleteDialogText)
                        title.text = getString(R.string.delete_dialog_title , category.name)

                        val cancelButton = dialog.findViewById<Button>(R.id.cancelButton)

                        cancelButton.setOnClickListener {
                            dialog.dismiss()
                        }
                        val deleteButton = dialog.findViewById<Button>(R.id.deleteButton)

                        deleteButton.setOnClickListener {
                            viewModel.deleteBox(uid , category, category.id)
                            finish()

                            dialog.dismiss()
                        }

                        dialog.show()

                        true
                    }
                    else -> {
                        false
                    }
                }
            }

            popup.show()
        }

        detailAdapter = CategoryDetailAdapter (
            onClick = {
                val intent = Intent(this@CategoryDetailActivity, DetailItemActivity::class.java)
                intent.putExtra(DETAIL_ID, it)
                startActivity(intent)
            }
        )

        binding.rvDetailPage.apply {
            adapter = detailAdapter
            layoutManager = GridLayoutManager(this@CategoryDetailActivity , 2)
            addItemDecoration(
                GridSpacingItemDecoration(2, 24, false)
            )
        }

        viewModel.snackBarText.observe(this) {
            showSnackBar(it)
        }

        binding.floatingActionButton2.setOnClickListener {
            val intent = Intent(this@CategoryDetailActivity, AddItemActivity::class.java)
            intent.putExtra(BOX_ID, categoryId)
            startActivity(intent)
        }

        fetchData()

        initAction()

    }

    private fun showDialog(url: String?) {
        binding.ivBanner.setOnClickListener {
            val builder = AlertDialog.Builder(this, R.style.RoundShapeTheme)
            val inflater = LayoutInflater.from(this)
            val view = inflater.inflate(R.layout.image_dialog,null)

            val image: ImageView = view.findViewById(R.id.dialogImage)
            Glide.with(this)
                .load(url)
                .into(image)


            builder.setView(view)
            val dialog = builder.create()

            dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation

            dialog.setCanceledOnTouchOutside(true)
            dialog.show()
        }
    }

    override fun onResume() {
        super.onResume()
        fetchData()
    }


    private fun fetchData() {
        viewModel.getCategoriesBoxById(uid , categoryId.toInt()).observe(this) { result->
            if (result != null) {
                when(result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }

                    is Result.Success -> {
                        showLoading(false)

                        category = result.data

                        Glide.with(this)
                            .load(result.data.imageURL)
                            .into(binding.imageView4)
                        showDialog(result.data.imageURL)

                        binding.tViewTitle.text = result.data.name

                        binding.tViewDesc.text = result.data.description

                    }

                    is Result.Error -> {
                        showLoading(false)
                        finish()
                        makeToast(this ,result.error)
                    }
                }
            }
        }

        viewModel.getDetailBox(uid, categoryId.toInt()).observe(this) { result->
            if (result != null) {
                when(result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }

                    is Result.Success -> {
                        showLoading(false)
                        val list =  result.data
                        if (list.isEmpty()){
                            showEmptyState(true)
                            detailAdapter.submitList(list.toList())
                        }else{
                            showEmptyState(false)
                            detailAdapter.submitList(list.toList())
                        }
                    }

                    is Result.Error -> {
                        onBackPressedDispatcher.onBackPressed()
                        showLoading(false)
                        makeToast(this, result.error)
                    }
                }
            }
        }
    }

    private fun showEmptyState(isEmpty: Boolean) {
        val mainLayout = findViewById<LinearLayout>(R.id.emptyStateLayout)

        if (isEmpty) {
            mainLayout.removeAllViews()

            val emptyStateView = layoutInflater.inflate(R.layout.empty_state_layout, binding.root, false)
            mainLayout.addView(emptyStateView)
            mainLayout.visibility = View.VISIBLE
        }else{
            mainLayout.visibility = View.GONE
        }
    }

    private fun showSnackBar(eventMessage: Event<Int>) {
        val message = eventMessage.getContentIfNotHandled() ?: return
        val snackBar = Snackbar.make(
            findViewById(R.id.relativeLayoutDetail),
            getString(message),
            Snackbar.LENGTH_LONG
        )
        snackBar.show()
    }
    
    private fun showLoading(loading: Boolean) {
        binding.progressBar.background = (ColorDrawable(Color.parseColor("#63616161")))
        binding.progressBar.isVisible = loading
        if (loading){
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE , WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    private fun initAction() {
        val itemTouchHelper = ItemTouchHelper(object  : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView : RecyclerView ,
                viewHolder : RecyclerView.ViewHolder
            ) : Int {
                return makeMovementFlags(0, ItemTouchHelper.RIGHT)
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
                val position = viewHolder.bindingAdapterPosition
                detailAdapter.removeItem(position)

                viewModel.deleteDetailBox(uid , detailBox , detailBox.categoryId!! , detailBox.id!!)
                fetchData()
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.rvDetailPage)
    }
}