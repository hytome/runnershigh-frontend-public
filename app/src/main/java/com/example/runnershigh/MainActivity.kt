package com.example.runnershigh

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.runnershigh.navigation.AppNavGraph
import com.example.runnershigh.ui.theme.RunnersHighTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import androidx.activity.result.contract.ActivityResultContracts
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Firebase Auth
        auth = Firebase.auth

        // Google Sign-In 옵션 설정
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Firebase 콘솔에서 자동생성되는 값
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            RunnersHighTheme {
                val navController = rememberNavController()
                AppNavGraph(
                    navController = navController,
                    onGoogleLoginClick = {
                        // 여기서 실제 구글 로그인 화면 띄움
                        googleSignInLauncher.launch(googleSignInClient.signInIntent)
                    }
                )
            }
        }
    }
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    // 구글 로그인 결과를 받는 런처
    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // TODO: 로그인 성공 처리 (예: 다른 화면으로 이동)
                        } else {
                            // TODO: 실패 처리 (스낵바, 토스트 등)
                        }
                    }
            } catch (e: ApiException) {
                // TODO: 예외 처리
            }
        }
}
