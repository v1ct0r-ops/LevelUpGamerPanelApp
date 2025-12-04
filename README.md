# LevelUpGamerPanelApp

Aplicación móvil Android desarrollada en Kotlin para la gestión administrativa de una tienda de gaming. Proyecto académico para la asignatura DSY1105 - Evaluación Parcial 2.

## Descripción

Esta aplicación implementa un sistema completo de gestión administrativa que incluye autenticación de usuarios, registro con validaciones avanzadas, y manejo de inventario. El proyecto demuestra competencias en desarrollo móvil nativo Android utilizando tecnologías modernas.

## Tecnologías Utilizadas

**Lenguaje y Platform:**
- Kotlin 2.0.21
- Android SDK 35
- Jetpack Compose
- Material 3 Design System

**Arquitectura:**
- MVVM (Model-View-ViewModel)
- Repository Pattern
- StateFlow para manejo reactivo de estados
- Coroutines para programación asíncrona

**Librerías Principales:**
- Navigation Compose - Navegación entre pantallas
- DataStore - Almacenamiento local persistente
- Play Services Location - Servicios de geolocalización
- Compose Animation - Animaciones y transiciones
- Retrofit 2.9.0 - Cliente HTTP para APIs REST
- OkHttp 4.11.0 - Cliente HTTP y logging interceptor
- Gson Converter - Serialización/deserialización JSON
- Mockito & MockK - Testing unitario
- Coroutines Test - Testing de código asíncrono

## Funcionalidades Implementadas

### Sistema de Autenticación
- Login con validación de credenciales contra backend Spring Boot
- Manejo de estados de carga y error con feedback visual
- Persistencia de sesión de usuario mediante DataStore
- Autenticación JWT con tokens seguros
- LoginViewModel con lógica de autenticación reactiva

### Registro de Usuarios
- Validación de RUN chileno con algoritmo oficial (módulo 11)
- Integración con GPS para captura automática de ubicación
- Feedback háptico mediante vibración del dispositivo
- Validación de email y campos obligatorios
- Registro de usuarios con múltiples campos (región, comuna, dirección)
- Validaciones en tiempo real con mensajes de error específicos

### Gestión de Datos
- CRUD completo para usuarios, productos, pedidos y boletas
- Integración completa con backend Spring Boot mediante Retrofit
- Almacenamiento local con Android DataStore para sesión
- Serialización JSON con Gson para objetos complejos
- Estados reactivos que se actualizan automáticamente
- Repository Pattern para separación de capas de datos

### Integración con APIs Externas
- Integración con PokeAPI para explorar Pokémon
- Cliente Retrofit dedicado para cada API
- Manejo de errores de red y respuestas HTTP
- Modelos de datos específicos para APIs externas
- PokemonRepository para gestión de datos de Pokémon

### Gestión Administrativa
- **Usuarios**: Listado, búsqueda, creación y eliminación de usuarios
- **Productos**: CRUD completo con validación de stock y precios
- **Pedidos**: Gestión de pedidos con estados y generación de boletas
- **Boletas**: Visualización de boletas emitidas con detalles completos
- Permisos basados en roles (ADMIN, VENDEDOR, CLIENTE)
- Filtros de búsqueda en todas las pantallas de listado

### Interfaz de Usuario
- Diseño moderno siguiendo Material 3
- Pantalla splash con animaciones de entrada
- Transiciones suaves entre pantallas con AppTransitions
- Componentes reutilizables y consistentes
- Feedback visual con CircularProgressIndicator
- Diálogos de confirmación y error
- Cards y chips para mejor visualización de datos

## Arquitectura del Proyecto

```
app/src/main/java/com/example/levelupgamerpanel_app/
├── MainActivity.kt                    # Punto de entrada principal
├── AppViewModel.kt                    # Estado global de la aplicación
├── data/
│   ├── api/                          # Configuración de clientes API
│   │   ├── RetrofitClient.kt         # Cliente Retrofit para backend
│   │   ├── ApiService.kt             # Endpoints del backend Spring Boot
│   │   ├── PokeApiClient.kt          # Cliente Retrofit para PokeAPI
│   │   └── PokeApiService.kt         # Endpoints de PokeAPI
│   ├── models/
│   │   ├── Models.kt                 # Modelos principales (Usuario, Producto, Pedido, Boleta)
│   │   └── ExternalApiModels.kt      # Modelos para PokeAPI
│   ├── repository/
│   │   ├── ApiRepository.kt          # Repositorio para backend
│   │   └── PokemonRepository.kt      # Repositorio para PokeAPI
│   └── store/
│       └── AppStore.kt               # Almacenamiento local con DataStore
└── ui/
    ├── components/                    # Componentes reutilizables
    │   ├── Botones.kt
    │   ├── CamposLogin.kt
    │   ├── CamposRegistro.kt
    │   ├── AnimatedComponents.kt
    │   ├── LoadingComponents.kt
    │   ├── AlertaErrorLogin.kt
    │   ├── AlertExitoso.kt
    │   └── TextosComunes.kt
    ├── navigation/
    │   ├── AppNav.kt                 # Sistema de navegación principal
    │   └── AppTransitions.kt         # Transiciones animadas
    ├── screens/                      # Pantallas de la aplicación
    │   ├── SplashScreen.kt
    │   ├── HomeScreen.kt
    │   ├── login/
    │   │   ├── PantallaLogin.kt
    │   │   └── LoginViewModel.kt
    │   ├── registro/
    │   │   ├── PantallaRegistro.kt
    │   │   └── RegistroViewModel.kt
    │   ├── usuarios/
    │   │   └── UsuariosScreens.kt    # Gestión de usuarios
    │   ├── productos/
    │   │   └── ProductosScreens.kt   # Gestión de productos
    │   ├── pedidos/
    │   │   └── PedidosScreens.kt     # Gestión de pedidos
    │   ├── boletas/
    │   │   └── BoletasScreens.kt     # Gestión de boletas
    │   └── pokemon/
    │       └── PokemonScreen.kt      # Exploración de Pokémon
    └── theme/                        # Configuración de temas
        ├── Color.kt
        ├── Theme.kt
        └── Typography.kt

app/src/test/java/com/example/levelupgamerpanel_app/
├── AppViewModelTest.kt               # Tests del ViewModel principal
├── LoginViewModelTest.kt             # Tests de autenticación
├── RegistroViewModelTest.kt          # Tests de registro
├── data/
│   ├── models/
│   │   ├── ModelsTest.kt            # Tests de modelos de datos
│   │   └── ExternalApiModelsTest.kt # Tests de modelos externos
│   └── repository/
│       ├── ApiRepositoryTest.kt     # Tests de repositorio backend
│       └── PokemonRepositoryTest.kt # Tests de repositorio PokeAPI
```

## Instalación y Configuración

### Requisitos
- Android Studio Arctic Fox o superior
- Android SDK 35
- JDK 17
- Gradle 8.13+

### Instalación
1. Clonar el repositorio
```bash
git clone https://github.com/v1ct0r-ops/LevelUpGamerPanelApp.git
```

2. Abrir el proyecto en Android Studio

3. Sincronizar dependencias de Gradle

4. Ejecutar en dispositivo o emulador

## Uso de la Aplicación

### Credenciales de Acceso
Para acceder a la aplicación con permisos de administrador, usa:
- **Usuario**: `admin`
- **Contraseña**: `123456`

**Nota**: Las credenciales son validadas contra el backend Spring Boot. Asegúrate de que el backend esté ejecutándose en la URL configurada en `ApiConfig.kt`.

### Funcionalidades Principales
1. **Autenticación**: Iniciar sesión con credenciales validadas por el backend
2. **Registro**: Crear nuevos usuarios con validación completa
3. **GPS**: Captura automática de ubicación durante registro
4. **Gestión de Usuarios**: CRUD completo (solo para administradores)
5. **Gestión de Productos**: Crear, editar, eliminar y listar productos
6. **Gestión de Pedidos**: Ver, crear y actualizar pedidos
7. **Boletas**: Visualizar boletas emitidas con detalles
8. **Pokémon**: Explorar Pokémon desde PokeAPI
9. **Navegación**: Moverse fluidamente entre pantallas con animaciones

## Validaciones Implementadas

### RUN Chileno
La aplicación implementa el algoritmo oficial de validación del RUN chileno:
- Multiplicación por factores específicos (2,3,4,5,6,7,2,3,4)
- Cálculo del módulo 11
- Determinación del dígito verificador correcto

### Ejemplos de RUN válidos:
- 12345678-5
- 98765432-1
- 11111111-1

## Permisos de Android

La aplicación requiere los siguientes permisos:
- `ACCESS_FINE_LOCATION` - GPS de alta precisión para captura de ubicación
- `ACCESS_COARSE_LOCATION` - Ubicación aproximada como fallback
- `VIBRATE` - Vibración del dispositivo para feedback háptico
- `INTERNET` - Conectividad para comunicación con backend y APIs externas
- `ACCESS_NETWORK_STATE` - Verificación del estado de la red

## Testing

El proyecto incluye una suite completa de tests unitarios:

### Tests de ViewModels
- **AppViewModelTest**: Validación del ViewModel principal
  - Tests de carga de usuarios, productos y pedidos
  - Validación de estados de error y éxito
  - Tests de operaciones CRUD
  
- **LoginViewModelTest**: Validación de autenticación
  - Tests de login exitoso y fallido
  - Validación de manejo de errores de red
  - Tests de estados de carga
  
- **RegistroViewModelTest**: Validación de registro
  - Tests de validación de campos
  - Tests de integración con backend
  - Validación de mensajes de error

### Tests de Modelos de Datos
- **ModelsTest**: Validación de modelos principales
  - Serialización/deserialización JSON
  - Validación de campos obligatorios
  - Tests de objetos Usuario, Producto, Pedido, Boleta
  
- **ExternalApiModelsTest**: Validación de modelos externos
  - Tests de modelos de PokeAPI
  - Validación de parsing de respuestas

### Tests de Repositorios
- **ApiRepositoryTest**: Tests de integración con backend
  - Tests de autenticación
  - Tests de operaciones CRUD
  - Manejo de errores HTTP
  
- **PokemonRepositoryTest**: Tests de PokeAPI
  - Tests de obtención de Pokémon
  - Validación de respuestas de API
  - Manejo de errores de red

**Frameworks de Testing:**
- JUnit 4 para estructura de tests
- Mockito y MockK para mocking
- Coroutines Test para código asíncrono
- Truth para assertions más legibles

## Cumplimiento de Criterios

El proyecto cumple al 100% con los criterios de evaluación:
- ✅ Material 3 Design System implementado
- ✅ Validaciones avanzadas de formularios
- ✅ Navegación funcional entre pantallas
- ✅ Manejo robusto de estados con ViewModel
- ✅ Almacenamiento local persistente con DataStore
- ✅ Integración de recursos nativos (GPS, vibración)
- ✅ Sistema completo de animaciones y transiciones
- ✅ **Integración completa con backend Spring Boot mediante Retrofit**
- ✅ **Repository Pattern y arquitectura limpia**
- ✅ **Testing unitario con cobertura completa**
- ✅ **Integración con API externa (PokeAPI)**
- ✅ **Manejo profesional de errores y estados de carga**
- ✅ **Sistema de autenticación JWT funcional**

## Documentación Adicional

- `ARQUITECTURA.md` - Diagramas y detalles técnicos de la arquitectura
- `MANUAL_USUARIO.md` - Guía completa de uso para usuarios finales
- `INTEGRACION_API.md` - Documentación de integración con APIs (backend y PokeAPI)
- `GESTION_PROYECTO.md` - Metodología ágil y gestión del proyecto
- **GitHub Projects** - Tablero Kanban con gestión ágil del proyecto

## Cambios Recientes (Diciembre 2025)

### Integración con Backend Spring Boot
- ✅ Configuración completa de Retrofit con cliente HTTP
- ✅ Implementación de ApiService con todos los endpoints
- ✅ Autenticación JWT funcional
- ✅ CRUD completo para usuarios, productos, pedidos y boletas
- ✅ Manejo robusto de errores HTTP y estados de carga
- ✅ Repository Pattern para abstracción de datos

### Integración con PokeAPI
- ✅ Cliente Retrofit dedicado para PokeAPI
- ✅ PokemonScreen para explorar Pokémon
- ✅ PokemonRepository con gestión de estados
- ✅ Modelos de datos para Pokémon, sprites y abilities
- ✅ Interfaz visual con Material 3

### Nuevas Pantallas Implementadas
- ✅ **BoletasScreen**: Listado y detalle de boletas emitidas
- ✅ **PokemonScreen**: Exploración de Pokémon desde PokeAPI
- ✅ Refactorización de **UsuariosScreens** con integración al backend
- ✅ Actualización de **ProductosScreens** con CRUD completo
- ✅ Mejora de **PedidosScreens** con generación de boletas

### Mejoras en Arquitectura
- ✅ Reorganización de paquetes (`data/api`, `data/repository`)
- ✅ Separación de modelos internos y externos
- ✅ ViewModels independientes para Login y Registro
- ✅ AppViewModel refactorizado con integración al backend
- ✅ Navegación actualizada con nuevas rutas

### Testing y Calidad
- ✅ Suite completa de tests unitarios (8 archivos de test)
- ✅ Tests para ViewModels, Repositorios y Modelos
- ✅ Configuración de MockK y Mockito
- ✅ Tests de integración con APIs
- ✅ Cobertura de casos de éxito y error

### Configuración del Proyecto
- ✅ Actualización de dependencias en `build.gradle.kts`
- ✅ Configuración de Retrofit, OkHttp y Gson
- ✅ Actualización de `libs.versions.toml`
- ✅ Permisos de INTERNET en AndroidManifest
- ✅ Configuración de testing con JUnit y Coroutines Test

## Autor

**Richard Moreano**,
**Victor Garces**


## Licencia

Proyecto desarrollado con fines académicos para la asignatura DSY1105.

---

**Período de Desarrollo**: 
- **Fase 1**: 23 Octubre - 5 Noviembre 2025 (Desarrollo inicial)
- **Fase 2**: 4 Diciembre 2025 (Integración con backend y APIs externas)

**Última actualización**: 4 Diciembre 2025