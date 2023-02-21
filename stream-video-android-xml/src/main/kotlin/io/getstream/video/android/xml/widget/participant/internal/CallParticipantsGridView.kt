package io.getstream.video.android.xml.widget.participant.internal

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.Guideline
import androidx.transition.TransitionManager
import io.getstream.video.android.core.model.CallParticipantState
import io.getstream.video.android.xml.utils.extensions.constrainViewBottomToTopOfView
import io.getstream.video.android.xml.utils.extensions.constrainViewEndToStartOfView
import io.getstream.video.android.xml.utils.extensions.constrainViewStartToEndOfView
import io.getstream.video.android.xml.utils.extensions.constrainViewToParent
import io.getstream.video.android.xml.utils.extensions.constrainViewToParentBySide
import io.getstream.video.android.xml.utils.extensions.constrainViewTopToBottomOfView
import io.getstream.video.android.xml.utils.extensions.createStreamThemeWrapper
import io.getstream.video.android.xml.utils.extensions.setConstraints
import io.getstream.video.android.xml.widget.participant.CallParticipantView
import io.getstream.video.android.xml.widget.participant.RendererInitializer
import io.getstream.video.android.xml.widget.renderer.VideoRenderer

internal class CallParticipantsGridView : ConstraintLayout, VideoRenderer {

    private val childList: MutableList<CallParticipantView> = mutableListOf()

    /**
     * Sets the [RendererInitializer] handler.
     *
     * @param rendererInitializer Handler for initializing the renderer.
     */
    override fun setRendererInitializer(rendererInitializer: RendererInitializer) {
        childList.forEach { it.setRendererInitializer(rendererInitializer) }
    }

    public constructor(context: Context) : this(context, null)
    public constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    public constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context.createStreamThemeWrapper(), attrs, defStyleAttr
    ) {}

    internal var callControlsHeight: () -> Int = { 0 }
    internal lateinit var buildParticipantView: () -> CallParticipantView

    internal fun updateParticipants(participants: List<CallParticipantState>) {
        when {
            childList.size > participants.size -> {
                val diff = childList.size - participants.size
                for (index in 0 until diff) {
                    val view = childList.last()
                    removeView(view)
                    childList.remove(view)
                }
            }

            childList.size < participants.size -> {
                val diff = participants.size - childList.size
                for (index in 0 until diff) {
                    val view = buildParticipantView().apply {
                        layoutParams = LayoutParams(LayoutParams.MATCH_CONSTRAINT, LayoutParams.MATCH_CONSTRAINT)
                    }
                    childList.add(view)
                    addView(view)
                }
            }
        }

        participants.forEachIndexed { index, participant ->
            val view = childList[index]
            view.setParticipant(participant)
            view.tag = participant.id
        }

        updateConstraints()
    }

    /**
     * Updates the constraints of the shown [CallParticipantView]s so they all fit in the viewport.
     */
    private fun updateConstraints() {
        TransitionManager.beginDelayedTransition(this)
        setConstraints {
            childList.forEachIndexed { index, callParticipantView ->
                if (isBottomChild(index)) callParticipantView.setLabelBottomOffset(callControlsHeight())
            }

            when (childList.size) {
                1 -> {
                    constrainViewToParent(childList[0])
                }
                2 -> {
                    constrainViewToParentBySide(childList[0], ConstraintSet.TOP)
                    constrainViewToParentBySide(childList[0], ConstraintSet.START)
                    constrainViewToParentBySide(childList[0], ConstraintSet.END)
                    constrainViewBottomToTopOfView(childList[0], childList[1])

                    constrainViewToParentBySide(childList[1], ConstraintSet.BOTTOM)
                    constrainViewToParentBySide(childList[1], ConstraintSet.START)
                    constrainViewToParentBySide(childList[1], ConstraintSet.END)
                    constrainViewTopToBottomOfView(childList[1], childList[0])
                }
                3 -> {
                    constrainViewToParentBySide(childList[0], ConstraintSet.TOP)
                    constrainViewToParentBySide(childList[0], ConstraintSet.START)
                    constrainViewEndToStartOfView(childList[0], childList[1])
                    constrainViewBottomToTopOfView(childList[0], childList[2])

                    constrainViewToParentBySide(childList[1], ConstraintSet.TOP)
                    constrainViewToParentBySide(childList[1], ConstraintSet.END)
                    constrainViewStartToEndOfView(childList[1], childList[0])
                    constrainViewBottomToTopOfView(childList[1], childList[2])

                    constrainViewToParentBySide(childList[2], ConstraintSet.BOTTOM)
                    constrainViewToParentBySide(childList[2], ConstraintSet.START)
                    constrainViewToParentBySide(childList[2], ConstraintSet.END)
                    constrainViewTopToBottomOfView(childList[2], childList[1])
                }
                4 -> {
                    constrainViewToParentBySide(childList[0], ConstraintSet.TOP)
                    constrainViewToParentBySide(childList[0], ConstraintSet.START)
                    constrainViewEndToStartOfView(childList[0], childList[1])
                    constrainViewBottomToTopOfView(childList[0], childList[2])

                    constrainViewToParentBySide(childList[1], ConstraintSet.TOP)
                    constrainViewToParentBySide(childList[1], ConstraintSet.END)
                    constrainViewStartToEndOfView(childList[1], childList[0])
                    constrainViewBottomToTopOfView(childList[1], childList[3])

                    constrainViewToParentBySide(childList[2], ConstraintSet.BOTTOM)
                    constrainViewToParentBySide(childList[2], ConstraintSet.START)
                    constrainViewTopToBottomOfView(childList[2], childList[0])
                    constrainViewEndToStartOfView(childList[2], childList[3])

                    constrainViewToParentBySide(childList[3], ConstraintSet.BOTTOM)
                    constrainViewToParentBySide(childList[3], ConstraintSet.END)
                    constrainViewTopToBottomOfView(childList[3], childList[1])
                    constrainViewStartToEndOfView(childList[3], childList[2])
                }
            }
        }
    }

    private fun isBottomChild(index: Int): Boolean {
        return when {
            childList.size == 1 -> true
            childList.size == 2 && index == 1 -> true
            childList.size > 2 && index > 1 -> true
            else -> false
        }
    }

    /**
     * Updates the current primary speaker and shows a border around the primary speaker.
     *
     * @param participant The call participant marked as a primary speaker.
     */
    internal fun updatePrimarySpeaker(participant: CallParticipantState?) {
        childList.forEach { it.setActive(it.tag == participant?.id) }
    }

    /**
     * Used to instantiate a new [Guideline] which help us to divide the screen to sections.
     */
    private fun buildGuideline(orientation: Int, guidePercent: Float) = Guideline(context).apply {
        this.id = View.generateViewId()
        this.layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT
        ).apply {
            this.orientation = orientation
            this.guidePercent = guidePercent
        }
    }
}