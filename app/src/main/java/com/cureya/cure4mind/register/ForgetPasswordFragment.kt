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
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cureya.cure4mind.R
import com.cureya.cure4mind.databinding.FragmentForgetPasswordBinding
import com.cureya.cure4mind.register.SignUpFragment.Companion.USER_LIST
import com.cureya.cure4mind.util.database
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
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
    private var varifyId = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        binding.continueButton.setOnClickListener {
            if (varifyId.isNotEmpty()) {
                verifyOtp(varifyId)
            }
        }
    }

    private fun validatePhoneNumber() {
        val phoneNumber = binding.edtPhone.text.toString().trim()

        database.child(USER_LIST).orderByChild("phone").equalTo(phoneNumber)
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value != null) {
                        val phoneWithCountry = "+91".plus(phoneNumber)
                        sendOTP(phoneWithCountry)
                    } else {
                        showToast("User with this Phone does not exists")
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
                Log.d(TAG, "onVerificationCompleted:$credential")
                showToast("Verification completed")
                goToHomeFragment()
            }
            override fun onVerificationFailed(e: FirebaseException) {
                Log.w(TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }
            }
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(verificationId, token)
                // verifyOtp(verificationId)
                Log.d(TAG, "onCodeSent:$verificationId")
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

    private fun verifyOtp(verifyID: String) {
        val otpInput = binding.otpBoxOne.text.toString()
            .plus(binding.otpBoxTwo.text.toString())
            .plus(binding.otpBoxThree.text.toString())
            .plus(binding.otpBoxFour.text.toString())
            .plus(binding.otpBoxFive.text.toString())
            .plus(binding.otpBoxSix.text.toString())

        val credential = PhoneAuthProvider.getCredential(verifyID, otpInput)
        if (credential != null) {
            goToHomeFragment()
        }
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

    /* private fun signInWithCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener {
            try {
                // to prevent crash from calling nav multiple times
                // in FirebaseDatabase callbacks
                findNavController().navigate(R.id.action_forgetPasswordFragment_to_homeFragment)
            } catch (e: Exception) {
                Log.e(SignUpFragment.TAG, "Second time nav call aborted", e)
            }
        }
    } */

    private fun goToHomeFragment() {
        try {
            // to prevent crash from calling nav multiple times
            // in FirebaseDatabase callbacks
            findNavController().navigate(R.id.action_forgetPasswordFragment_to_homeFragment)
        } catch (e: Exception) {
            Log.e(SignUpFragment.TAG, "Second time nav call aborted", e)
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
        const val EMAIL_CHILD = "email"
        private const val TAG = "ForgetPasswordFragment"
    }
}