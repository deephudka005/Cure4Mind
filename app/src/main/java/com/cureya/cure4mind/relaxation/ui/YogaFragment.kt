package com.cureya.cure4mind.relaxation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cureya.cure4mind.R
import com.cureya.cure4mind.databinding.CardYogaBinding
import com.cureya.cure4mind.databinding.FragmentRelaxationYogaBinding
import com.cureya.cure4mind.model.Yoga
import com.cureya.cure4mind.relaxation.viewHolder.YogaViewHolder
import com.cureya.cure4mind.util.database
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class YogaFragment : Fragment() {

    private lateinit var adapter: FirebaseRecyclerAdapter<Yoga, YogaViewHolder>
    private lateinit var binding: FragmentRelaxationYogaBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRelaxationYogaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val yogaRef = database.child(YOGA_LIST)
        val yogaList = FirebaseRecyclerOptions.Builder<Yoga>()
            .setQuery(yogaRef, Yoga::class.java)
            .build()

        adapter = object: FirebaseRecyclerAdapter<Yoga, YogaViewHolder>(yogaList) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YogaViewHolder {
                val layoutView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.card_yoga, parent, false)
                return YogaViewHolder(
                    CardYogaBinding.bind(layoutView),
                    this@YogaFragment,
                    binding.progressBar
                )
            }
            override fun onBindViewHolder(holder: YogaViewHolder, position: Int, model: Yoga) {
                holder.bind(model)
            }
        }

        binding.yogaCardRecyclerView.adapter = adapter
        binding.yogaCardRecyclerView.itemAnimator = null
    }

    fun goToYogaDetailsFragment(itemTitle: String) = findNavController().navigate(
        YogaFragmentDirections.actionYogaFragmentToYogaDetailsFragment(
            itemTitle
        )
    )

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    companion object {
        const val YOGA_LIST = "yoga"
        const val YOGA_TITLE = "title"
    }
}