package com.cureya.cure4mind.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.cureya.cure4mind.R
import com.cureya.cure4mind.databinding.FragmentSignUpBinding
import com.cureya.cure4mind.model.User
import com.cureya.cure4mind.util.database
import com.cureya.cure4mind.util.defaultProfilePic
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class SignUpFragment: Fragment() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        binding.btnRegister.setOnClickListener { prepareToRegister() }

        binding.googleSignIn.setOnClickListener { handleGoogleSignIn() }

        binding.logInTextView.setOnClickListener { goToLogInFragment() }

        handleCheckBox()
    }

    private fun prepareToRegister() {
        if (areTextFieldsValid(binding.nameEditText, binding.edtLogInEmail, binding.edtLogInPassword, binding.edtPhone)) {
            register()
        }
    }

    private fun register() {
        val name = binding.nameEditText.text.toString().trim()
        val email = binding.edtLogInEmail.text.toString().trim()
        val phone = binding.edtPhone.text.toString().trim()
        val password = binding.edtLogInPassword.text.toString().trim()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val gender =
                        if (binding.checkboxMale.isChecked) {
                            "Male"
                        } else if (binding.checkboxFemale.isChecked) {
                            "Female"
                        } else "LGBTQIA"
                    Log.w("SignUpFragment","Firebase auth successful")
                    val user = User(name, email, phone, defaultProfilePic, password, gender,false)
                    addToUserBase(user)
                }
            }
            .addOnFailureListener {
                if (it.message == USER_EXISTS_ERROR) {
                    showToast("User is already registered")
                }
                Log.e("SignUpFragment", "Sing up failure", it)
            }
    }

    private fun areTextFieldsValid(
        nameField: EditText,
        emailFiled: EditText,
        phoneField: EditText,
        passwordField: EditText
    ) : Boolean {
        val nameFieldCheck = isTextFieldEmpty(nameField.text.toString())

        val phoneFieldCheck = isTextFieldEmpty(phoneField.text.toString())

        val emailFieldCheck = isTextFieldEmpty(emailFiled.text.toString()) &&
                isEmailValid(emailFiled.text.toString())

        val passwordFieldCheck = isTextFieldEmpty(passwordField.text.toString()) &&
                isPasswordLong(passwordField.text.toString())

        if (!nameFieldCheck) {
            nameField.error = "Please enter your name"
            nameField.requestFocus()
        }
        if (!emailFieldCheck) {
            emailFiled.error = "Please enter a valid email address"
            emailFiled.requestFocus()
        }
        if (!phoneFieldCheck) {
            phoneField.error = "Please enter a valid email address"
            phoneField.requestFocus()
        }
        if (!passwordFieldCheck) {
            passwordField.error = "Password should be at least 8 characters long"
            passwordField.requestFocus()
        }
        return nameFieldCheck && emailFieldCheck && passwordFieldCheck && phoneFieldCheck
    }

    private fun isTextFieldEmpty(text: String): Boolean {
        return text.isNotEmpty() && text.isNotBlank()
    }

    private fun isEmailValid(email: String) = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun isPasswordLong(str: String) = str.length > 7

    private fun addToUserBase(user: User) {
        val newChildKey = auth.currentUser?.uid!!

        database.child(USER_LIST).child(newChildKey).apply {
            addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value == null) {
                        this@apply.setValue(user)
                        Log.w(TAG, "New user inserted to database")
                    } else Log.w(TAG, "User already exists")
                    updateUI()
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Inside addToUserList()", error.toException())
                }
            })
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun handleGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity() ,gso)
        signIn()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                if (isRequiredSelected()) {
                    firebaseAuthWithGoogle(account.idToken!!)
                } else {
                    showToast("Gender and phone number are required")
                }
            } catch (e: ApiException) {

                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun isRequiredSelected() : Boolean {
        val checkBoxClick =
            binding.checkboxMale.isChecked || binding.checkboxFemale.isChecked || binding.checkboxLgbtqia.isChecked
        return checkBoxClick && binding.edtPhone.text!!.isNotEmpty()
    }

    private fun updateUI() {
        val currentUser = auth.currentUser
        if(currentUser != null) {
            goToHomeFragment()
        } else {
            showToast("Please register to continue")
        }
    }

    private fun goToLogInFragment() = findNavController().navigate(R.id.action_signUpFragment_to_logInFragment)

    private fun goToHomeFragment() {
        try {
            // to prevent crash from calling nav multiple times
            // in FirebaseDatabase callbacks
            findNavController().navigate(R.id.action_signUpFragment_to_homeFragment)
        } catch (e: Exception) {
            Log.d(TAG, "Second time nav call aborted", e)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val gender =
                        if (binding.checkboxMale.isChecked) {
                            "Male"
                        } else if (binding.checkboxFemale.isChecked) {
                            "Female"
                        } else "LGBTQIA"

                    val user = User(
                        auth.currentUser?.displayName,
                        auth.currentUser?.email,
                        binding.edtPhone.text.toString(),
                        auth.currentUser?.photoUrl.toString(),
                        null,
                        gender,
                        false
                    )
                    addToUserBase(user)
                    googleSignInClient.revokeAccess()
                } else {
                    showToast("Unexpected error occurred")
                }
            }
    }

    private fun handleCheckBox() {
        binding.checkboxMale.setOnCheckedChangeListener { _, p1 ->
            if (p1) {
                binding.checkboxFemale.isChecked = false
                binding.checkboxLgbtqia.isChecked = false
            }
        }
        binding.checkboxFemale.setOnCheckedChangeListener { _, p1 ->
            if (p1) {
                binding.checkboxMale.isChecked = false
                binding.checkboxLgbtqia.isChecked = false
            }
        }
        binding.checkboxLgbtqia.setOnCheckedChangeListener { _, p1 ->
            if (p1) {
                binding.checkboxMale.isChecked = false
                binding.checkboxFemale.isChecked = false
            }
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(
            context,
            text,
            Toast.LENGTH_LONG
        ).show()
        return
    }

    override fun onStart() {
        super.onStart()
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

    companion object {
        const val RC_SIGN_IN = 100
        const val USER_LIST = "users"
        const val EMAIL = "email"
        const val PASSWORD = "password"
        const val TAG = "SignUpFragment"
        const val USER_EXISTS_ERROR =
            "The email address is already in use by another account."
    }
}