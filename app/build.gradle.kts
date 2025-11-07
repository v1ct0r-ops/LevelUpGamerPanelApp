// Plugins que usa Android Studio para compilar mi app
plugins {
    id("com.android.application")                      // Para crear apps de Android
    id("org.jetbrains.kotlin.android")                 // Para usar Kotlin (el lenguaje)
    id("org.jetbrains.kotlin.plugin.compose")          // Para usar Jetpack Compose (UI moderna)      
    id("org.jetbrains.kotlin.plugin.serialization")    // Para convertir objetos a JSON fácilmente
}


// Aquí configuro cómo se va a compilar mi app de Android
android {
    namespace = "com.example.levelupgamerpanel_app"  // El "apellido" único de mi app
    compileSdk = 35                                  // Versión del SDK que uso para compilar
    
    // Configuración básica de la app
    defaultConfig {
        applicationId = "com.example.levelupgamerpanel_app"  // ID único en Google Play Store
        minSdk = 24          // Versión mínima de Android que sí o sí necesito (Android 7)
        targetSdk = 35       // Versión de Android para la que optimicé la app
        versionCode = 1      // Número interno de versión (para updates)
        versionName = "1.0"  // Versión que ve el usuario
    }

    // Habilito Jetpack Compose para la interfaz
    buildFeatures { compose = true }

    // Configuración de Java (Kotlin necesita esto)
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17  // Uso Java 17 como base
        targetCompatibility = JavaVersion.VERSION_17
    }

    // Configuración específica de Compose
    composeOptions {
        kotlinCompilerExtensionVersion = "1.7.3"  // Versión del compilador de Compose
    }

    // Configuración de empaquetado (para evitar conflictos de archivos)
    packaging {
        resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" }  // Excluyo archivos de licencia duplicados
    }
}

kotlin { jvmToolchain(17) }

// Aquí listo todas las librerías externas que usa mi app (como "ingredientes" de una receta)
dependencies {
    // Librerías básicas de Compose
    implementation(libs.androidx.compose.foundation)           // Componentes base de Compose
    implementation(libs.androidx.compose.runtime.saveable)     // Para que Compose recuerde estados
    implementation(libs.play.services.location)               // Para usar GPS y ubicación
    
    // BOM (Bill of Materials) asegura que todas las librerías de Compose sean compatibles
    val composeBom = platform("androidx.compose:compose-bom:2024.10.01")
    implementation(composeBom)          // Para la app normal
    androidTestImplementation(composeBom)  // Para las pruebas

    // Librerías básicas de Android
    implementation("androidx.core:core-ktx:1.13.1")           // Extensiones de Kotlin para Android
    implementation("androidx.activity:activity-compose:1.9.3")  // Para integrar Compose con Activities

    // Librerías de interfaz de usuario (Compose)
    implementation("androidx.compose.ui:ui")                           // UI básica de Compose
    implementation("androidx.compose.foundation:foundation")           // Componentes base (botones, textos, etc.)
    implementation("androidx.compose.ui:ui-tooling-preview")          // Para ver previews en Android Studio
    debugImplementation("androidx.compose.ui:ui-tooling")             // Herramientas de desarrollo
    implementation("androidx.compose.material:material-icons-extended") // Iconos de Material Design

    // Material 3 (el tema visual más moderno de Google)
    implementation("androidx.compose.material3:material3:1.3.1")                        // Componentes Material 3
    implementation("androidx.compose.material3:material3-window-size-class:1.3.1")      // Para apps responsivas

    // Material Design clásico y compatibilidad
    implementation("com.google.android.material:material:1.12.0")  // Material Design tradicional
    implementation("androidx.appcompat:appcompat:1.7.0")          // Compatibilidad con versiones viejas

    // Gestión del ciclo de vida y ViewModels
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")      // Manejo del ciclo de vida
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.6")   // Integración con Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6") // ViewModels para Compose

    // Navegación entre pantallas
    implementation("androidx.navigation:navigation-compose:2.8.3")  // Para navegar entre pantallas

    // Almacenamiento local y datos
    implementation("androidx.datastore:datastore-preferences:1.1.1")  // Para guardar configuraciones
    implementation("io.coil-kt:coil-compose:2.7.0")                  // Para cargar imágenes de internet

    // Serialización JSON (convertir objetos a texto y viceversa)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.7.3")  // Core de serialización
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")  // Específico para JSON
    
    // Testing dependencies
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    testImplementation(kotlin("test"))
}
