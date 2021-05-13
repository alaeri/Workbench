package com.alaeri.cats.app.ui.cats

import android.util.Log
import android.view.View
import androidx.lifecycle.*
import com.alaeri.cats.app.R
import com.alaeri.cats.app.cats.Cat
import com.alaeri.cats.app.databinding.CatItemBinding
import com.alaeri.log.glide.ImageLoadingState
import com.alaeri.recyclerview.extras.lifecycle.ViewModelVH
import com.alaeri.recyclerview.extras.viewholder.Bindable


/**
 * Created by Emmanuel Requier on 19/04/2020.
 *
 *
 */
sealed class CatItemVH(view: View, parentLifecycle: Lifecycle, vmStore: ViewModelStore): ViewModelVH(view, parentLifecycle, vmStore),
    Bindable<Cat> {


    class CatVH(private val viewBinding: CatItemBinding,
                private val vmFactory: ViewModelProvider.Factory,
                parentLifecycle: Lifecycle, vmStore: ViewModelStore) : CatItemVH(viewBinding.root, parentLifecycle, vmStore){

        private lateinit var cat: Cat
        private lateinit var catItemViewModel : CatItemViewModel

        private val lcl =  object: View.OnLayoutChangeListener {
            override fun onLayoutChange(
                view: View,
                p1: Int,
                p2: Int,
                p3: Int,
                p4: Int,
                p5: Int,
                p6: Int,
                p7: Int,
                p8: Int
            ) {
                val height = view.height
                val width = view.width
                if (width > 0 &&  height> 0) {
                    view.removeOnLayoutChangeListener(this)
                    catItemViewModel.onItemSet(cat, width, height)
                }
            }
        }



        override fun setItem(item: Cat) {
            Log.d("CATS","setItem")
            cat = item
            Log.d("CATS","$this $catItemViewModel ${cat.url}")
            viewBinding.apply {
                val ratio = cat.height.toFloat() / cat.width.toFloat()
                imageView.ratio = ratio
                imageView.requestLayout()
                imageView.setBackgroundResource(R.drawable.bg_cat_placeholder)
                imageView.setImageDrawable(null)
                val width = imageView.width
                val height = (imageView.width.toFloat() * ratio).toInt()
                if(width == 0){
                    imageView.addOnLayoutChangeListener(lcl)
                }else{
                    catItemViewModel.onItemSet(cat, width, height)
                    retryButton.setOnClickListener { catItemViewModel.onRetryClicked(width, height) }
                }
            }
        }

        override fun onCreate() {
            Log.d("CATS","onCreate")
            super.onCreate()
            catItemViewModel = viewModelProvider(vmFactory).get(viewBinding.toString(), CatItemViewModel::class.java)
            catItemViewModel.catLoadingState.map { it.imageLoadingState }.observe(this@CatVH, Observer {
                Log.d("CATS","$this $catItemViewModel $it")
                viewBinding.apply {
                    when(it){
                        is ImageLoadingState.Loading -> {
                            retryButton.visibility = View.GONE
                            progressCircular.visibility = View.VISIBLE
                            progressCircular.max = it.totalReadCount.toInt()
                            progressCircular.progress = it.readCount.toInt()
                            catsLoadingTextView.visibility = View.VISIBLE
                            catsLoadingTextView.text = "loading"
                        }
                        is ImageLoadingState.AwaitingLoad -> {
                            retryButton.visibility = View.GONE
                            progressCircular.visibility = View.VISIBLE
                            progressCircular.max = 1
                            progressCircular.progress = 0
                            catsLoadingTextView.text = "queued"
                            catsLoadingTextView.visibility = View.VISIBLE
                        }
                        is ImageLoadingState.Failed -> {
                            retryButton.visibility = View.VISIBLE
                            progressCircular.visibility = View.GONE
                            catsLoadingTextView.visibility = View.VISIBLE
                            catsLoadingTextView.text = it.exception.message ?: "Error while loading"
                            catsLoadingTextView.visibility = View.VISIBLE
                        }
                        is ImageLoadingState.Completed -> {
                            progressCircular.visibility = View.GONE
                            retryButton.visibility = View.GONE
                            catsLoadingTextView.visibility = View.GONE
                            imageView.setImageDrawable(it.bitmap)
                        }
                    }
                }
            })
        }

        override fun onDestroy() {
            super.onDestroy()
            viewBinding.apply {
                imageView.setImageDrawable(null)
                imageView.removeOnLayoutChangeListener(lcl)
            }
            Log.d("CATS","$catItemViewModel clearViewModel")
        }
    }

}