package com.cureya.cure4mind.register

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cureya.cure4mind.R
import com.cureya.cure4mind.databinding.FragmentForgetPasswordBinding
import com.cureya.cure4mind.register.SignUpFragment.Companion.PASSWORD
import com.cureya.cure4mind.register.SignUpFragment.Companion.USER_LIST
import com.cureya.cure4mind.util.database
import com.cureya.cure4mind.util.shortToast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class ForgetPasswordFragment: Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentForgetPasswordBinding

    private var verifyId = ""
    private var key = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        autoChangeOtpEditTextFocus()

        binding.sendButton.setOnClickListener {
            validatePhoneNumber()
        }
    }

    /**
     * Execution WorkFlow:
     *
     * [validatePhoneNumber] -> [hasUserPassword] -> [sendOTP] -> [enableContinueButtonFunction] -> [signUpWithCredentials]
     *
     * [validatePhoneNumber]: this function checks whether the phone number entered
     * by the user exists/matches with Firebase database. It shifts to [hasUserPassword]
     * if any row data matches with the phone number provided.
     *
     * [hasUserPassword]: this function checks whether the user with the phone number
     * signed in with password. If there's no password in database that denotes user
     * signed in with google sign in, and therefore there's no need to change password and
     * shows a toast for the same. If there's an old existing password then it shifts to [sendOTP]
     * for otp verification.
     *
     * If all prerequisites are fulfilled the phone number with country code is verified in
     * [sendOTP] where variable [verifyId] is assigned with the string value provied by firebase
     * in onCodeSend(). Upon Receiving the OTP the continue button becomes visible in
     * [enableContinueButtonFunction] where user input is taken and credentials are stored.
     * Once that is not user is sign in with the credentials.
     */

    private fun hasUserPassword(key: String, phone: String) {

        database.child(USER_LIST).child(key).child(PASSWORD)
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value != null) {
                        val phoneWithCountry = COUNTRY_CODE_IND.plus(phone)
                        sendOTP(phoneWithCountry)
                    } else {
                        showToast("No password; User signed in with google account")
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error in retrieving password data", error.toException())
                }
            })
    }

    private fun validatePhoneNumber() {
        val phoneNumber = binding.edtPhone.text.toString().trim()

        database.child(USER_LIST).orderByChild("phone").equalTo(phoneNumber)
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value != null) {
                        for (childSnapshot in snapshot.children) {
                            key = childSnapshot.key!!
                        }
                        hasUserPassword(key, phoneNumber)
                        Log.i(TAG, "Retrieved key value: $key")
                    } else {
                        showToast("User with this Phone does not exist")
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error in retrieving phone number data", error.toException())
                }
            })
    }

    private fun sendOTP(phoneNumber: String) {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // verification is done automatically by firebase
                Log.i(TAG, "verification complete, credential: $credential")
                signUpWithCredentials(credential)
            }
            override fun onVerificationFailed(e: FirebaseException) {
                Log.w(TAG, "onVerificationFailed", e)

            }
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // manually handle verification if the process is not auto generated
                Log.i(TAG, "inside on code sent")
                verifyId = verificationId
                enableContinueButtonFunction()
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
        showToast("Redirecting to verify you are not a robot")
    }

    private fun enableContinueButtonFunction() {
        binding.continueButton.visibility = View.VISIBLE
        binding.continueButton.setOnClickListener {
            if (verifyId != "") {
                val userInput = this.getUserOtp()
                if (userInput.isNotEmpty()) {
                    val phoneAuthCredential = PhoneAuthProvider.getCredential(verifyId, userInput)
                    signUpWithCredentials(phoneAuthCredential)
                }
            } else {
                context?.shortToast("Please wait!")
            }
        }
    }

    private fun signUpWithCredentials(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                showToast("Success")
                goToChangePasswordFragment()
            }
            .addOnFailureListener {
                showToast("Unable to process request")
            }
    }

    private fun getUserOtp(): String {
        return binding.otpBoxOne.text.toString().trim()
            .plus(binding.otpBoxTwo.text.toString()).trim()
            .plus(binding.otpBoxThree.text.toString()).trim()
            .plus(binding.otpBoxFour.text.toString()).trim()
            .plus(binding.otpBoxFive.text.toString()).trim()
            .plus(binding.otpBoxSix.text.toString()).trim()
    }

    private fun autoChangeOtpEditTextFocus() {
        binding.otpBoxOne.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.otpBoxOne.text.isNullOrEmpty()) {
                    binding.otpBoxTwo.requestFocus()
                }
            }
        })
        binding.otpBoxTwo.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.otpBoxTwo.text.isNullOrEmpty()) {
                    binding.otpBoxThree.requestFocus()
                }
            }
        })
        binding.otpBoxThree.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.otpBoxThree.text.isNullOrEmpty()) {
                    binding.otpBoxFour.requestFocus()
                }
            }
        })
        binding.otpBoxFour.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.otpBoxFour.text.isNullOrEmpty()) {
                    binding.otpBoxFive.requestFocus()
                }
            }
        })
        binding.otpBoxFive.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.otpBoxFive.text.isNullOrEmpty()) {
                    binding.otpBoxSix.requestFocus()
                }
            }
        })
    }

    private fun goToChangePasswordFragment() {
        try {
            // to prevent crash from calling nav multiple times
            // in FirebaseDatabase callbacks
            this.findNavController().navigate(ForgetPasswordFragmentDirections
                .actionForgetPasswordFragmentToChangePasswordFragment(key))
        } catch (e: Exception) {
            Log.d(TAG, "Second time nav call aborted", e)
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(
            context,
            text,
            Toast.LENGTH_LONG
        ).show()
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
        private const val COUNTRY_CODE_IND = "+91"
        private const val TAG = "ForgetPasswordFragment"
    }
}