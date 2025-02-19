package com.example.mybox.ui.screen.mainScreen

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.ChangeTransform
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.mybox.R
import com.example.mybox.data.Result
import com.example.mybox.data.model.CategoryModel
import com.example.mybox.databinding.ActivityMainBinding
import com.example.mybox.settings.SettingsActivity
import com.example.mybox.ui.addCategoryScreen.AddCategoryActivity
import com.example.mybox.ui.detailScreen.CategoryDetailActivity
import com.example.mybox.utils.BOX
import com.example.mybox.utils.BOX_ID
import com.example.mybox.utils.Event
import com.example.mybox.utils.PermissionHandler
import com.example.mybox.utils.PreferencesHelper
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    private lateinit var binding : ActivityMainBinding
    private val permissionHandler = PermissionHandler(this)
    private lateinit var mainAdapter : MainScreenAdapter
    private val viewModel: MainViewModel by viewModels()
    private val uid: String by lazy {
        try {
            preferencesHelper.getSavedUsername()
        } catch (e: Exception) {
            e.printStackTrace()
            "username"
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHandler.onRequestPermissionsResult(requestCode , permissions)
    }

    override fun onStart() {
        super.onStart()
        getUserData(uid)
    }

    private fun getUserData(uid: String) {
        viewModel.getUserDataByUID(uid).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    val username = result.data.name ?: "username"
                    
                    setGreetings(username)

                    Log.d("LoginActivity", "Username: $username")
                }
                is Result.Error -> {
                    showLoading(false)
                    Log.e("LoginActivity" , "getUserData: $uid ?? not found")
                }
            }
        }
    }

    private fun setGreetings(username : String) {
        binding.tvGreetings.text = getString(R.string.greetings, username)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        permissionHandler.requestPermissionsIfNecessary()

        if (!preferencesHelper.isViewVisible()) {
            binding.disposableCardView.isVisible = false
        }

        binding.appBar.addOnOffsetChangedListener { _ , verticalOffset ->
            val scalingFactor = 0.2F
            val adjustedOffset = verticalOffset.toFloat() * scalingFactor
            val scrollRange = binding.appBar.totalScrollRange
            val fraction = (scrollRange + verticalOffset).toFloat() / scrollRange
            val percent = (abs(verticalOffset)).toFloat() / scrollRange

            binding.apply {
                buttonContainer.translationY = -verticalOffset.toFloat()
                container.alpha = 1F - percent
                container.translationY = adjustedOffset
            }

            val fadeTransition = AutoTransition()
            TransitionManager.beginDelayedTransition(binding.buttonContainer , fadeTransition)

            if (fraction == 0.toFloat()) {
                binding.apply {
                    buttonContainer.isVisible = false
                    toolbarContainer.isVisible = true
                }
            } else {
                binding.apply {
                    buttonContainer.isVisible = true
                    toolbarContainer.isVisible = false
                }
            }
        }


        binding.closeIv.setOnClickListener {
            binding.disposableCardView.isVisible = false
            preferencesHelper.saveState(false)

            val params = binding.rvMainPage.layoutParams as ConstraintLayout.LayoutParams
            binding.rvMainPage.layoutParams = params
        }

        mainAdapter = MainScreenAdapter(
            onClick = {
                val intent = Intent(this@MainActivity, CategoryDetailActivity::class.java)
                intent.putExtra(BOX, it)
                startActivity(intent)
            },
            onButtonClick = { clickedCategories, position->
                showBottomSheet(clickedCategories, position)
            }
        )

        binding.rvMainPage.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = mainAdapter
            mainAdapter.viewState = preferencesHelper.getView()

            val space = 16
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect , view: View , parent: RecyclerView , state: RecyclerView.State) {
                    with(outRect) {
                        if (parent.getChildAdapterPosition(view) == 0) {
                            top = space
                        }
                        left =  space
                        right = space
                        bottom = space
                    }
                }
            })
        }

        val gridClickListener = View.OnClickListener {
            mainAdapter.viewState = (mainAdapter.viewState + 1) % 2
            mainAdapter.notifyDataSetChanged()

            preferencesHelper.saveViewState(mainAdapter.viewState)

            val icon = if (mainAdapter.viewState == 0) R.drawable.ic_list_view_24 else R.drawable.ic_card_view_24
            binding.toolbarBtnChangeGrid.setIconResource(icon)
            binding.gridButton.setIconResource(icon)
        }

        val onSearchListener = View.OnClickListener {

            transition()

            binding.appBar.setExpanded(false, false)
            binding.rvMainPage.smoothScrollToPosition(0)
            binding.relativeLayoutMain.smoothScrollTo(0, 0)

            val params = binding.appBar.layoutParams as CoordinatorLayout.LayoutParams
            if (params.behavior == null) {
                params.behavior = AppBarLayout.Behavior()
            }
            val behavior = params.behavior as AppBarLayout.Behavior?
            behavior?.setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
                override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                    return false
                }
            })

            binding.appBar.layoutParams = params

            showSearchView(true)

        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.searchViews.isVisible) {
                    handleBack()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }

        onBackPressedDispatcher.addCallback(this, callback)

        binding.backButtonSearch.setOnClickListener {
            handleBack()
        }

        binding.apply {
            toolbarBtnSettings.setOnClickListener { settings() }

            settingsButton.setOnClickListener { settings() }

            toolbarBtnChangeGrid.setOnClickListener(gridClickListener)

            gridButton.setOnClickListener(gridClickListener)

            searchButton.setOnClickListener(onSearchListener)

            toolbarBtnSearch.setOnClickListener(onSearchListener)

            fabMain.setOnClickListener {
                startActivity(
                    Intent(
                        this@MainActivity,
                           AddCategoryActivity::class.java
                    )
                )
            }

        }

        binding.searchViews.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query : String?) : Boolean {
                mainAdapter.getFilter().filter(query.toString())

                return false
            }

            override fun onQueryTextChange(newText : String?) : Boolean {
                val isQueryEmpty = newText.isNullOrEmpty()
                if (!isQueryEmpty) {
                    mainAdapter.getFilter().filter(newText)
                }else{
                    mainAdapter.submitList(mainAdapter.fullList)
                }

                return true
            }

        })

        viewModel.snackbarText.observe(this) {
            showSnackBar(it)
        }

        fetchData()
    }

    override fun onResume() {
        super.onResume()
        fetchData()
    }

    private fun handleBack() {
        showSearchView(false)
        clearQuery()
        transition()
    }

    private fun transition() {
        val transition = ChangeTransform().apply {
            duration = 300
        }

        TransitionManager.beginDelayedTransition(binding.root, transition)
    }

    private fun showSearchView(isSearchViewVisible : Boolean) {
        binding.relativeLayoutMain.isNestedScrollingEnabled = !isSearchViewVisible

        if (isSearchViewVisible){
            binding.apply {
                backButtonSearch.visibility = View.VISIBLE
                searchViews.visibility = View.VISIBLE

                toolbarBtnSearch.visibility = View.GONE
                toolbarBtnChangeGrid.visibility = View.GONE
                toolbarBtnSettings.visibility = View.GONE
            }
        }else{
            binding.apply {
                backButtonSearch.visibility = View.GONE
                searchViews.visibility = View.GONE

                toolbarBtnSearch.visibility = View.VISIBLE
                toolbarBtnChangeGrid.visibility = View.VISIBLE
                toolbarBtnSettings.visibility = View.VISIBLE
            }
        }
    }

    private fun clearQuery(){
        binding.searchViews.setQuery("", false)

    }

    private fun showBottomSheet(clickedCategories : CategoryModel , position : Int) {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_dialog, binding.root ,false)
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetView)

        val titleCategory = bottomSheetDialog.findViewById<MaterialTextView>(R.id.bottomSheet_CategoryInfo)
        val editButton = bottomSheetView.findViewById<MaterialTextView>(R.id.bottomSheet_Edit)
        val deleteButton = bottomSheetView.findViewById<MaterialTextView>(R.id.bottomSheet_Delete)

        titleCategory?.text = clickedCategories.name
        Glide.with(this)
            .load(clickedCategories.imageURL)
            .apply(
                RequestOptions()
                    .override(150,150)
                    .placeholder(R.drawable.thumbnail_countainer)
                    .centerCrop()
            )
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    val roundedDrawable = RoundedBitmapDrawableFactory.create(
                        resources,
                        (resource as BitmapDrawable).bitmap
                    )
                    roundedDrawable.cornerRadius = 20f

                    titleCategory?.setCompoundDrawablesWithIntrinsicBounds(roundedDrawable, null, null, null)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    titleCategory?.setCompoundDrawables(null,null, null,null)
                }
            })

        editButton.setOnClickListener {
            val intent = Intent(this@MainActivity, AddCategoryActivity::class.java)
            intent.putExtra(BOX_ID, clickedCategories)
            startActivity(intent)

            bottomSheetDialog.dismiss()
        }

        deleteButton.setOnClickListener {
            showCustomDialog(clickedCategories, position)

            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun showCustomDialog(clickedCategories : CategoryModel , position : Int) {
        val dialog = Dialog(this, R.style.RoundShapeTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.delete_custom_dialog)

        val title = dialog.findViewById<TextView>(R.id.deleteDialogText)
        title.text = getString(R.string.delete_dialog_title, clickedCategories.name)

        val cancelButton = dialog.findViewById<Button>(R.id.cancelButton)

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        val deleteButton = dialog.findViewById<Button>(R.id.deleteButton)

        deleteButton.setOnClickListener {
            mainAdapter.removeItem(position)

            viewModel.deleteBox(uid , clickedCategories , clickedCategories.id)
            fetchData()

            dialog.dismiss()
        }

        dialog.show()
    }

    private fun fetchData() {
        viewModel.getBox(uid).observe(this) {boxList->
            if (boxList != null) {
                when(boxList) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                        val list =  boxList.data
                        if (list.isEmpty()){
                            showEmptyState(true)
                            mainAdapter.submitFullList(list)
                        }else{
                            showEmptyState(false)
                            mainAdapter.submitFullList(list)
                        }

                    }
                    is Result.Error -> {
                        viewModel.retryOrLogout(uid)
                        showLoading(false)
                        Toast.makeText(this@MainActivity , getString(R.string.failed_fetch) , Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun settings() {
        val intent = Intent(this@MainActivity, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun showSnackBar(eventMessage: Event<Int>) {
        val message = eventMessage.getContentIfNotHandled() ?: return
        val snackBar = Snackbar.make(
            findViewById(R.id.relativeLayoutMain),
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

    private fun showEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.cardEmptyState.visibility = View.VISIBLE
        }else{
            binding.cardEmptyState.visibility = View.GONE
        }
    }
}