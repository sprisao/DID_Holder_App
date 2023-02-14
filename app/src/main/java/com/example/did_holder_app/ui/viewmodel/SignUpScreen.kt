package com.example.did_holder_app.ui.viewmodel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.did_holder_app.data.api.RetrofitInstance
import com.example.did_holder_app.data.model.VC.SignUpRequest
import com.example.did_holder_app.data.model.VC.SignUpResponse
import com.example.did_holder_app.util.DidDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import timber.log.Timber

@Composable
fun SignUpScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = DidDataStore(context)

    Column() {

        Text(text = "SignUpScreen")
        UserSignupScreen(onSignup = { user ->
            scope.launch {
                signupUser(user, dataStore, scope)
            }
        })
    }
}

private fun signupUser(request: SignUpRequest, dataStore: DidDataStore, scope: CoroutineScope) {
    val call = RetrofitInstance.vcServerApi.createUser(request)
    call.enqueue(object : retrofit2.Callback<SignUpResponse> {
        override fun onResponse(
            call: Call<SignUpResponse>,
            response: Response<SignUpResponse>
        ) {
            if (response.isSuccessful) {
                Timber.d("Success")
                Timber.d(response.body().toString())
                /*get userseq from SignUpRequest */
                val userseq = response.body()?.data?.userseq
                scope.launch { dataStore.saveUserseq(userseq!!) }
            } else {
                println("Error")
            }
        }

        override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
            println(t.message)
        }

    })
}


@Composable
fun UserSignupScreen(onSignup: (SignUpRequest) -> Unit) {
    var userid: String by remember { mutableStateOf("androidTest") }
    var userpass: String by remember { mutableStateOf("androidTest1") }
    var username: String by remember { mutableStateOf("android") }
    var userphoneno: String by remember { mutableStateOf("01000000000") }
    var jobposition: String by remember { mutableStateOf("android") }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        OutlinedTextField(
            value = userid,
            onValueChange = { userid = it },
            label = { Text("User ID") },
            singleLine = true
        )

        OutlinedTextField(
            value = userpass,
            onValueChange = { userpass = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("User Name") },
            singleLine = true
        )

        OutlinedTextField(
            value = userphoneno.toString(),
            onValueChange = { userphoneno = it },
            label = { Text("Phone Number") },
            singleLine = true
        )

        OutlinedTextField(
            value = jobposition,
            onValueChange = { jobposition = it },
            label = { Text("Job Position") },
            singleLine = true
        )


        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                Timber.d("Sign Up")
                val user = SignUpRequest(userid, userpass, username, userphoneno, jobposition)
                Timber.d(user.toString())
                onSignup(user)
            }
        ) {
            Text("Sign Up")
        }
    }
}