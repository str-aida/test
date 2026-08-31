# Antigravity Rules — Gestia Frontend

## 1. Objetivo

Este repositorio es un entorno experimental separado del proyecto principal de Gestia.

El objetivo es completar y mejorar exclusivamente el Frontend de Gestia, utilizando:

- El Frontend existente como base técnica.
- El Backend existente como referencia funcional y contractual.
- Stitch como referencia visual y de diseño.

El objetivo NO es rediseñar ni modificar la arquitectura del Backend.

---

# 2. Regla absoluta sobre el Backend

El directorio:

    /backEnd

es de SOLO LECTURA.

Antigravity tiene PROHIBIDO:

- Modificar archivos del Backend.
- Crear archivos dentro de `backEnd/`.
- Eliminar archivos del Backend.
- Renombrar archivos del Backend.
- Refactorizar código del Backend.
- Cambiar endpoints.
- Cambiar DTOs.
- Cambiar entidades.
- Cambiar validaciones.
- Cambiar configuraciones.
- Cambiar seguridad o JWT.
- Cambiar contratos de API.
- Cambiar la base de datos.
- Crear migraciones.
- Modificar `application.properties`.
- Modificar `pom.xml`.

El Backend existe únicamente como referencia para comprender:

- Endpoints disponibles.
- Request DTOs.
- Response DTOs.
- Modelos.
- Roles.
- Validaciones existentes.
- Reglas funcionales.
- Contratos entre Frontend y Backend.

Si una funcionalidad del Frontend requiere algo que el Backend actualmente no proporciona:

1. NO modificar el Backend.
2. NO inventar un endpoint.
3. NO inventar una respuesta.
4. NO cambiar el contrato existente.
5. Informar claramente qué funcionalidad necesita y qué falta en el Backend.

---

# 3. Área permitida de trabajo

El único directorio donde se permite realizar modificaciones es:

    /frontEnd

Todas las nuevas funcionalidades, componentes, servicios, estilos, validaciones y mejoras deben implementarse dentro de `frontEnd/`.

Antes de crear una nueva estructura, reutilizar componentes, servicios, estilos, modelos y utilidades existentes cuando sea posible.

No duplicar código innecesariamente.

---

# 4. Respetar el Frontend existente

El Frontend actual fue desarrollado manualmente y constituye la base del proyecto.

Antes de modificar o crear funcionalidades:

- Analizar la arquitectura existente.
- Analizar la estructura de carpetas.
- Analizar los componentes existentes.
- Analizar los servicios existentes.
- Analizar los modelos existentes.
- Analizar los interceptores existentes.
- Analizar los estilos existentes.
- Analizar las rutas existentes.
- Analizar los patrones de implementación utilizados.

No reemplazar la arquitectura existente por otra diferente sin autorización.

No introducir una nueva librería o framework para resolver algo que ya puede resolverse con las herramientas existentes.

Mantener las convenciones actuales del proyecto.

Respetar especialmente:

- Angular.
- TypeScript.
- Standalone Components.
- Reactive Forms.
- Servicios existentes.
- Interceptors existentes.
- Modelos existentes.
- Convenciones de nombres.
- Estructura de features.
- Estructura de layouts.
- Estilos SCSS existentes.

---

# 5. Diseño visual — Stitch

El proyecto de Stitch es la referencia principal para el diseño visual de Gestia.

Proyecto Stitch:

https://stitch.withgoogle.com/projects/17112155549078833184

Utilizar Stitch como referencia para:

- Layouts.
- Distribución de contenido.
- Botones.
- Formularios.
- Tablas.
- Cards.
- Modales.
- Navegación.
- Sidebar.
- Topbar.
- Espaciados.
- Tipografías.
- Jerarquía visual.
- Estados visuales.
- Colores.
- Iconografía.
- Componentes de interfaz.

No reemplazar el diseño de Stitch por interfaces genéricas o botones simples cuando exista una referencia visual disponible.

El objetivo es que el Frontend tenga una apariencia coherente con el diseño definido en Stitch.

---

# 6. Responsive Design

El Frontend debe ser completamente responsive.

Debe funcionar correctamente en:

- Celulares.
- Tablets.
- Laptops.
- PCs y monitores grandes.

No limitarse simplemente a reducir tamaños.

Los layouts deben adaptarse correctamente según el espacio disponible.

Prestar especial atención a:

- Sidebar.
- Topbar.
- Formularios.
- Tablas.
- Cards.
- Botones.
- Modales.
- Navegación.
- Grillas.
- Contenido horizontal.
- Imágenes.
- Espaciados.

No generar scroll horizontal innecesario.

Las interfaces deben seguir siendo utilizables tanto en pantallas pequeñas como grandes.

---

# 7. APIs y Backend

El Frontend debe consumir exclusivamente las APIs existentes.

Antes de implementar una funcionalidad:

1. Revisar el Backend.
2. Identificar el endpoint correspondiente.
3. Revisar el Request DTO.
4. Revisar el Response DTO.
5. Revisar parámetros.
6. Revisar respuestas y errores.
7. Implementar el consumo desde el Frontend.

No inventar endpoints.

No modificar el Backend para adaptar el Frontend.

No asumir estructuras de respuesta que no existan.

Si existe una discrepancia entre Stitch y el Backend:

    Backend = contrato funcional
    Stitch = referencia visual

La interfaz debe adaptarse al contrato real de la API.

---

# 8. Autenticación y autorización

Respetar completamente el sistema de autenticación existente.

No modificar:

- JWT.
- Login del Backend.
- Roles del Backend.
- Seguridad del Backend.
- Interceptors existentes.

Utilizar los mecanismos existentes del Frontend para:

- Guardar tokens.
- Enviar tokens.
- Obtener información del usuario.
- Manejar sesiones.
- Navegar según roles.

Los roles existentes deben respetarse.

No inventar nuevos roles.

---

# 9. Categorías

Las Categorías NO utilizan imágenes.

No agregar:

- Upload de imágenes.
- Campos de imagen.
- URLs de imágenes.
- Placeholders de fotografías.

a las Categorías, salvo que exista posteriormente una modificación explícita del contrato del Backend.

La interfaz de Categorías debe seguir el diseño correspondiente de Stitch.

---

# 10. Productos e imágenes

Los Productos SÍ utilizan imágenes.

Actualmente el modelo funcional del sistema almacena la URL de la imagen del producto en la base de datos.

El Frontend debe respetar este modelo.

No almacenar imágenes directamente en la base de datos.

No modificar el modelo del Backend.

No crear un sistema alternativo de almacenamiento.

El Frontend debe permitir trabajar con las imágenes de productos según lo que actualmente soporte el Backend.

Las imágenes deben:

- Ser adecuadas para uso web.
- Evitar archivos innecesariamente pesados.
- Respetar los formatos y límites actualmente soportados por el Backend.
- Mostrar correctamente la imagen en las pantallas de productos.
- Permitir reemplazar la imagen cuando la funcionalidad de edición lo permita.
- Tener un estado visual adecuado cuando un producto no tenga imagen.

Si el Backend no define actualmente un límite específico de tamaño o formato:

- No modificar el Backend.
- No inventar arbitrariamente un nuevo contrato.
- Informar la situación antes de implementar una restricción que pueda afectar la compatibilidad.

---

# 11. Calidad del código

Priorizar código:

- Claro.
- Mantenible.
- Reutilizable.
- Tipado.
- Consistente.
- Profesional.

Evitar:

- Duplicación innecesaria.
- Código muerto.
- Variables sin uso.
- Console logs innecesarios.
- Hardcodear datos que deberían provenir de APIs.
- Soluciones temporales que queden como definitivas.
- Componentes excesivamente grandes.

Reutilizar componentes y servicios existentes cuando corresponda.

---

# 12. No inventar funcionalidad

No agregar funcionalidades simplemente porque parecen útiles.

Una funcionalidad debe provenir de:

1. Código existente.
2. Backend existente.
3. Diseño de Stitch.
4. Requerimiento explícito proporcionado por el usuario.

Si una funcionalidad no está clara:

STOP.

Explicar la duda y solicitar confirmación antes de implementar.

---

# 13. Antes de modificar archivos

Durante la primera interacción con el proyecto:

NO modificar ningún archivo.

Primero:

1. Analizar la estructura del Frontend.
2. Analizar la estructura del Backend.
3. Analizar los módulos existentes.
4. Analizar los servicios.
5. Analizar las APIs.
6. Analizar los estilos.
7. Analizar Stitch.
8. Identificar qué funcionalidades ya están implementadas.
9. Identificar qué funcionalidades faltan.
10. Presentar un plan de trabajo.

Esperar autorización antes de comenzar a modificar archivos.

---

# 14. Testing y validación

Cada funcionalidad implementada debe probarse.

Siempre que sea posible:

1. Ejecutar el Frontend.
2. Verificar que compile.
3. Verificar errores de consola.
4. Verificar llamadas HTTP.
5. Verificar respuestas del Backend.
6. Probar los flujos principales.
7. Verificar comportamiento responsive.
8. Comparar visualmente con Stitch.

No considerar una funcionalidad terminada únicamente porque compila.

---

# 15. Backend de referencia durante las pruebas

Para las pruebas del Frontend se utilizará el Backend funcional del proyecto principal cuando sea necesario.

El Backend ubicado en:

    /backEnd

es principalmente una copia de referencia para comprender el contrato de la aplicación.

El hecho de que el Backend esté presente en este repositorio NO autoriza a modificarlo.

---

# 16. Regla de seguridad principal

Ante cualquier situación donde la solución parezca requerir modificar el Backend:

    DETENERSE.

Informar:

- Qué se intentaba implementar.
- Qué API existe actualmente.
- Qué falta.
- Por qué el Frontend no puede resolverlo con el contrato actual.

No realizar cambios en `backEnd/`.

---

# 17. Prioridad de fuentes

Cuando exista una diferencia entre fuentes, utilizar este criterio:

### Funcionalidad

    Backend actual
        ↓
    Frontend actual
        ↓
    Requerimientos explícitos

### Diseño

    Stitch
        ↓
    Estilos y componentes existentes
        ↓
    Criterios responsive

Nunca modificar el Backend para hacer coincidir el diseño.

---

# 18. Regla final

El objetivo es mejorar y completar el Frontend de Gestia manteniendo la arquitectura existente y respetando completamente el Backend.

Antes de realizar cambios importantes:

    ANALIZAR → EXPLICAR → PROPONER → ESPERAR AUTORIZACIÓN → IMPLEMENTAR → PROBAR

No:

    ANALIZAR → MODIFICAR TODO