package com.example.triptrek

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log.d
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        SignUpButton.setOnClickListener {
            registerUser()
        }

        SignInButton.setOnClickListener {
            signinUser()
        }

        FacebookSignInButton.setOnClickListener {
            Toast.makeText(this, "This doesn't work currently. We're working on this", Toast.LENGTH_SHORT).show()
        }

        GmailSignInButton.setOnClickListener {
            Toast.makeText(this, "This doesn't work currently. We're working on this", Toast.LENGTH_SHORT).show()
        }

        TwitterSignInButton.setOnClickListener {
            Toast.makeText(this, "This doesn't work currently. We're working on this", Toast.LENGTH_SHORT).show()
        }
    }

    private fun registerUser()
    {
        val username = username.text.toString()
        val email = email.text.toString()
        val password = password.text.toString()

        if ( email.isEmpty() || password.isEmpty() )
        {
            Toast.makeText(this, "Please enter at least email address and password", Toast.LENGTH_SHORT).show()
            return
        }
        else
        {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if ( it.isSuccessful )
                        {
                            Toast.makeText(this, "User sign up successful", Toast.LENGTH_SHORT).show()
                            d("TT", "User created with email: $email, password: $password and user id: ${it.result?.user?.uid}")
                            FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
                                    ?.addOnCompleteListener {
                                        if ( it.isSuccessful )
                                        {
                                            Toast.makeText(this, "Email sent to $email. Please check and verify.", Toast.LENGTH_SHORT).show()
                                            d("TT", "Email sent")
                                        }
                                    }
                            //Add user to database
                            val user = FirebaseAuth.getInstance().currentUser
                            val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName(username).build()
                            user?.updateProfile(profileUpdates)
                            val phototmp = ""
                            val usertmp = User(username, email, phototmp)
                            val uid = FirebaseAuth.getInstance().uid
                            val ref = FirebaseDatabase.getInstance().getReference("users")
                            ref.child("$uid").setValue(usertmp)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "User $username added successfully", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this, "Failed to add user to database. ${it.message}", Toast.LENGTH_SHORT).show()
                                    }
                            val userintent = Intent(this, UserProfile::class.java)
                            startActivity(userintent)
                            return@addOnCompleteListener
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "User sign up failed. ${it.message}", Toast.LENGTH_SHORT).show()
                        d("TT", "User sign up failed. ${it.message}")
                        return@addOnFailureListener
                    }
        }

    }

    private fun signinUser()
    {
        val user = username.text.toString()
        val email = email.text.toString()
        val password = password.text.toString()

        if ( email.isEmpty() || password.isEmpty() )
        {
            Toast.makeText(this, "Please enter at least email address and password", Toast.LENGTH_SHORT).show()
            return
        }
        else
        {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if ( it.isSuccessful )
                        {
                            Toast.makeText(this, "User sign in successful", Toast.LENGTH_SHORT).show()
                            d("TT", "User signed in with email: $email, password: $password and user id: ${it.result?.user?.uid}")
                            val userintent = Intent(this, UserProfile::class.java)
                            startActivity(userintent)
                            return@addOnCompleteListener
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "User sign in failed. ${it.message}", Toast.LENGTH_SHORT).show()
                        d("TT", "User sign in failed. ${it.message}")
                        return@addOnFailureListener
                    }
        }
    }

}

class User(val username: String, val email: String, val photo: String)
{
    constructor() : this("", "", "")
    public fun getphotourl(): String
    {
        return photo
    }
}