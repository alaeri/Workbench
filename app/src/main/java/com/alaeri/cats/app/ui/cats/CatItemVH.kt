package com.alaeri.cats.app.ui.cats

import android.util.Log
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.recyclerview.widget.extras.lifecycle.ViewModelVH
import androidx.recyclerview.widget.extras.viewholder.Bindable
import com.alaeri.cats.app.R
import com.alaeri.cats.app.cats.Cat
import com.alaeri.cats.app.databinding.CatItemBinding
import com.alaeri.ui.glide.ImageLoadingState


/**
 * Created by Emmanuel Requier on 19/04/2020.
 *
 *
 */
sealed class CatItemVH(view: View, parentLifecycle: Lifecycle): ViewModelVH(view, parentLifecycle), Bindable<Cat>{


    class CatVH(private val viewBinding: CatItemBinding,
                private val vmFactory: ViewModelProvider.Factory,
                parentLifecycle: Lifecycle) : CatItemVH(viewBinding.root, parentLifecycle){

        private lateinit var cat: Cat
        private lateinit var catViewModel : CatViewModel

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
                    catViewModel.onItemSet(cat, width, height)
                }
            }
        }



        override fun setItem(item: Cat) {
            Log.d("CATS","setItem")
            cat = item
            Log.d("CATS","$this $catViewModel ${cat.url}")
            viewBinding.apply {
                val ratio = cat.height.toFloat() / cat.width.toFloat()
                imageView.ratio = ratio
                imageView.setBackgroundResource(R.drawable.bg_cat_placeholder)
                imageView.setImageDrawable(null)
                val width = imageView.width
                val height = (imageView.width.toFloat() * ratio).toInt()
                if(width == 0){
                    imageView.addOnLayoutChangeListener(lcl)
                }else{
                    catViewModel.onItemSet(cat, width, height)
                    retryButton.setOnClickListener { catViewModel.onRetryClicked(width, height) }
                }
            }
        }

        override fun onCreate() {
            Log.d("CATS","onCreate")
            super.onCreate()
            catViewModel = viewModelProvider(vmFactory).get(CatViewModel::class.java)
            catViewModel.catLoadingState.map { it.imageLoadingState }.observe(this@CatVH, Observer {
                Log.d("CATS","$this $catViewModel $it")
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
            Log.d("CATS","$catViewModel clearViewModel")
        }
    }

}