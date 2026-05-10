package com.example.notekeeper

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.notekeeper.ViewModel.SignIn
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignInTest {

    @Before
    fun setUp() {
        launchFragmentInContainer<SignIn>(themeResId = R.style.Theme_NoteKeeper)
    }

    @Test
    fun registre_dadesValides() {
        // Escriu email vàlid
        onView(withId(R.id.email)).perform(typeText("test@gmail.com"), closeSoftKeyboard())
        // Escriu contrasenya vàlida
        onView(withId(R.id.password)).perform(typeText("Test@123#"), closeSoftKeyboard())
        // Prem el botó Registrar
        onView(withId(R.id.btnRegistrar)).perform(click())

        // Comprova que el missatge és correcte
        onView(withId(R.id.tvMissatgeError)).check(matches(withText("Registre completat")))
    }

    @Test
    fun registre_emailBuit() {
        // Email buit
        onView(withId(R.id.email)).perform(typeText(""), closeSoftKeyboard())
        // Contrasenya correcta
        onView(withId(R.id.password)).perform(typeText("Test@123#"), closeSoftKeyboard())
        // Prem Registrar
        onView(withId(R.id.btnRegistrar)).perform(click())

        // Comprova el missatge d'error
        onView(withId(R.id.tvMissatgeError)).check(matches(withText("Tots els camps són obligatoris")))
    }

    @Test
    fun registre_passwordBuid() {
        // Email correcte
        onView(withId(R.id.email)).perform(typeText("test@gmail.com"), closeSoftKeyboard())
        // Contrasenya buida
        onView(withId(R.id.password)).perform(typeText(""), closeSoftKeyboard())
        // Prem Registrar
        onView(withId(R.id.btnRegistrar)).perform(click())

        // Missatge esperat
        onView(withId(R.id.tvMissatgeError)).check(matches(withText("Tots els camps són obligatoris")))
    }

    @Test
    fun registre_correuInvalid() {
        // Email sense format correcte
        onView(withId(R.id.email)).perform(typeText("correu-sense-arroba"), closeSoftKeyboard())
        // Contrasenya correcta
        onView(withId(R.id.password)).perform(typeText("Test@123#"), closeSoftKeyboard())
        // Prem Registrar
        onView(withId(R.id.btnRegistrar)).perform(click())

        // Comprovació del missatge
        onView(withId(R.id.tvMissatgeError)).check(matches(withText("Correu invàlid")))
    }

    @Test
    fun registre_contasenyaDebil() {
        // Email correcte
        onView(withId(R.id.email)).perform(typeText("test@gmail.com"), closeSoftKeyboard())
        // Contrasenya massa curta
        onView(withId(R.id.password)).perform(typeText("123"), closeSoftKeyboard())
        // Prem Registrar
        onView(withId(R.id.btnRegistrar)).perform(click())

        // Missatge esperat
        onView(withId(R.id.tvMissatgeError)).check(matches(withText("Contrasenya dèbil")))
    }
}
