package daniel.brian.ecommerceapp.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import daniel.brian.ecommerceapp.util.Constants.INTRODUCTION_SP
import javax.inject.Singleton

@Module // defines how dependencies are provided and constructed within a Hilt enabled App
@InstallIn(SingletonComponent::class) // ensures that there is only on instance of FirebaseAuth within the application
object AppModule {
    @Provides // defines a provider function which in this case is the provideFirebaseAuth()
    @Singleton // ensures there is only one instance of the FirebaseAuth
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    // dependency for firebase firestore database
    @Provides
    @Singleton
    fun provideFirebaseFirestoreDatabase() = Firebase.firestore

    @Provides
    // not using the singleton because we're only going to use it in the introduction fragment
    fun provideIntroductionSP(
        application: Application
        // passing the Mode as private since only our app will be able to use this preference
    ): SharedPreferences = application.getSharedPreferences(INTRODUCTION_SP,MODE_PRIVATE)

}
