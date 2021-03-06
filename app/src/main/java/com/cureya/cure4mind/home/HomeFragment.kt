package com.cureya.cure4mind.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.cureya.cure4mind.R
import com.cureya.cure4mind.blog
import com.cureya.cure4mind.databinding.FragmentHomeBinding
import com.cureya.cure4mind.util.database
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import java.util.*

class HomeFragment : Fragment(), blogitemClicked {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var blogRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth

        binding.homeContextualMenu.setOnClickListener { showMenuPopUp(it) }
        binding.webLink.setOnClickListener {
            openweblink()
        }
        binding.fbLink.setOnClickListener {
            openfblink()
        }
        binding.linkedInLink.setOnClickListener {
            openlinkedinlink()
        }
        binding.ytLink.setOnClickListener {
            openytlink()
        }
        binding.instaLink.setOnClickListener {
            openinstalink()
        }
        binding.twitterLink.setOnClickListener {
            opentwitterlink()
        }
        binding.card.setOnClickListener{
            openplaystorelink()
        }
        binding.card1.setOnClickListener{
            openshareme()
        }

        binding.profile.setOnClickListener {
            val direction =
                HomeFragmentDirections.actionHomeFragmentToPersonalProfile(auth.uid!!)
            val cdirection = HomeFragmentDirections.actionHomeFragmentToCounsellorProfile(auth.uid!!)
            if(checkCounsellor()){
                findNavController().navigate(cdirection)
            }
            else findNavController().navigate(direction)
        }
        binding.moods.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_moodFragment)
        }
        getusername()
        initMembers(view)
        val images = listOf<blog>(
            blog(
                "Mental Health Disorders and How to Overcome",
                R.drawable.frame_546,
                "https://cureya.blogspot.com/2021/10/mental-health-disorders-how-to-overcome.html"
            ),
            blog(
                "What is Depression, Symptoms, Know all",
                R.drawable.frame_547,
                "https://cureya.blogspot.com/2022/01/what-is-depression-symptoms-know-all.html"
            ),
            blog(
                "Foods that Relieve Anxiety",
                R.drawable.frame_548,
                "https://cureya.blogspot.com/2022/01/foods-that-relieve-anxiety.html"
            ),
            blog(
                "Music & Our Mind",
                R.drawable.frame_549,
                "https://cureya.blogspot.com/2022/01/music-and-our-mind.html"
            ),
            blog(
                "Good Food Good Mood",
                R.drawable.frame_550,
                "https://cureya.blogspot.com/2022/01/good-food-good-mood.html"
            )
        )
        blogRecyclerView.layoutManager =
            LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
        blogRecyclerView.adapter = blogAdapter(this, images)
    }
    private fun openplaystorelink(){
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(requireContext(), Uri.parse("https://play.google.com/store/apps/details?id=com.cureya.cure4mind"))
    }

    private fun openshareme(){
        val intent= Intent()
        intent.action= Intent.ACTION_SEND
        intent.type = "text/plain"

        intent.putExtra(
            Intent.EXTRA_TEXT,
            "Check out this App : Cure4Mind https://play.google.com/store/apps/details?id=com.cureya.cure4mind")
        startActivity(Intent.createChooser(intent,"Share To:"))
    }
    private fun getusername() {
        val user = auth.currentUser
        val uid = user?.uid.toString()
        database.child("users").child(uid).get().addOnSuccessListener {
            Log.d("TAG", "getusername: ${it.getValue(Any::class.java)}")
            val username = it.child("name").value.toString()//.split(" ")[0]
            val photoUrl = it.child("photoUrl").value.toString()
            binding.userinfo.text = username
            binding.profile.load(photoUrl)
        }
    }
    private fun checkCounsellor() : Boolean{
        val user = auth.currentUser
        val uid = user?.uid.toString()
        var check : Boolean = false
        database.child("users").child(uid).get().addOnSuccessListener {
            check = it.child("counsellarstatus").value.toString().toBoolean()
        }
        return check
    }

    private fun opentwitterlink() {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(
            requireContext(),
            Uri.parse("https://twitter.com/CureyaR?t=9l3a2-Qx3EkMLD-4JYnFYw&s=09")
        )
    }

    private fun openinstalink() {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(
            requireContext(),
            Uri.parse("https://www.instagram.com/cureya.in/")
        )
    }

    private fun openytlink() {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(
            requireContext(),
            Uri.parse("https://youtube.com/channel/UCjsRwGm--mr1ADln5CB5Siw")
        )
    }

    private fun openlinkedinlink() {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(
            requireContext(),
            Uri.parse("https://www.linkedin.com/company/cureya")
        )
    }

    private fun openfblink() {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(requireContext(), Uri.parse("https://m.facebook.com/cureya7"))
    }

    private fun openweblink() {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(requireContext(), Uri.parse("https://www.cureya.in/"))
    }

    private fun initMembers(view: View) {
        blogRecyclerView = view.findViewById(R.id.blogs_recycler_view)
    }

    private fun showMenuPopUp(view: View) {
        PopupMenu(context, view).apply {
            setOnMenuItemClickListener { p0 ->
                when (p0?.itemId) {
                    R.id.settings -> {
                        findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
                        true
                    }
                    R.id.about_us -> {
                        findNavController().navigate(R.id.action_homeFragment_to_aboutUsFragment)
                        true
                    }
                    R.id.log_out -> {
                        auth.signOut()
                        findNavController().navigate(R.id.action_homeFragment_to_logInFragment)
                        true
                    }
                    R.id.profile -> {
                        val direction =
                            HomeFragmentDirections.actionHomeFragmentToPersonalProfile(auth.uid!!)
                        val cdirection = HomeFragmentDirections.actionHomeFragmentToCounsellorProfile(auth.uid!!)
                        if(checkCounsellor()){
                            findNavController().navigate(cdirection)
                        }
                        else findNavController().navigate(direction)
                        true
                    }
                    R.id.moods -> {
                        findNavController().navigate(R.id.action_homeFragment_to_moodFragment)
                        true
                    }
                    R.id.contact_us -> {
                      findNavController().navigate(R.id.action_homeFragment_to_contactUsFragment)
                        true
                    }
                    else -> false
                }
            }
            inflate(R.menu.home_contextual_menu)
            show()
        }

    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser == null) {
           findNavController().navigate(R.id.action_homeFragment_to_logInFragment)
        }
    }

    override fun onItemClicked(item: blog) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(requireContext(), Uri.parse(item.url))
    }
}