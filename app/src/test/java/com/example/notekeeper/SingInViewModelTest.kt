package com.example.notekeeper

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.notekeeper.ViewModel.SignInViewModel
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SingInViewModelTest {

    // Regla necessària perquè LiveData funcioni fora d'Android
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    //Creem un variable per poder utilizar el ViewModel
    private lateinit var viewModel: SignInViewModel

    // S'executa abans de cada test per tenir un ViewModel net
    @Before
    fun setUp() {
        viewModel = SignInViewModel()
    }

    @Test
    fun registre_campsBuits() {
        //Si tots el camps son buits
        viewModel.onSignInChanged("", "")

        //Ens mostra un error
        Assert.assertEquals("Tots els camps són obligatoris", viewModel.errorMessage.value)
    }

    @Test
    fun registre_correuInvalid() {
        //El correu té un format incorrecte
        viewModel.onSignInChanged( "akasha.com", "k@ramAk@asha12??")

        //Ens mostra un error
        Assert.assertEquals("Correu invàlid", viewModel.errorMessage.value)
    }

    @Test
    fun registre_contasenyaDebil(){

        //La contrasenya és dèbil
        viewModel.onSignInChanged("akasha@gmail.com","12345")

        Assert.assertEquals("Contrasenya dèbil", viewModel.errorMessage.value)
    }

    @Test
    fun registre_emailBuid(){
        //Si el email esta buid
        viewModel.onSignInChanged("", "k@ramAk@asha12??")

        //Ens mostra un error
        Assert.assertEquals("El email no pot estar buid", viewModel.errorMessage.value)
    }

    @Test
    fun registre_passwordBuid(){
        //Si el password esta buid
        viewModel.onSignInChanged("akasha@gmail.com", "")

        //Ens mostra un error
        Assert.assertEquals("El password no pot estar buid", viewModel.errorMessage.value)
    }

    @Test
    fun registre_dadesValides() {
        viewModel.registrarUsuari( "akasha@gmail.com", "k@ramAk@asha12??")

        Assert.assertEquals("Registre completat", viewModel.errorMessage.value)
    }

}