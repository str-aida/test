# Gestia

# Módulo: Autenticación y Gestión de Usuarios

## Objetivo

Este documento describe el funcionamiento del módulo de autenticación y gestión de usuarios desde el punto de vista del Frontend.

Toda la información aquí documentada corresponde a los endpoints implementados actualmente en el backend.

---

# Índice

- Flujo general
- Setup del establecimiento
- Login
- Logout
- Registro de Administrador
- Registro de Cliente
- Recuperación de contraseña
- Gestión del perfil
- Gestión de usuarios
- Manejo del JWT

---

# Flujo General

## Primera ejecución del sistema

```text
Pantalla Bienvenida
        │
        ▼
Setup del establecimiento
POST /setup/establecimiento
        │
        ▼
Registro del Administrador
POST /auth/registro-admin
        │
        ▼
Login
POST /auth/login
        │
        ▼
Dashboard Administrador
```

> **Importante**
>
> El Setup solamente puede realizarse una única vez al instalar el sistema.

---

# Setup del Establecimiento

## Descripción

Permite registrar la información del establecimiento donde funcionará Gestia.

Este endpoint se ejecuta únicamente durante la instalación inicial del sistema.

Una vez realizado correctamente, el sistema habilita el registro del primer administrador.

---

## Endpoint

```http
POST /setup/establecimiento
```

---

## Quién puede utilizarlo

Público (solo durante la instalación inicial).

---

## Request

```json
{
  "nombre": "Mi Restaurante",
  "razonSocial": "Mi Restaurante SRL",
  "cuit": "20123456789",
  "email": "contacto@mirestaurante.com",
  "telefono": "1169875432",
  "direccion": {
    "nombre": "Local",
    "calle": "Av. Belgrano",
    "numero": "1234",
    "localidad": "Avellaneda",
    "piso": "",
    "departamento": "",
    "codigoPostal": "1870",
    "referencia": "Frente a la plaza"
  },
  "horarioApertura": "09:00",
  "horarioCierre": "23:00",
  "diasHabiles": [
    "LUNES",
    "MARTES",
    "MIERCOLES",
    "JUEVES",
    "VIERNES",
    "SABADO"
  ],
  "descripcion": "Restaurante especializado en comidas rápidas.",
  "tipoServicio": "DELIVERY"
}
```

---

## Response (200)

```json
{
  "mensaje": "Establecimiento creado correctamente",
  "idEstablecimiento": 1
}
```

---

## Acción del Frontend

- Mostrar el formulario de configuración inicial.
- Validar los campos obligatorios.
- Consumir el endpoint.
- Si la operación fue exitosa, redireccionar al Registro de Administrador.

---

## Posibles errores

| Código | Descripción |
|---------|-------------|
| 400 | Datos inválidos. |
| 409 | El establecimiento ya fue configurado. |
| 500 | Error interno del servidor. |

---

# Login

## Descripción

Permite autenticar un usuario y obtener un JWT para acceder a los endpoints protegidos.

---

## Endpoint

```http
POST /auth/login
```

---

## Quién puede utilizarlo

- Administrador
- Empleado
- Cliente

---

## Request

```json
{
  "email": "usuario@gmail.com",
  "password": "Pass1234"
}
```

---

## Response (200)

```json
{
  "token": "eyJhbGc..."
}
```

---

## Acción del Frontend

- Validar que los campos obligatorios estén completos.
- Consumir el endpoint.
- Guardar el JWT.
- Redireccionar al Dashboard correspondiente según el rol.

---

## Posibles errores

| Código | Descripción |
|---------|-------------|
| 400 | Datos inválidos. |
| 401 | Credenciales incorrectas. |
| 500 | Error interno del servidor. |

---

# Logout

## Descripción

Finaliza la sesión del usuario autenticado invalidando el JWT.

---

## Endpoint

```http
POST /auth/logout
```

---

## Quién puede utilizarlo

- Administrador
- Empleado
- Cliente
  (Cualquier usuario autenticado)
---

## Request

No recibe Body.

Debe enviarse el JWT en el Header.

```http
Authorization: Bearer {token}
```

---

## Response (200)

```text
Sesión cerrada correctamente
```

---

## Acción del Frontend

- Consumir el endpoint.
- Eliminar el JWT almacenado.
- Limpiar la información del usuario.
- Redireccionar al Login.

---

## Posibles errores

| Código | Descripción |
|---------|-------------|
| 401 | Usuario no autenticado. |
| 500 | Error interno del servidor. |

---

# Registro de Administrador

## Descripción

Permite registrar el primer administrador del sistema.

Este endpoint solamente puede utilizarse una vez, inmediatamente después del Setup.

---

## Endpoint

```http
POST /auth/registro-admin
```

---

## Quién puede utilizarlo

Público (únicamente durante la instalación).

---

## Request

```json
{
  "nombre": "Aida",
  "apellido": "Beltran",
  "email": "admin@gmail.com",
  "password": "Pass1234",
  "telefono": "1169875432",
  "dni": "33333333",
  "fechaNacimiento": "1997-09-21",
  "direccion": {
    "nombre": "Casa",
    "calle": "Av. Belgrano",
    "numero": "1234",
    "localidad": "Avellaneda",
    "piso": "",
    "departamento": "B",
    "codigoPostal": "2222",
    "referencia": "Frente a la plaza",
    "esPrincipal": true
  }
}
```

---

## Response (200)

```json
{
  "token": "eyJhbGc..."
}
```

---

## Acción del Frontend

- Validar el formulario.
- Consumir el endpoint.
- Guardar el JWT recibido.
- Redireccionar al Login.

---

## Posibles errores

| Código | Descripción |
|---------|-------------|
| 400 | Datos inválidos. |
| 409 | Ya existe un administrador registrado. |
| 500 | Error interno del servidor. |

---

# Registro de Cliente

## Descripción

Permite registrar un nuevo cliente en el sistema.

---

## Endpoint

```http
POST /auth/registro-cliente
```

---

## Quién puede utilizarlo

Público.

---

## Request

```json
{
  "nombre": "Juan",
  "apellido": "Perez",
  "email": "juan@gmail.com",
  "password": "Pass1234",
  "telefono": "1169875432",
  "dni": "40111222",
  "fechaNacimiento": "1998-03-15",
  "direccion": {
    "nombre": "Casa",
    "calle": "Belgrano",
    "numero": "120",
    "localidad": "Avellaneda",
    "piso": "",
    "departamento": "",
    "codigoPostal": "1870",
    "referencia": ""
  }
}
```

---

## Response (200)

```json
{
  "token": "eyJhbGc..."
}
```

---

## Acción del Frontend

- Validar el formulario.
- Consumir el endpoint.
- Guardar el JWT.
- Redireccionar al inicio o Dashboard del Cliente.

---

## Posibles errores

| Código | Descripción |
|---------|-------------|
| 400 | Datos inválidos o email ya registrado. |
| 500 | Error interno del servidor. |

---
# Recuperación de Contraseña

## Flujo

```text
Login
    │
    ▼
¿Olvidaste tu contraseña?
    │
    ▼
POST /auth/solicitar-recuperacion
    │
    ▼
Correo electrónico con enlace
    │
    ▼
Pantalla Restablecer Contraseña
    │
    ▼
POST /auth/restablecer-password
    │
    ▼
Login
```

---

# Solicitar Recuperación de Contraseña

## Descripción

Permite solicitar el envío de un correo electrónico con un enlace para restablecer la contraseña del usuario.

---

## Endpoint

```http
POST /auth/solicitar-recuperacion
```

---

## Quién puede utilizarlo

Público.

---

## Request

```json
{
  "email": "usuario@gmail.com"
}
```

---

## Response (200)

```text
Si el correo está registrado, vas a recibir instrucciones.
```

---

## Acción del Frontend

- Mostrar únicamente el campo Email.
- Validar que el email tenga un formato válido.
- Consumir el endpoint.
- Mostrar el mensaje devuelto por el backend.
- Redireccionar al Login o mostrar una pantalla indicando que se envió el correo.

---

## Posibles errores

| Código | Descripción |
|---------|-------------|
| 400 | Email inválido. |
| 500 | Error interno del servidor. |

---

# Restablecer Contraseña

## Descripción

Permite establecer una nueva contraseña utilizando el token recibido por correo electrónico.

---

## Endpoint

```http
POST /auth/restablecer-password
```

---

## Quién puede utilizarlo

Público.

---

## Request

```json
{
  "token": "eyJhbGc....",
  "nuevaPassword": "NuevaPassword123"
}
```

---

## Response (200)

```text
Contraseña actualizada correctamente.
```

---

## Acción del Frontend

- Obtener el token desde el enlace recibido por correo.
- Solicitar la nueva contraseña.
- Solicitar la confirmación de la nueva contraseña.
- Verificar que ambas coincidan.
- Consumir el endpoint.
- Redireccionar al Login.

---

## Posibles errores

| Código | Descripción |
|---------|-------------|
| 400 | Token inválido o expirado. |
| 400 | La contraseña no cumple las validaciones. |
| 500 | Error interno del servidor. |

---

# Obtener Mi Perfil

## Descripción

Obtiene la información del usuario autenticado.

El usuario se identifica mediante el JWT enviado en la solicitud.

---

## Endpoint

```http
GET /perfil
```

---

## Quién puede utilizarlo

- ADMIN
- EMPLEADO
- CLIENTE
  (Cualquier usuario autenticado)
---

## Request

No recibe Body.

Debe enviarse el JWT en el Header.

```http
Authorization: Bearer {token}
```

---

## Response (200)

```json
{
  "id": 2,
  "nombre": "Aida",
  "apellido": "Beltran",
  "email": "aida@gmail.com",
  "telefono": "1169875432",
  "dni": "50112223",
  "fechaNacimiento": "2000-05-10",
  "rol": "ADMIN",
  "direccion": {
    "id": 2,
    "nombre": "Casa",
    "calle": "Mitre",
    "numero": "1",
    "localidad": "Avellaneda",
    "piso": "8",
    "departamento": "B",
    "codigoPostal": "1111",
    "referencia": "Frente a la plaza",
    "esPrincipal": true
  }
}
```

---

## Acción del Frontend

- Consumir el endpoint al ingresar a la pantalla.
- Completar automáticamente el formulario con la información recibida.
- Mantener el ID únicamente para uso interno.

---

## Posibles errores

| Código | Descripción |
|---------|-------------|
| 401 | Usuario no autenticado. |
| 500 | Error interno del servidor. |

---

# Editar Mi Perfil

## Descripción

Permite modificar la información del usuario autenticado.

El usuario se identifica mediante el JWT.

---

## Endpoint

```http
PUT /perfil
```

---

## Quién puede utilizarlo

- ADMIN
- EMPLEADO
- CLIENTE
  (Cualquier usuario autenticado)
---

## Request

> **Importante:** Este endpoint utiliza `UpdatePerfilRequest`, por lo tanto **no recibe email, DNI ni rol**.

```json
{
  "nombre": "Aida",
  "apellido": "Beltran",
  "telefono": "1122334455",
  "fechaNacimiento": "2000-05-10",
  "direccion": {
    "id": 2,
    "nombre": "Casa",
    "calle": "Mitre",
    "numero": "1",
    "localidad": "Avellaneda",
    "piso": "8",
    "departamento": "B",
    "codigoPostal": "1111",
    "referencia": "Frente a la plaza",
    "esPrincipal": true
  }
}
```

---

## Response (200)

```json
{
  "id": 2,
  "nombre": "Aida",
  "apellido": "Beltran",
  "email": "aida@gmail.com",
  "telefono": "1122334455",
  "dni": "50112223",
  "fechaNacimiento": "2000-05-10",
  "rol": "ADMIN",
  "estado": "ACTIVO",
  "direccion": {
    "id": 2,
    "nombre": "Casa",
    "calle": "Mitre",
    "numero": "1",
    "localidad": "Avellaneda",
    "piso": "8",
    "departamento": "B",
    "codigoPostal": "1111",
    "referencia": "Frente a la plaza",
    "esPrincipal": true
  }
}
```

---

## Acción del Frontend

- Obtener previamente la información mediante `GET /perfil`.
- Completar el formulario.
- Permitir modificar únicamente los campos habilitados.
- Consumir el endpoint.
- Actualizar la información mostrada en pantalla.
- Mostrar un mensaje indicando que la actualización fue exitosa.

---

## Posibles errores

| Código | Descripción |
|---------|-------------|
| 400 | Datos inválidos. |
| 401 | Usuario no autenticado. |
| 500 | Error interno del servidor. |

---

# Cambiar Contraseña

## Descripción

Permite al usuario autenticado cambiar su contraseña.

Para realizar la operación es obligatorio ingresar la contraseña actual.

---

## Endpoint

```http
PUT /perfil/password
```

---

## Quién puede utilizarlo

- ADMIN
- EMPLEADO
- CLIENTE
  (Cualquier usuario autenticado)
---

## Request

```json
{
  "passwordActual": "Password123",
  "passwordNueva": "NuevaPassword123"
}
```

---

## Response (200)

```text
Contraseña actualizada correctamente.
```

---

## Acción del Frontend

- Solicitar la contraseña actual.
- Solicitar la nueva contraseña.
- Solicitar la confirmación de la nueva contraseña.
- Validar que ambas coincidan.
- Consumir el endpoint.
- Mostrar el mensaje devuelto por el backend.
- Limpiar el formulario.

---

## Posibles errores

| Código | Descripción |
|---------|-------------|
| 400 | La contraseña actual es incorrecta. |
| 400 | La nueva contraseña no cumple las validaciones. |
| 401 | Usuario no autenticado. |
| 500 | Error interno del servidor. |

---

# Solicitar Cambio de Contraseña por Email

## Descripción

Permite al usuario autenticado solicitar el envío de un correo electrónico para cambiar su contraseña desde un enlace seguro.

---

## Endpoint

```http
POST /perfil/password
```

---

## Quién puede utilizarlo

- ADMIN
- EMPLEADO
- CLIENTE
  (Cualquier usuario autenticado)
---

## Request

No recibe Body.

Debe enviarse el JWT en el Header.

```http
Authorization: Bearer {token}
```

---

## Response (200)

```text
Se envió el correo para cambiar la contraseña.
```

---

## Acción del Frontend

- Mostrar un botón **Enviar correo de recuperación**.
- Consumir el endpoint.
- Mostrar el mensaje devuelto por el backend.
- Informar al usuario que debe revisar su correo electrónico.

---

## Posibles errores

| Código | Descripción |
|---------|-------------|
| 401 | Usuario no autenticado. |
| 500 | Error interno del servidor. |

---
# Gestión de Usuarios

Esta sección documenta las funcionalidades disponibles para la administración de usuarios del sistema.

> **Importante:** Todas las funcionalidades de esta sección requieren autenticación y solamente pueden ser utilizadas por un usuario con rol **ADMIN**.

---

# Crear Personal

## Descripción

Permite crear nuevos usuarios internos del sistema.

El mismo endpoint permite registrar tanto **Administradores** como **Empleados**.

Si no se envía el campo `rol`, el backend creará un usuario con rol **EMPLEADO**.

---

## Endpoint

```http
POST /auth/crear-personal
```

---

## Quién puede utilizarlo

- ADMIN

---

## Crear un Empleado

### Request

```json
{
  "nombre": "Juan",
  "apellido": "Perez",
  "email": "juan@gmail.com",
  "password": "Pass1234",
  "telefono": "1169875432",
  "dni": "40111222",
  "fechaNacimiento": "1998-03-15",
  "direccion": {
    "nombre": "Casa",
    "calle": "Belgrano",
    "numero": "120",
    "localidad": "Avellaneda",
    "piso": "",
    "departamento": "",
    "codigoPostal": "1870",
    "referencia": ""
  }
}
```

> Al no enviar el campo `rol`, el backend asignará automáticamente el rol **EMPLEADO**.
> **Importante**
>
> El backend solamente permite crear usuarios con rol **ADMIN** o **EMPLEADO**.
> Si se envía cualquier otro rol, la solicitud será rechazada.
---

## Crear un Administrador

### Request

```json
{
  "nombre": "Maria",
  "apellido": "Gomez",
  "email": "maria@gmail.com",
  "password": "Pass1234",
  "telefono": "1199988877",
  "dni": "32111222",
  "fechaNacimiento": "1995-07-20",
  "rol": "ADMIN",
  "direccion": {
    "nombre": "Casa",
    "calle": "Mitre",
    "numero": "300",
    "localidad": "Avellaneda",
    "piso": "",
    "departamento": "",
    "codigoPostal": "1870",
    "referencia": ""
  }
}
```

---

## Response (200)

Empleado:

```text
Empleado creado correctamente
```

Administrador:

```text
Admin creado correctamente
```

---

## Acción del Frontend

- Mostrar un selector con los roles disponibles.
- Si el usuario selecciona **Empleado**, no enviar el campo `rol`.
- Si selecciona **Administrador**, enviar `"rol":"ADMIN"`.
- Mostrar el mensaje recibido.
- Actualizar automáticamente el listado de usuarios.

---

## Posibles errores

| Código | Descripción |
|---------|-------------|
|400|Datos inválidos.|
|400|Email o DNI ya registrado.|
|400|Rol inválido.|
|401|Usuario no autenticado.|
|403|No posee permisos.|
|500|Error interno.|

---

# Listar Usuarios

## Descripción

Obtiene el listado de usuarios registrados.

Permite filtrar por:

- Rol
- Texto (nombre o email)

---

## Endpoint

```http
GET /perfil/usuarios
```

---

## Quién puede utilizarlo

- ADMIN

---
> **Importante**
>
> Si no se envía el parámetro `rol`, el backend devuelve únicamente los usuarios con rol **ADMIN** y **EMPLEADO**.
> Para listar clientes debe enviarse el filtro correspondiente (`rol=CLIENTE`).
## Parámetros (opcionales)

|Parámetro|Descripción|
|----------|-----------|
|rol|ADMIN, EMPLEADO o CLIENTE|
|texto|Busca por nombre o email|

---

## Ejemplos

```http
GET /perfil/usuarios
```

```http
GET /perfil/usuarios?rol=EMPLEADO
```

```http
GET /perfil/usuarios?texto=juan
```

```http
GET /perfil/usuarios?texto=gmail
```

---

## Response (200)

```json
[
  {
    "id": 1,
    "nombre": "Lucia",
    "apellido": "Fernandez",
    "email": "lucia@gmail.com",
    "telefono": "1169875432",
    "dni": "38987654",
    "fechaNacimiento": "1997-09-21",
    "rol": "EMPLEADO",
    "direccion": {
      "id": 1,
      "nombre": "Casa",
      "calle": "Belgrano",
      "numero": "120",
      "localidad": "Avellaneda",
      "piso": "",
      "departamento": "",
      "codigoPostal": "1870",
      "referencia": "",
      "esPrincipal": true
    }
  }
]
```

---

## Acción del Frontend

- Cargar automáticamente la información.
- Permitir buscar por texto.
- Permitir filtrar por rol.
- Actualizar la tabla sin recargar la página.
- Permitir seleccionar un usuario para editar.

---

## Posibles errores

|Código|Descripción|
|------|-----------|
|401|Usuario no autenticado.|
|403|No posee permisos.|
|500|Error interno.|

---

# Editar Usuario

## Descripción

Permite modificar la información de cualquier usuario registrado en el sistema.

El endpoint solamente puede ser utilizado por un Administrador.

> **Importante:** Este endpoint utiliza `UpdateUsuarioRequest`.

---

## Endpoint

```http
PUT /perfil/usuarios/{id}
```

---

## Quién puede utilizarlo

- ADMIN

---

## Parámetro

|Nombre|Descripción|
|-------|-----------|
|id|Identificador del usuario.|

---

## Request

```json
{
  "nombre": "Juan",
  "apellido": "Perez",
  "telefono": "1122334455",
  "email": "juan@gmail.com",
  "estado": "ACTIVO"
}
```

> No se pueden modificar:

- DNI
- Rol
- Contraseña
- Dirección
- Fecha de nacimiento

---

## Response (200)

```json
{
  "id": 10,
  "nombre": "Juan",
  "apellido": "Perez",
  "email": "juan@gmail.com",
  "telefono": "1122334455",
  "dni": "40111222",
  "fechaNacimiento": "1998-03-15",
  "rol": "EMPLEADO",
  "direccion": {
    "id": 5,
    "nombre": "Casa",
    "calle": "Belgrano",
    "numero": "120",
    "localidad": "Avellaneda",
    "piso": "",
    "departamento": "",
    "codigoPostal": "1870",
    "referencia": "",
    "esPrincipal": true
  }
}
```

---

## Acción del Frontend

- Obtener el usuario desde el listado.
- Completar automáticamente el formulario.
- Permitir editar únicamente los campos habilitados.
- Consumir el endpoint.
- Actualizar la tabla.
- Mostrar un mensaje de éxito.

---

## Posibles errores

|Código|Descripción|
|------|-----------|
|400|Datos inválidos.|
|401|Usuario no autenticado.|
|403|No posee permisos.|
|404|Usuario no encontrado.|
|500|Error interno.|

---

# Desactivar Usuario

## Descripción

Permite desactivar un usuario del sistema.

El backend realiza un **Soft Delete**, por lo que el usuario permanece almacenado en la base de datos, pero deja de estar disponible para operar y ya no aparece en los listados.

---

## Endpoint

```http
DELETE /perfil/usuarios/{id}
```

---

## Quién puede utilizarlo

- ADMIN

---

## Parámetro

|Nombre|Descripción|
|-------|-----------|
|id|Identificador del usuario.|

---

## Request

No recibe Body.

---

## Response (200)

```text
Usuario eliminado correctamente
```

---

## Acción del Frontend

- Mostrar un cuadro de confirmación.
- Consumir el endpoint.
- Mostrar el mensaje recibido.
- Eliminar el usuario de la tabla.
- Actualizar automáticamente el listado.

---

## Posibles errores

|Código|Descripción|
|------|-----------|
|401|Usuario no autenticado.|
|403|No posee permisos.|
|404|Usuario no encontrado.|
|500|Error interno.|

---

# Manejo del JWT

Todos los endpoints protegidos requieren enviar el JWT mediante el siguiente encabezado HTTP:

```http
Authorization: Bearer {token}
```

---

## Recomendaciones para el Frontend

- Guardar el JWT luego del Login o Registro exitoso.
- Utilizar un interceptor HTTP para enviar automáticamente el token.
- Si el backend responde **401 Unauthorized**, eliminar el token almacenado y redireccionar al Login.
- No permitir que el usuario modifique campos que el backend no acepta (DNI, Rol, etc.).
- Mostrar siempre el mensaje devuelto por el backend cuando una operación sea exitosa.
- Centralizar el manejo de errores HTTP para mantener una experiencia consistente en toda la aplicación.

---