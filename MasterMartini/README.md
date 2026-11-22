# 1. MasterMartini (Grupo 10 - DSY1105 Desarrollo de Aplicaciones Móviles, Duoc UC)

MasterMartini es una app móvil hecha en **Kotlin + Jetpack Compose** para negocios de panadería y pastelería y sus clientes. Permite registrar un negocio, revisar un catálogo de recetas, ubicar distribuidores y compartir recetas en un recetario comunitario que se conecta a una API externa.

Está armada con arquitectura MVVM (`model`, `data`, `viewmodel`, `ui`), Navigation Compose, DataStore para guardar datos locales y Retrofit para consumir la API REST.

# 2. Integrantes

- Paola Franco
- Oliver Duncan

# 3. Funcionalidades

- **Registro de negocio:** validación de RUT, correo y teléfono, selección de rubro y foto con la cámara.
- **Login y sesión persistente:** se ingresa con RUT y correo, comparados con el negocio registrado en el dispositivo. La sesión se guarda con DataStore y la app abre directo en Inicio si ya hay una sesión activa.
- **Catálogo de recetas:** filtro por categoría y pantalla de detalle con insumos y pasos.
- **Favoritos:** se guardan con DataStore y se ven en su propia pestaña.
- **Distribuidores:** ubicación del usuario con GPS y listado de sucursales.
- **Perfil editable:** muestra los datos del negocio, permite editarlos y cerrar sesión.
- **Recetario Comunitario (CRUD completo):** listar, crear, editar y eliminar recetas contra la API externa. Categoría y dificultad se eligen desde menús desplegables.

# 4. Endpoints utilizados (API externa)

El Recetario Comunitario consume una API externa en **MockAPI.io**, por medio de Retrofit (`RetrofitInstance` y `RecetaApiService`). No hay un microservicio propio en el proyecto.

**URL base:** `https://6afe63113a0e70bc8d5367db.mockapi.io/api/v1/`

| Método | Ruta | Uso |
|--------|------|-----|
| `GET` | `recetas` | Listar recetas |
| `GET` | `recetas/{id}` | Obtener una receta |
| `POST` | `recetas` | Crear receta |
| `PUT` | `recetas/{id}` | Actualizar receta |
| `DELETE` | `recetas/{id}` | Eliminar receta |

**Campos del modelo `RecetaRemote`:**

| Campo | Tipo |
|-------|------|
| `id` | `String` |
| `titulo` | `String` |
| `descripcion` | `String` |
| `categoria` | `String` |
| `tiempoMinutos` | `Int` |
| `dificultad` | `String` |

# 5. Pasos para ejecutar

1. Clonar el repositorio y abrir la carpeta en Android Studio.
2. Esperar a que Gradle termine de sincronizar.
3. Ejecutar las pruebas:
   ```bash
   ./gradlew test                    # pruebas unitarias (app/src/test)
   ./gradlew connectedAndroidTest    # pruebas de interfaz (requiere emulador o dispositivo)
   ```
   Las pruebas de interfaz están en `app/src/androidTest/java/com/example/mastermartini/MasterMartiniUiTest.kt`.
4. Compilar la app: ejecutar `app` en un emulador o dispositivo (minSdk 24), o generar el APK de debug con `./gradlew assembleDebug`. Se necesita internet para el Recetario Comunitario.

# 6. Captura del APK firmado y `.jks`

El APK firmado (`release`) se generó con el asistente de Android Studio (`Build > Generate Signed App Bundle / APK...`) usando un almacén de claves local.

- **APK firmado:** `app/release/app-release.apk`
- **Archivo de firma:** `mastermartini_key.jks`. Por seguridad no se sube al repositorio, la clave y sus contraseñas se mantienen privadas.

![APK firmado y Keystore](docs/captura_apk_jks.png)
