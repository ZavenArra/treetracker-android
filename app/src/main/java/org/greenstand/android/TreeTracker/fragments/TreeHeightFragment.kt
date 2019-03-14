package org.greenstand.android.TreeTracker.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.TransitionManager
import kotlinx.android.synthetic.main.fragment_tree_height.*
import kotlinx.android.synthetic.main.fragment_tree_height.view.*
import org.greenstand.android.TreeTracker.R

import org.greenstand.android.TreeTracker.utilities.animateColor
import org.greenstand.android.TreeTracker.utilities.color
import org.greenstand.android.TreeTracker.viewmodels.TreeHeightViewModel


class TreeHeightFragment : Fragment() {

    lateinit var viewModel: TreeHeightViewModel

    var isInitialState = true

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this).get(TreeHeightViewModel::class.java)
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tree_height, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val parentView = view as ConstraintLayout

        listOf(view.height_button_five,
               view.height_button_four,
               view.height_button_three,
               view.height_button_two,
               view.height_button_one)
            .forEachIndexed { index, colorView ->
                colorView.setOnClickListener {
                    moveToHeight(parentView, colorView, index)
                }
            }
    }

    fun indexToBias(index: Int): Float {
        return when(index) {
            0 -> .025f
            1 -> .275f
            2 -> .5f
            3 -> .725f
            4 -> .975f
            else -> 0f
        }
    }

    fun moveToHeight(view: ConstraintLayout, colorView: View, index: Int) {

        fun animatedUpdate(view: ConstraintLayout, index: Int) {
            view.post {

                val bias = indexToBias(index)

                val height = view.stick_container.height / 5
                val width = view.stick_container.width

                val selectedHeight = (height * 1.1).toInt()
                val selectedWidth = (width * 2)

                TransitionManager.beginDelayedTransition(view)

                ConstraintSet().apply {
                    clone(view)
                    setVerticalBias(R.id.floating_button, bias)
                    constrainHeight(R.id.floating_button, selectedHeight)
                    constrainWidth(R.id.floating_button, selectedWidth)
                    floating_button.animateColor(toColor = colorView.color)
                    applyTo(view)
                }

            }
        }

        if (isInitialState) {
            // move view to position of tapped color, size it, color it, make visible all without animations
            view.post {

                val bias = indexToBias(index)

                val height = view.stick_container.height / 5
                val width = view.stick_container.width


                ConstraintSet().apply {
                    clone(view)
                    connect(R.id.floating_button, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 32)
                    connect(R.id.floating_button, ConstraintSet.BOTTOM, R.id.fragmentTreeHeightSave, ConstraintSet.TOP, 32)
                    centerHorizontally(R.id.floating_button, ConstraintSet.PARENT_ID)
                    setVerticalBias(R.id.floating_button, bias)
                    constrainHeight(R.id.floating_button, height)
                    constrainWidth(R.id.floating_button, width)
                    setVisibility(R.id.floating_button, ConstraintSet.VISIBLE)
                    floating_button.setCardBackgroundColor(colorView.color)
                    applyTo(view)
                }
                isInitialState = false

                animatedUpdate(view, index)
            }
        } else {
            animatedUpdate(view, index)
        }
    }

}
