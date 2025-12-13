package com.example.levelupgamerpanel_app

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.levelupgamerpanel_app.ui.screens.registro.RegistroViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RegistroViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var mockApp: Application
    private lateinit var viewModel: RegistroViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        mockkStatic(android.util.Log::class)
        every { android.util.Log.d(any(), any()) } returns 0
        every { android.util.Log.e(any(), any()) } returns 0
        every { android.util.Log.i(any(), any()) } returns 0
        every { android.util.Log.w(any(), any<String>()) } returns 0
        
        mockApp = mockk(relaxed = true)
        viewModel = RegistroViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `estado inicial - todos los campos vacios`() {
        val state = viewModel.ui.value
        assertEquals("", state.run)
        assertEquals("", state.nombres)
        assertEquals("", state.apellidos)
        assertEquals("", state.correo)
        assertEquals("", state.fechaNacimiento)
        assertEquals("Administrador", state.tipoUsuario)
        assertEquals("", state.region)
        assertEquals("", state.comuna)
        assertEquals("", state.direccion)
        assertEquals("", state.password)
        assertEquals("", state.confirmar)
        assertEquals("", state.referido)
        assertNull(state.error)
        assertFalse(state.isCreating)
        assertFalse(viewModel.isCreating.value)
    }

    @Test
    fun `estado inicial - ningun campo tiene error`() {
        val state = viewModel.ui.value
        assertNull(state.runError)
        assertNull(state.nombresError)
        assertNull(state.apellidosError)
        assertNull(state.correoError)
        assertNull(state.fechaError)
        assertNull(state.regionError)
        assertNull(state.comunaError)
        assertNull(state.direccionError)
        assertNull(state.passwordError)
    }

    @Test
    fun `onRun - convierte a mayusculas y limpia error`() {
        viewModel.onRun("12345678k")
        assertEquals("12345678K", viewModel.ui.value.run)
        assertNull(viewModel.ui.value.error)
    }

    @Test
    fun `onNombres - actualiza el campo`() {
        viewModel.onNombres("Juan")
        assertEquals("Juan", viewModel.ui.value.nombres)
    }

    @Test
    fun `onApellidos - actualiza el campo`() {
        viewModel.onApellidos("Pérez")
        assertEquals("Pérez", viewModel.ui.value.apellidos)
    }

    @Test
    fun `onCorreo - actualiza el campo`() {
        viewModel.onCorreo("juan@duoc.cl")
        assertEquals("juan@duoc.cl", viewModel.ui.value.correo)
    }

    @Test
    fun `onFecha - actualiza el campo`() {
        viewModel.onFecha("15-03-1995")
        assertEquals("15-03-1995", viewModel.ui.value.fechaNacimiento)
    }

    @Test
    fun `onTipo - actualiza el campo`() {
        viewModel.onTipo("CLIENTE")
        assertEquals("CLIENTE", viewModel.ui.value.tipoUsuario)
    }

    @Test
    fun `onRegion - actualiza el campo`() {
        viewModel.onRegion("Metropolitana")
        assertEquals("Metropolitana", viewModel.ui.value.region)
    }

    @Test
    fun `onComuna - actualiza el campo`() {
        viewModel.onComuna("Santiago")
        assertEquals("Santiago", viewModel.ui.value.comuna)
    }

    @Test
    fun `onDireccion - actualiza el campo`() {
        viewModel.onDireccion("Calle 123")
        assertEquals("Calle 123", viewModel.ui.value.direccion)
    }

    @Test
    fun `onPassword - actualiza el campo`() {
        viewModel.onPassword("password123")
        assertEquals("password123", viewModel.ui.value.password)
    }

    @Test
    fun `onConfirmar - actualiza el campo`() {
        viewModel.onConfirmar("password123")
        assertEquals("password123", viewModel.ui.value.confirmar)
    }

    @Test
    fun `onReferido - actualiza el campo`() {
        viewModel.onReferido("11111111K")
        assertEquals("11111111K", viewModel.ui.value.referido)
    }

    @Test
    fun `validarCampos - RUN vacio genera error`() {
        viewModel.onRun("")
        val validado = viewModel.validarCampos()
        assertNotNull(validado.runError)
        assertEquals("El RUN es obligatorio", validado.runError)
    }

    @Test
    fun `validarCampos - RUN invalido genera error`() {
        viewModel.onRun("123")
        val validado = viewModel.validarCampos()
        assertNotNull(validado.runError)
    }

    @Test
    fun `validarCampos - RUN valido con digito K`() {
        viewModel.onRun("111111111")  // RUN válido con dígito K
        viewModel.onNombres("Juan")
        viewModel.onApellidos("Pérez")
        viewModel.onCorreo("juan@duoc.cl")
        viewModel.onFecha("15-03-1995")
        viewModel.onTipo("CLIENTE")
        viewModel.onRegion("Metropolitana")
        viewModel.onComuna("Santiago")
        viewModel.onDireccion("Calle 123")
        viewModel.onPassword("password123")
        viewModel.onConfirmar("password123")
        
        val validado = viewModel.validarCampos()
        assertNull(validado.runError)
    }

    @Test
    fun `validarCampos - RUN valido con digito numerico`() {
        viewModel.onRun("111111111")  // RUN válido sin guión
        viewModel.onNombres("Juan")
        viewModel.onApellidos("Pérez")
        viewModel.onCorreo("juan@duoc.cl")
        viewModel.onFecha("15-03-1995")
        viewModel.onTipo("CLIENTE")
        viewModel.onRegion("Metropolitana")
        viewModel.onComuna("Santiago")
        viewModel.onDireccion("Calle 123")
        viewModel.onPassword("password123")
        viewModel.onConfirmar("password123")
        
        val validado = viewModel.validarCampos()
        assertNull(validado.runError)
    }

    @Test
    fun `validarCampos - nombres vacio genera error`() {
        viewModel.onNombres("")
        val validado = viewModel.validarCampos()
        assertNotNull(validado.nombresError)
        assertEquals("Los nombres son obligatorios", validado.nombresError)
    }

    @Test
    fun `validarCampos - nombres muy cortos genera error`() {
        viewModel.onNombres("A")
        val validado = viewModel.validarCampos()
        assertNotNull(validado.nombresError)
        assertEquals("Solo letras, mínimo 2 caracteres", validado.nombresError)
    }

    @Test
    fun `validarCampos - nombres validos`() {
        viewModel.onRun("11111111K")
        viewModel.onNombres("Juan Carlos")
        viewModel.onApellidos("Pérez")
        viewModel.onCorreo("juan@duoc.cl")
        viewModel.onFecha("15-03-1995")
        viewModel.onTipo("CLIENTE")
        viewModel.onRegion("Metropolitana")
        viewModel.onComuna("Santiago")
        viewModel.onDireccion("Calle 123")
        viewModel.onPassword("password123")
        viewModel.onConfirmar("password123")
        
        val validado = viewModel.validarCampos()
        assertNull(validado.nombresError)
    }

    @Test
    fun `validarCampos - apellidos vacio genera error`() {
        viewModel.onApellidos("")
        val validado = viewModel.validarCampos()
        assertNotNull(validado.apellidosError)
        assertEquals("Los apellidos son obligatorios", validado.apellidosError)
    }

    @Test
    fun `validarCampos - apellidos validos`() {
        viewModel.onRun("11111111K")
        viewModel.onNombres("Juan")
        viewModel.onApellidos("Pérez González")
        viewModel.onCorreo("juan@duoc.cl")
        viewModel.onFecha("15-03-1995")
        viewModel.onTipo("CLIENTE")
        viewModel.onRegion("Metropolitana")
        viewModel.onComuna("Santiago")
        viewModel.onDireccion("Calle 123")
        viewModel.onPassword("password123")
        viewModel.onConfirmar("password123")
        
        val validado = viewModel.validarCampos()
        assertNull(validado.apellidosError)
    }

    @Test
    fun `validarCampos - correo vacio genera error`() {
        viewModel.onCorreo("")
        val validado = viewModel.validarCampos()
        assertNotNull(validado.correoError)
        assertEquals("El correo es obligatorio", validado.correoError)
    }

    @Test
    fun `validarCampos - correo sin arroba genera error`() {
        viewModel.onCorreo("juanduoc.cl")
        val validado = viewModel.validarCampos()
        assertNotNull(validado.correoError)
    }

    @Test
    fun `validarCampos - correo duoc valido`() {
        viewModel.onRun("11111111K")
        viewModel.onNombres("Juan")
        viewModel.onApellidos("Pérez")
        viewModel.onCorreo("juan.perez@duoc.cl")
        viewModel.onFecha("15-03-1995")
        viewModel.onTipo("CLIENTE")
        viewModel.onRegion("Metropolitana")
        viewModel.onComuna("Santiago")
        viewModel.onDireccion("Calle 123")
        viewModel.onPassword("password123")
        viewModel.onConfirmar("password123")
        
        val validado = viewModel.validarCampos()
        assertNull(validado.correoError)
    }

    @Test
    fun `validarCampos - correo profesor duoc valido`() {
        viewModel.onRun("11111111K")
        viewModel.onNombres("Juan")
        viewModel.onApellidos("Pérez")
        viewModel.onCorreo("juan@profesor.duoc.cl")
        viewModel.onFecha("15-03-1995")
        viewModel.onTipo("CLIENTE")
        viewModel.onRegion("Metropolitana")
        viewModel.onComuna("Santiago")
        viewModel.onDireccion("Calle 123")
        viewModel.onPassword("password123")
        viewModel.onConfirmar("password123")
        
        val validado = viewModel.validarCampos()
        assertNull(validado.correoError)
    }

    @Test
    fun `validarCampos - correo gmail valido`() {
        viewModel.onRun("11111111K")
        viewModel.onNombres("Juan")
        viewModel.onApellidos("Pérez")
        viewModel.onCorreo("juan@gmail.com")
        viewModel.onFecha("15-03-1995")
        viewModel.onTipo("CLIENTE")
        viewModel.onRegion("Metropolitana")
        viewModel.onComuna("Santiago")
        viewModel.onDireccion("Calle 123")
        viewModel.onPassword("password123")
        viewModel.onConfirmar("password123")
        
        val validado = viewModel.validarCampos()
        assertNull(validado.correoError)
    }

    @Test
    fun `validarCampos - fecha vacia genera error`() {
        viewModel.onFecha("")
        val validado = viewModel.validarCampos()
        assertNotNull(validado.fechaError)
        assertEquals("La fecha es obligatoria", validado.fechaError)
    }

    @Test
    fun `validarCampos - fecha formato invalido genera error`() {
        viewModel.onFecha("1995-03-15")
        val validado = viewModel.validarCampos()
        assertNotNull(validado.fechaError)
    }

    @Test
    fun `validarCampos - fecha valida`() {
        viewModel.onRun("11111111K")
        viewModel.onNombres("Juan")
        viewModel.onApellidos("Pérez")
        viewModel.onCorreo("juan@duoc.cl")
        viewModel.onFecha("15-03-1995")
        viewModel.onTipo("CLIENTE")
        viewModel.onRegion("Metropolitana")
        viewModel.onComuna("Santiago")
        viewModel.onDireccion("Calle 123")
        viewModel.onPassword("password123")
        viewModel.onConfirmar("password123")
        
        val validado = viewModel.validarCampos()
        assertNull(validado.fechaError)
    }

    @Test
    fun `validarCampos - password vacio genera error`() {
        viewModel.onPassword("")
        val validado = viewModel.validarCampos()
        assertNotNull(validado.passwordError)
        assertEquals("La contraseña es obligatoria", validado.passwordError)
    }

    @Test
    fun `validarCampos - password muy corto genera error`() {
        viewModel.onPassword("12345")
        val validado = viewModel.validarCampos()
        assertNotNull(validado.passwordError)
        assertEquals("Mínimo 6 caracteres, debe incluir letras y números", validado.passwordError)
    }

    @Test
    fun `validarCampos - password valido`() {
        viewModel.onRun("11111111K")
        viewModel.onNombres("Juan")
        viewModel.onApellidos("Pérez")
        viewModel.onCorreo("juan@duoc.cl")
        viewModel.onFecha("15-03-1995")
        viewModel.onTipo("CLIENTE")
        viewModel.onRegion("Metropolitana")
        viewModel.onComuna("Santiago")
        viewModel.onDireccion("Calle 123")
        viewModel.onPassword("password123")
        viewModel.onConfirmar("password123")
        
        val validado = viewModel.validarCampos()
        assertNull(validado.passwordError)
    }

    @Test
    fun `validarCampos - confirmar diferente de password genera error`() {
        viewModel.onPassword("password123")
        viewModel.onConfirmar("different")
        val validado = viewModel.validarCampos()
        assertNotNull(validado.confirmarError)
        assertEquals("Las contraseñas no coinciden", validado.confirmarError)
    }

    @Test
    fun `validarCampos - confirmar igual a password`() {
        viewModel.onRun("11111111K")
        viewModel.onNombres("Juan")
        viewModel.onApellidos("Pérez")
        viewModel.onCorreo("juan@duoc.cl")
        viewModel.onFecha("15-03-1995")
        viewModel.onTipo("CLIENTE")
        viewModel.onRegion("Metropolitana")
        viewModel.onComuna("Santiago")
        viewModel.onDireccion("Calle 123")
        viewModel.onPassword("password123")
        viewModel.onConfirmar("password123")
        
        val validado = viewModel.validarCampos()
        assertNull(validado.passwordError)
        assertNull(validado.confirmarError)
    }

    @Test
    fun `validarCampos - region vacia genera error`() {
        viewModel.onRegion("")
        val validado = viewModel.validarCampos()
        assertNotNull(validado.regionError)
        assertEquals("La región es obligatoria", validado.regionError)
    }

    @Test
    fun `validarCampos - comuna vacia genera error`() {
        viewModel.onComuna("")
        val validado = viewModel.validarCampos()
        assertNotNull(validado.comunaError)
        assertEquals("La comuna es obligatoria", validado.comunaError)
    }

    @Test
    fun `validarCampos - direccion vacia genera error`() {
        viewModel.onDireccion("")
        val validado = viewModel.validarCampos()
        assertNotNull(validado.direccionError)
        assertEquals("La dirección es obligatoria", validado.direccionError)
    }

    @Test
    fun `puedeCrear - retorna false cuando hay campos vacios`() {
        assertFalse(viewModel.puedeCrear())
    }

    @Test
    fun `puedeCrear - retorna false cuando passwords no coinciden`() {
        viewModel.onRun("111111111")
        viewModel.onNombres("Juan")
        viewModel.onApellidos("Pérez")
        viewModel.onCorreo("juan@duoc.cl")
        viewModel.onFecha("15-03-1995")
        viewModel.onTipo("CLIENTE")
        viewModel.onRegion("Metropolitana")
        viewModel.onComuna("Santiago")
        viewModel.onDireccion("Calle 123")
        viewModel.onPassword("password123")
        viewModel.onConfirmar("different")
        
        assertFalse(viewModel.puedeCrear())
    }

    @Test
    fun `puedeCrear - retorna true cuando todos los campos son validos`() {
        viewModel.onRun("111111111")
        viewModel.onNombres("Juan")
        viewModel.onApellidos("Pérez")
        viewModel.onCorreo("juan@duoc.cl")
        viewModel.onFecha("15-03-1995")
        viewModel.onTipo("CLIENTE")
        viewModel.onRegion("Metropolitana")
        viewModel.onComuna("Santiago")
        viewModel.onDireccion("Calle 123")
        viewModel.onPassword("password123")
        viewModel.onConfirmar("password123")
        
        assertTrue(viewModel.puedeCrear())
    }

    @Test
    fun `crearCuenta - llama onError cuando hay campos invalidos`() = runTest {
        var errorCalled = false
        var errorMessage = ""
        
        viewModel.crearCuenta(
            onOk = { },
            onError = { msg ->
                errorCalled = true
                errorMessage = msg
            }
        )
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertTrue(errorCalled)
        assertEquals("Por favor corrige los errores en el formulario", errorMessage)
    }

    @Test
    fun `crearCuenta - actualiza estado de UI con errores de validacion`() = runTest {
        viewModel.onRun("")  // RUN vacío
        viewModel.onNombres("A")  // Nombre muy corto
        
        viewModel.crearCuenta(
            onOk = { },
            onError = { }
        )
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        val state = viewModel.ui.value
        assertNotNull(state.runError)
        assertNotNull(state.nombresError)
    }
}