# Gestia

# Módulo: Productos

## Objetivo

Este documento describe el funcionamiento del módulo de productos desde el punto de vista del Frontend.

Toda la información aquí documentada corresponde a los endpoints implementados actualmente en el backend.

---

# Índice

- Flujo general
- Crear producto
- Editar producto
- Listar productos
- Gestión de imágenes
- Filtros del listado
- Manejo de errores
- Recomendaciones para el Frontend

---

# Flujo General

```text
                    Módulo Productos
                          │
          ┌───────────────┼───────────────┐
          │               │               │
          ▼               ▼               ▼
     Crear producto  Editar producto  Listar productos
     POST /productos PUT /productos/{id} GET /productos
          │               │               │
          ▼               ▼               ▼
       ADMIN           ADMIN          ADMIN / CLIENTE
```

> **Importante**
>
> Crear y editar productos utilizan `multipart/form-data` porque pueden recibir una imagen.
>
> El campo `producto` contiene el JSON y el campo `imagen` contiene el archivo, cuando corresponde.

---

# Crear Producto

## Descripción

Permite registrar un nuevo producto.

El producto se crea asociado al establecimiento del usuario autenticado.

El estado inicial del producto es asignado por el backend como `ACTIVO`.

## Endpoint

```http
POST /productos
```

## Quién puede utilizarlo

- ADMIN

## Tipo de Request

```http
Content-Type: multipart/form-data
```

El request contiene:

| Parte | Obligatoria | Tipo |
|-------|-------------|------|
| `producto` | Sí | JSON |
| `imagen` | No | Archivo |

> **Importante**
>
> La parte `producto` debe enviarse como `application/json`.
>
> No enviar el JSON como `application/octet-stream`.

## Request

### Parte `producto`

```json
{
  "nombre": "Torta de Chocolate",
  "descripcion": "Torta de chocolate con cobertura",
  "precio": 8500,
  "categoriaId": 1,
  "stock": 10,
  "codigo": "TORT-001"
}
```

### Parte `imagen`

Opcional.

Ejemplo:

```text
imagen: torta-chocolate.jpg
```

## Campos del Request

| Campo | Tipo | Obligatorio | Validación |
|-------|------|-------------|------------|
| `nombre` | String | Sí | Entre 3 y 150 caracteres |
| `descripcion` | String | No | Máximo 500 caracteres |
| `precio` | BigDecimal | Sí | Mayor a 0 |
| `categoriaId` | Long | Sí | Obligatorio |
| `stock` | Integer | No | No puede ser negativo |
| `codigo` | String | No | Máximo 50 caracteres |
| `imagen` | File | No | Archivo opcional |

## Reglas de negocio

El backend valida:

- La categoría debe existir y pertenecer al establecimiento.
- El código, cuando se informa, no puede estar registrado.
- El nombre no puede repetirse dentro del mismo establecimiento.
- El estado inicial siempre es `ACTIVO`.
- La imagen es opcional.

## Response (201)

```json
{
  "mensaje": "Producto creado correctamente"
}
```

## Acción del Frontend

- Validar los campos obligatorios.
- Construir un `FormData`.
- Agregar el JSON como la parte `producto`.
- Agregar la imagen solamente si el usuario seleccionó una.
- Enviar el JWT mediante el mecanismo de autenticación utilizado por el Frontend.
- Consumir el endpoint.
- Mostrar el mensaje recibido.
- Actualizar el listado de productos después de una creación exitosa.

---

# Editar Producto

## Descripción

Permite modificar un producto existente.

También permite conservar, reemplazar o eliminar la imagen asociada al producto.

## Endpoint

```http
PUT /productos/{id}
```

## Quién puede utilizarlo

- ADMIN

## Parámetro

| Nombre | Descripción |
|--------|-------------|
| `id` | Identificador del producto |

## Tipo de Request

```http
Content-Type: multipart/form-data
```

El request contiene:

| Parte | Obligatoria | Tipo |
|-------|-------------|------|
| `producto` | Sí | JSON |
| `imagen` | No | Archivo |

> **Importante**
>
> La parte `producto` debe enviarse como `application/json`.

## Request

```json
{
  "nombre": "Torta de Chocolate",
  "descripcion": "Torta de chocolate actualizada",
  "precio": 9000,
  "categoriaId": 1,
  "estado": "ACTIVO",
  "stock": 15,
  "eliminarImagen": false,
  "codigo": "TORT-001"
}
```

## Campos del Request

| Campo | Tipo | Obligatorio | Validación |
|-------|------|-------------|------------|
| `nombre` | String | Sí | Entre 3 y 150 caracteres |
| `descripcion` | String | No | Máximo 500 caracteres |
| `precio` | BigDecimal | Sí | Mayor a 0 |
| `categoriaId` | Long | Sí | Obligatorio |
| `estado` | EstadoProducto | Sí | `ACTIVO` o `INACTIVO` |
| `stock` | Integer | No | No puede ser negativo |
| `eliminarImagen` | Boolean | No | `true`, `false` o `null` |
| `codigo` | String | No | Máximo 50 caracteres |
| `imagen` | File | No | Archivo opcional |

### Estados permitidos

```text
ACTIVO
INACTIVO
```

### Importante sobre `eliminarImagen`

El backend utiliza `Boolean`, no `boolean`.

Por lo tanto, el Frontend puede enviar:

```json
{"eliminarImagen" :  true}
```

```json
{"eliminarImagen": false}
```

o no especificar el campo / enviarlo como `null`.

# Casos de edición de imagen

## Caso 1 — Editar datos sin cambiar la imagen

El usuario modifica datos del producto pero no selecciona una imagen nueva.

```json
{
  "nombre": "Torta de Chocolate",
  "descripcion": "Nueva descripción",
  "precio": 9000,
  "categoriaId": 1,
  "estado": "ACTIVO",
  "stock": 15,
  "eliminarImagen": false,
  "codigo": "TORT-001"
}
```

No se envía la parte `imagen`.

Resultado:

```text
Datos del producto → actualizados
Imagen actual      → conservada
```

## Caso 2 — Reemplazar la imagen

El usuario selecciona una imagen nueva.

### Parte `producto`

```json
{
  "nombre": "Torta de Chocolate",
  "descripcion": "Torta de chocolate actualizada",
  "precio": 9000,
  "categoriaId": 1,
  "estado": "ACTIVO",
  "stock": 15,
  "eliminarImagen": false,
  "codigo": "TORT-001"
}
```

### Parte `imagen`

```text
imagen: torta-nueva.jpg
```

Resultado:

```text
Imagen anterior → reemplazada
Imagen nueva    → guardada
```

## Caso 3 — Eliminar la imagen

Si el usuario quiere eliminar la imagen actual y no selecciona una nueva:

### Parte `producto`

```json
{
  "nombre": "Torta de Chocolate",
  "descripcion": "Torta de chocolate",
  "precio": 9000,
  "categoriaId": 1,
  "estado": "ACTIVO",
  "stock": 15,
  "eliminarImagen": true,
  "codigo": "TORT-001"
}
```

### Parte `imagen`

No enviar.

Resultado:

```text
imagenUrl → null
imagen física → eliminada
```

## Caso 4 — Imagen nueva + eliminarImagen=true

Esta combinación no está permitida.

No enviar:

```text
imagen → archivo nuevo
eliminarImagen → true
```

El backend responde con un error porque ambas acciones son contradictorias.

Mensaje:

```text
No se puede enviar una imagen nueva y solicitar su eliminación al mismo tiempo
```

# Response de Edición

## Response (200)

```json
{
  "id": 3,
  "nombre": "Torta de Chocolate",
  "descripcion": "Torta de chocolate actualizada",
  "precio": 9000,
  "categoriaNombre": "Tortas",
  "estado": "ACTIVO",
  "stock": 15,
  "imagenUrl": "/uploads/establecimiento-1/productos/xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx.jpg",
  "codigo": "TORT-001"
}
```

> `imagenUrl` es proporcionada por el backend.
>
> El Frontend no debe enviar `imagenUrl` en el request de edición.

## Acción del Frontend

- Obtener el producto seleccionado.
- Completar el formulario con sus datos actuales.
- Permitir modificar los campos habilitados.
- Permitir seleccionar una nueva imagen.
- Permitir solicitar la eliminación de la imagen.
- Construir `FormData`.
- Enviar el JSON como `producto`.
- Enviar `imagen` solamente cuando corresponda.
- No enviar `imagenUrl`.
- Actualizar el producto mostrado después de una edición exitosa.
- Utilizar la `imagenUrl` devuelta por el backend.

---

# Listar Productos

## Descripción

Obtiene los productos pertenecientes al establecimiento del usuario autenticado.

Permite filtrar por categoría, estado y texto.

## Endpoint

```http
GET /productos
```

## Quién puede utilizarlo

- ADMIN
- CLIENTE

## Parámetros opcionales

| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| `categoriaId` | Long | Filtra por categoría |
| `estado` | EstadoProducto | Filtra por estado |
| `texto` | String | Busca por nombre o descripción |

Todos los parámetros son opcionales.



### Listar todos

```http
GET /productos
```

### Filtrar por categoría

```http
GET /productos?categoriaId=1
```

### Filtrar por estado

```http
GET /productos?estado=ACTIVO
```

### Buscar por texto

```http
GET /productos?texto=chocolate
```

La búsqueda por texto se realiza sobre:

- nombre
- descripción

### Combinar filtros

```http
GET /productos?categoriaId=1&estado=ACTIVO&texto=chocolate
```

## Regla para CLIENTE

Cuando el usuario autenticado tiene rol `CLIENTE`, el backend fuerza el filtro:

```text
estado = ACTIVO
```

Por lo tanto, el cliente no puede obtener productos `INACTIVO` mediante el listado.

## Response (200)

```json
[
  {
    "id": 3,
    "nombre": "Torta de Chocolate",
    "descripcion": "Torta de chocolate con cobertura",
    "precio": 8500,
    "categoriaNombre": "Tortas",
    "estado": "ACTIVO",
    "stock": 10,
    "imagenUrl": "/uploads/establecimiento-1/productos/xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx.jpg",
    "codigo": "TORT-001"
  }
]
```

## Campos de `ProductoResponse`

| Campo | Tipo |
|-------|------|
| `id` | Long |
| `nombre` | String |
| `descripcion` | String |
| `precio` | BigDecimal |
| `categoriaNombre` | String |
| `estado` | EstadoProducto |
| `stock` | Integer |
| `imagenUrl` | String |
| `codigo` | String |

## Acción del Frontend

- Consumir el endpoint al cargar el listado.
- Mostrar los productos recibidos.
- Mostrar nombre, descripción, precio, categoría, estado, stock, código e imagen cuando exista.
- Permitir filtrar por categoría.
- Permitir filtrar por estado cuando corresponda.
- Permitir buscar por nombre o descripción mediante `texto`.
- Actualizar el listado después de crear o editar un producto.
- Utilizar `imagenUrl` para mostrar la imagen del producto.

> **Nota sobre `imagenUrl`**
>
> El backend devuelve la ruta de la imagen. La forma de construir la URL final que utiliza el navegador depende de la configuración del Frontend y de la URL base del backend.

---

# Gestión de Imágenes

## Resumen

```text
CREAR
    │
    ├── sin imagen → producto sin imagen
    │
    └── con imagen → imagen guardada

EDITAR
    │
    ├── sin imagen → conserva imagen actual
    │
    ├── imagen nueva → reemplaza imagen actual
    │
    └── eliminarImagen=true → elimina imagen actual
```

### Regla importante

No combinar:

```text
imagen nueva
+
eliminarImagen=true
```

---

# Posibles errores

| Código | Descripción |
|--------|-------------|
| 400 | Datos inválidos según las validaciones del request. |
| 400 | Categoría inexistente o perteneciente a otro establecimiento. |
| 400 | Código ya registrado. |
| 400 | Nombre de producto ya registrado dentro del establecimiento. |
| 400 | Producto no encontrado. |
| 400 | No se puede enviar una imagen nueva y solicitar su eliminación al mismo tiempo. |
| 401 | Usuario no autenticado. |
| 403 | El usuario no posee permisos para realizar la operación. |
| 500 | Error interno del servidor. |

> **Importante**
>
> Los errores de negocio del módulo son manejados por el backend mediante `BusinessException` y devueltos como respuesta HTTP `400`.
>
> Los errores inesperados son devueltos como `500`.

---

# Recomendaciones para el Frontend

- Utilizar `FormData` para crear y editar productos.
- Enviar la parte `producto` como JSON con `Content-Type: application/json`.
- Enviar `imagen` solamente cuando el usuario haya seleccionado un archivo.
- No enviar `imagenUrl` al crear o editar.
- Utilizar `imagenUrl` del `ProductoResponse` para mostrar la imagen.
- Para eliminar una imagen, enviar `eliminarImagen: true` y no enviar `imagen`.
- Para reemplazar una imagen, enviar la nueva `imagen` y `eliminarImagen: false`.
- No enviar una imagen nueva junto con `eliminarImagen: true`.
- Después de crear o editar, actualizar el listado de productos.
- Mantener el JWT mediante el mecanismo de autenticación utilizado por el Frontend.
- Mostrar al usuario los mensajes devueltos por el backend cuando corresponda.
