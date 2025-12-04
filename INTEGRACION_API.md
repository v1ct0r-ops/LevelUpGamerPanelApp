# LevelUpGamer Panel App - Configuración API

## Cambios Realizados

La aplicación Android ha sido actualizada para consumir las APIs reales del backend Spring Boot en lugar de usar datos locales.

### Archivos Nuevos Creados

1. **`data/api/ApiService.kt`**: Define todos los endpoints de la API REST.
2. **`data/api/RetrofitClient.kt`**: Cliente singleton de Retrofit para hacer peticiones HTTP.
3. **`data/api/ApiConfig.kt`**: Configuración centralizada de URLs y timeouts.
4. **`data/repository/ApiRepository.kt`**: Repositorio que maneja JWT y llamadas a la API.

### Archivos Modificados

1. **`data/models/Models.kt`**: Modelos actualizados para coincidir con el backend.
2. **`AppViewModel.kt`**: Ahora usa la API en lugar de datos locales.
3. **`build.gradle.kts`**: Agregadas dependencias de Retrofit y Gson.

## Configuración

### 1. Cambiar entre Desarrollo y Producción

Edita el archivo `data/api/ApiConfig.kt`:

```kotlin
// Modo de desarrollo: true = local, false = producción
private const val IS_DEVELOPMENT = false  // Cambia a true para desarrollo local
```

### 2. URLs Disponibles

- **Producción:** `https://levelup-gamer-backend.up.railway.app/api/v1/`
- **Emulador Local:** `http://10.0.2.2:8080/api/v1/`
- **Dispositivo Físico:** `http://192.168.1.X:8080/api/v1/` (cambia la IP)

### 3. Probar en Dispositivo Físico

Si pruebas en un celular real conectado a la misma red WiFi que tu PC:

1. Encuentra la IP de tu PC:
   - Windows: `ipconfig` en CMD
   - Mac/Linux: `ifconfig` en Terminal

2. Actualiza `ApiConfig.kt`:
   ```kotlin
   private const val URL_LOCAL_DEVICE = "http://TU_IP_AQUI:8080/api/v1/"
   ```

3. Cambia el modo:
   ```kotlin
   private const val IS_DEVELOPMENT = true
   val BASE_URL: String
       get() = if (IS_DEVELOPMENT) {
           URL_LOCAL_DEVICE  // <-- Descomentar esta línea
       } else {
           URL_PRODUCTION
       }
   ```

## Funcionalidades Implementadas

### Autenticación
- ✅ Login con JWT
- ✅ Almacenamiento seguro del token
- ✅ Logout
- ✅ Sesión persistente

### Productos
- ✅ Listar productos
- ✅ Crear producto
- ✅ Actualizar producto
- ✅ Eliminar producto
- ✅ Buscar productos
- ✅ Productos con stock crítico

### Usuarios
- ✅ Ver perfil
- ✅ Listar usuarios
- ✅ Actualizar perfil

### Pedidos
- ✅ Ver pedidos
- ✅ Cambiar estado de pedido

## Verificar si el Backend Está Funcionando

### Opción 1: Desde el navegador
Abre en tu navegador:
```
https://levelup-gamer-backend.up.railway.app/actuator/health
```

Si ves algo como `{"status":"UP"}`, ¡el backend funciona!

### Opción 2: Desde terminal
```bash
curl https://levelup-gamer-backend.up.railway.app/actuator/health
```

### Opción 3: Probar el login
```bash
curl -X POST https://levelup-gamer-backend.up.railway.app/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"correo":"admin@levelup.cl","password":"admin123"}'
```

Si obtienes un token JWT, ¡todo funciona perfectamente!

## Si Railway NO Funciona

### Alternativa Rápida: ngrok (Recomendado para pruebas)

1. **Descarga ngrok:** https://ngrok.com/download

2. **Inicia tu backend local:**
   ```bash
   cd backend-spring
   mvn spring-boot:run
   ```

3. **En otra terminal, inicia ngrok:**
   ```bash
   ngrok http 8080
   ```

4. **Copia la URL HTTPS** que muestra ngrok (ejemplo: `https://abc123.ngrok-free.app`)

5. **Actualiza ApiConfig.kt:**
   ```kotlin
   private const val URL_PRODUCTION = "https://abc123.ngrok-free.app/api/v1/"
   ```

6. **Recompila y ejecuta la app**

¡Ahora cualquier dispositivo puede acceder a tu backend!

### Alternativa Permanente: Render.com

1. Crea cuenta en https://render.com
2. New → Web Service
3. Conecta tu repositorio GitHub
4. Configuración:
   - Build Command: `cd backend-spring && mvn clean package -DskipTests`
   - Start Command: `java -jar backend-spring/target/*.jar`
   - Environment: `SPRING_PROFILES_ACTIVE=prod`
5. Deploy

URL resultante: `https://levelup-gamer-backend.onrender.com/api/v1/`

## 1. Iniciar Sesión

Usa las credenciales del backend:

- **Admin:**
  - Correo: `admin@levelup.cl`
  - Password: `admin123`

- **Vendedor:**
  - Correo: `vendedor@levelup.cl`
  - Password: `vendedor123`

### 2. Gestión de Productos

Desde el panel de productos puedes:
- Ver lista completa
- Agregar nuevos productos
- Editar productos existentes
- Eliminar productos
- Buscar por nombre

### 3. Gestión de Pedidos

Desde el panel de pedidos puedes:
- Ver todos los pedidos
- Cambiar estado (pendiente, despachado, cancelado)
- Ver detalles de cada pedido

## Solución de Problemas

### Error de Conexión

Si obtienes "Error de conexión":

1. **Verifica que el backend esté corriendo:**
   ```bash
   curl https://levelup-gamer-backend.up.railway.app/actuator/health
   ```

2. **Si usas desarrollo local:**
   - Asegúrate de que el backend esté corriendo en el puerto 8080
   - Usa la IP correcta (10.0.2.2 para emulador, tu IP real para dispositivo físico)

### Error 401 - No Autorizado

- El token JWT expiró (duración: 24 horas)
- Cierra sesión y vuelve a iniciar

### Error 403 - Prohibido

- Tu usuario no tiene permisos para esa acción
- Verifica que uses un usuario admin o vendedor según la acción

### Datos no se cargan

1. Verifica los logs en Logcat (filtro: `OkHttp` o `Retrofit`)
2. Revisa que `ENABLE_LOGGING = true` en `ApiConfig.kt`
3. Verifica que el token se guardó correctamente

## Estructura del Código

```
app/src/main/java/com/example/levelupgamerpanel_app/
├── data/
│   ├── api/
│   │   ├── ApiConfig.kt          # Configuración de URLs
│   │   ├── ApiService.kt         # Definición de endpoints
│   │   └── RetrofitClient.kt     # Cliente HTTP
│   ├── models/
│   │   └── Models.kt             # Modelos de datos
│   ├── repository/
│   │   └── ApiRepository.kt      # Lógica de API y JWT
│   └── store/
│       └── AppStore.kt           # (Ya no se usa, mantener por compatibilidad)
├── AppViewModel.kt               # ViewModel principal
└── MainActivity.kt               # Actividad principal
```

## Próximos Pasos

- [ ] Agregar manejo de errores más detallado
- [ ] Implementar refresh token automático
- [ ] Agregar caché de datos offline
- [ ] Implementar registro de nuevos usuarios desde la app
- [ ] Agregar paginación para listas grandes

## Notas Importantes

1. **Seguridad:**
   - El token JWT se guarda en DataStore (encriptado por Android)
   - Todas las peticiones incluyen el token en el header Authorization
   - Los datos sensibles no se guardan en logs de producción

2. **Performance:**
   - Los datos se cargan automáticamente al iniciar sesión
   - Se recargan después de cada operación CRUD
   - Usa Flows para actualización reactiva de UI

3. **Compatibilidad:**
   - Requiere Android 7.0 (API 24) o superior
   - Funciona en emuladores y dispositivos físicos
   - Compatible con todas las versiones del backend

---

**Desarrollado para el Proyecto Semestral Full-Stack Level-Up Gamer**  
*Android + Kotlin + Retrofit + JWT + Spring Boot*
