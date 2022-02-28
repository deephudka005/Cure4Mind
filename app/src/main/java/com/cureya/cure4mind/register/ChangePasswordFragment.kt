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
import com.cureya.cure4mind.register.SignUpFragment.Companion.PASSWORD
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

        binding.savePassButton.setOnClickListener {
            val password = binding.newPassEditText.text.toString().trim()
            val confirmPassword = binding.confirmPassEditText.text.toString().trim()

            if (validatePasswords(password, confirmPassword)) {
                retrieveDataAndSignInUser(password)
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
                        this@ChangePasswordFragment.newPassword = newPassword
                        signInCurrentUser(email, user.password!!)
                    } else {
                        Toast.makeText(context, "User does not exists", Toast.LENGTH_LONG).show()
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error in retrieving phone number data", error.toException())
                }
            })
        }
    } 

    // deleting the account that logged-in with phone auth
    // with different uid during manual otp validation
    private fun deletePhoneAuth() {
        auth.currentUser?.delete()
            ?.addOnSuccessListener {
                Log.i(TAG, "PhoneAuth deleted")
            }
            ?.addOnFailureListener {
                Log.e(TAG, "Error deleting phone error", it)
            }
    }

    private fun setNewPasswordToDatabase() {
        database.child(USER_LIST).child(key).child(PASSWORD).setValue(newPassword)
            .addOnSuccessListener {
                Log.i(TAG, "new password set to database")
            }
            .addOnFailureListener {
                Log.e(TAG, "error setting new password to database", it)
            }
    }

    // sign in with email and old password
    private fun signInCurrentUser(email: String, oldPassword: String) {
        auth.signInWithEmailAndPassword(email, oldPassword)
            .addOnSuccessListener {
                Log.i(TAG, "User signed in")
                updateNewPassword()
            }
            .addOnFailureListener {
                Log.e(TAG, "error signing in the user with email: $email, password: $oldPassword", it)
            }
    }

    private fun updateNewPassword() {
        try {
            val user = auth.currentUser!!
            user.updatePassword(newPassword)
                .addOnSuccessListener {
                    setNewPasswordToDatabase()
                    goToHomeFragment()
                    Log.i(TAG, "Password updated")
                }
                .addOnFailureListener {
                    Log.e(TAG, "error updating password: $newPassword", it)
                }
        } catch (e: Exception) {
            e.stackTrace
        }
    }

    private fun validatePasswords(password: String, confirmPassword: String) : Boolean {
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
                Log.d(TAG, "second time nav call aborted", e)
            }
        } else {
            Log.d(TAG, "can't navigate; user does not exists")
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