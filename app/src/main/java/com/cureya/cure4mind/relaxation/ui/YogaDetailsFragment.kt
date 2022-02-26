package com.cureya.cure4mind.relaxation.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import coil.load
import com.cureya.cure4mind.databinding.FragmentRelaxationYogaDetailsBinding
import com.cureya.cure4mind.model.Yoga
import com.cureya.cure4mind.relaxation.ui.YogaFragment.Companion.YOGA_LIST
import com.cureya.cure4mind.relaxation.ui.YogaFragment.Companion.YOGA_TITLE
import com.cureya.cure4mind.util.database
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class YogaDetailsFragment : Fragment() {

    private lateinit var binding: FragmentRelaxationYogaDetailsBinding

    private val navArgument: YogaDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRelaxationYogaDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val yogaTitle = navArgument.itemTitle

        database.child(YOGA_LIST).orderByChild(YOGA_TITLE).equalTo(yogaTitle)
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value != null) {
                        val item = snapshot.children.first().getValue(Yoga::class.java)!!
                        bindData(item)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("YogaDetailsFragment", "List with yoga title does not exists", error.toException())
                }
            })
    }

    private fun bindData(item: Yoga) {
        var stepCount = 0
        val steps = item.steps!!.split('.')

        binding.yogaActionTitle.text = item.title
        binding.yogaDetailDescription.text = item.description
        binding.yogaImage.load(item.imgUrl)

        while (stepCount < steps.size - 1) {
            val str = "Step ${stepCount + 1}: ".plus(steps[stepCount] + ".\n\n")
            binding.yogaDetailSteps.append(str)
            stepCount++
        }
    }
}