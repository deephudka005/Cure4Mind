package com.cureya.cure4mind.register

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cureya.cure4mind.R
import com.cureya.cure4mind.databinding.FragmentLogInBinding
import com.cureya.cure4mind.model.User
import com.cureya.cure4mind.register.SignUpFragment.Companion.TAG
import com.cureya.cure4mind.register.SignUpFragment.Companion.USER_LIST
import com.cureya.cure4mind.util.database
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.util.*

class LogInFragment : Fragment() {

    private lateinit var binding: FragmentLogInBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLogInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode== Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d(ContentValues.TAG, "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.e(ContentValues.TAG, "Google sign in failed", e)
                }
            }
        }

        auth = Firebase.auth

        binding.apply {

            logIn.setOnClickListener { prelogin() }
            googleLogIn.setOnClickListener { launchSignInIntent() }
            register.setOnClickListener { goToSignUpFragment() }
            forgetPassword.setOnClickListener { goToForgetPassFragment() }
        }
    }
    private fun prelogin(){
        if (areTextFieldsValid(binding.edtLogInEmail, binding.edtLogInPassword)) {
            handleLogIn()
        }
    }
    private fun handleLogIn() {
        val email = binding.edtLogInEmail.text.toString().trim()
        val password = binding.edtLogInPassword.text.toString().trim()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    goToHomeFragment()
                }
            }
            .addOnFailureListener {
                when (it.message) {
                    WRONG_PASSWORD_ERROR -> showToast("Password Incorrect")
                    USER_VOID_ERROR -> showToast("User isn't registered with us")
                    else -> showToast("Please check your credentials")
                }
                Log.e("LogInFragment", "Log in failure", it)
            }
    }

    private fun areTextFieldsValid(
        emailFiled: EditText,
        passwordField: EditText
    ) : Boolean {

        val emailFieldCheck = isTextFieldEmpty(emailFiled.text.toString())

        val passwordFieldCheck = isTextFieldEmpty(passwordField.text.toString())

        if (!emailFieldCheck) {
            emailFiled.error = "Please enter a valid email address"
            emailFiled.requestFocus()
        }
        if (!passwordFieldCheck) {
            passwordField.error = "Password should be at least 8 characters long"
            passwordField.requestFocus()
        }
        return emailFieldCheck && passwordFieldCheck
    }

    private fun isTextFieldEmpty(text: String): Boolean {
        return text.isNotEmpty() && text.isNotBlank()
    }

    private fun launchSignInIntent() {
        val signInIntent = googleSignInClient.signInIntent
        activityResultLauncher.launch(signInIntent)
//        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN) {
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            try {
//                // Google Sign In was successful, authenticate with Firebase
//                val account = task.getResult(ApiException::class.java)!!
//                Log.d(ContentValues.TAG, "firebaseAuthWithGoogle:" + account.id)
//                firebaseAuthWithGoogle(account.idToken!!)
//            } catch (e: ApiException) {
//                // Google Sign In failed, update UI appropriately
//                Log.e(ContentValues.TAG, "Google sign in failed", e)
//            }
//        }
//    }

    override fun onStart() {
        super.onStart()
        updateUI()
    }

    override fun onResume() {
        super.onResume()
        val bottomView = (activity as AppCompatActivity)
            .findViewById<BottomNavigationView>(R.id.nav_view)
        bottomView.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        val bottomView = (activity as AppCompatActivity)
            .findViewById<BottomNavigationView>(R.id.nav_view)
        bottomView.visibility = View.VISIBLE
    }

    private fun showToast(text: String) {
        Toast.makeText(
            context,
            text,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun updateUI() {
        val currentUser = auth.currentUser
        if(currentUser != null) {
            goToHomeFragment()
        }
    }

    private fun goToHomeFragment() {
        try {
            // to prevent crash from calling nav multiple times
            // in FirebaseDatabase callbacks
            findNavController().navigate(R.id.action_logInFragment_to_homeFragment)
        } catch (e: Exception) {
            Log.d(TAG, "Second time nav call aborted", e)
        }
    }

    private fun goToSignUpFragment() = findNavController().navigate(R.id.action_logInFragment_to_signUpFragment)

    private fun goToForgetPassFragment() = findNavController().navigate(R.id.action_logInFragment_to_forgetPasswordFragment)

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = User(
                        auth.currentUser?.displayName,
                        auth.currentUser?.email,
                        null,
                        auth.currentUser?.photoUrl.toString(),
                        null,
                        null,
                        false,
                        joinedCureya = Date()
                    )
                    addToUserBase(user)
                    googleSignInClient.revokeAccess()
                } else {
                    showToast("Unexpected error occurred")
                }
            }
    }

    private fun addToUserBase(user: User) {
        val newChildKey = auth.currentUser?.uid!!

       database.child(USER_LIST).child(newChildKey).apply {
            addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value == null) {
                        this@apply.setValue(user)
                        Log.w(TAG, "New user inserted to database")
                    } else Log.w(TAG, "User already exists")
                    updateUI()
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "inside addToUserList()", error.toException())
                }
            })
        }
    }

    companion object {
        private const val WRONG_PASSWORD_ERROR =
            "The password is invalid or the user does not have a password."
        private const val USER_VOID_ERROR =
            "There is no user record corresponding to this identifier. The user may have been deleted."
    }
}