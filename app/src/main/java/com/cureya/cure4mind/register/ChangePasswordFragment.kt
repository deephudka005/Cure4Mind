package com.cureya.cure4mind.register

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.cureya.cure4mind.R
import com.cureya.cure4mind.databinding.FragmentForgotPasswordChangePassBinding
import com.cureya.cure4mind.model.User
import com.cureya.cure4mind.register.SignUpFragment.Companion.USER_LIST
import com.cureya.cure4mind.util.database
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class ChangePasswordFragment : Fragment() {

    private val auth = Firebase.auth
    private lateinit var binding: FragmentForgotPasswordChangePassBinding
    private val navArgument: ChangePasswordFragmentArgs by navArgs()
    private var oldPassword = ""
    private var newPassword = ""
    private val key by lazy {
        navArgument.key
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgotPasswordChangePassBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        deletePhoneAuth()

        val password = binding.newPassEditText.text.toString().trim()
        val confirmPassword = binding.confirmPassEditText.text.toString().trim()

        Log.d(TAG, "password emtry: ${password.isNotEmpty()}, lenght: ${password.length}, passwrod: $password, confirmPas: $confirmPassword")

        binding.savePassButton.setOnClickListener {
            // temp password; don't why editTexts always return null for validation
            if (validatePasswords("ronnieBine69", "ronnieBine69")) {
                retrieveDataAndSignInUser("ronnieBine69")
            } else {
                Toast.makeText(
                    context,
                    "Passwords field error",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun retrieveDataAndSignInUser(newPassword: String) {
        database.child(USER_LIST).child(key).apply {
            addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value != null) {
                        val user = snapshot.getValue(User::class.java)!!
                        val email = user.email!!
                        oldPassword = user.password!!
                        this@ChangePasswordFragment.newPassword = newPassword
                        this@apply.setValue(newPassword)
                        signInCurrentUser(email, oldPassword)
                    } else {
                        Toast.makeText(context, "User does not exists", Toast.LENGTH_LONG).show()
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("ChangePasswordFragment", "Error in retrieving phone number data", error.toException())
                }
            })
        }
    }

    // deleting the account that logged-in with phone auth
    // with different uid during manual otp validation
    private fun deletePhoneAuth() {
        auth.currentUser?.delete()
            ?.addOnCompleteListener {
                Log.i(TAG, "PhoneAuth deleted")
            }
            ?.addOnFailureListener {
                Log.e("ChangePasswordFragment", "Error deleting phone error", it)
            }
    }

    // sign in with email and new password
    private fun signInCurrentUser(email: String, oldPassword: String) {
        auth.signInWithEmailAndPassword(email, oldPassword)
            .addOnCompleteListener {
                Log.i("ChangePasswordFragment", "User signed in")
                updateNewPassword(newPassword)
            }
            .addOnFailureListener {
                Log.e("ChangePasswordFragment", "error signing in the user", it)
            }
    }

    private fun updateNewPassword(newPassword: String) {
        val user = auth.currentUser!!

        user.updatePassword(newPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    goToHomeFragment()
                    Log.i(TAG, "Password updated")
                } else {
                    Log.e(TAG, "Error password not updated")
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "error updating password", it)
            }
    }

    private fun validatePasswords(password: String, confirmPassword: String) : Boolean {
        Log.d(TAG, "password emtry: ${password.isNotEmpty()}, lenght: ${password.length}, passwrod: $password, confirmPas: $confirmPassword")
        return password.isNotEmpty() &&
                password.length > 7 &&
                password == confirmPassword
    }

    private fun goToHomeFragment() {
        if (auth.currentUser != null) {
            try {
                this.findNavController()
                    .navigate(R.id.action_changePasswordFragment_to_homeFragment)
            } catch (e: Exception) {
                Log.d("ChangePasswordFragment", "second time nav call aborted", e)
            }
        } else {
            Log.d("ChangePasswordFragment", "can't navigate; user does not exists")
        }
    }

    override fun onStart() {
        val bottomView = (activity as AppCompatActivity)
            .findViewById<BottomNavigationView>(R.id.nav_view)
        bottomView.visibility = View.GONE
        super.onStart()
    }

    override fun onStop() {
        val bottomView = (activity as AppCompatActivity)
            .findViewById<BottomNavigationView>(R.id.nav_view)
        bottomView.visibility = View.VISIBLE
        super.onStop()
    }

    companion object {
        private const val TAG = "ChangePasswordFragment"
    }
}