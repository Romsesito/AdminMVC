# Studio Managment

Esta API REST le permite al usuario controlar sus clientes y usuarios asignándoles el rol de cliente o de creativo para ofrecer un sistema que le permita recibir comisiones a una empresa.

## Enlace en Youtube 
[LINK AL VIDEO DE YOUTUBE](https://www.youtube.com/watch?v=xjLuToDD7ic)

## APIs

>[!IMPORTANT]
>**URL Base (`{{baseUrl}}`)**
Todas las rutas de la API comienzan con `/api/admin`.

>[!NOTE]
>**Headers Comunes**
* Para solicitudes `POST` y `PUT` que envían datos JSON, incluye el header:
  `Content-Type: application/json`

## Resumen de Endpoints API

| Método | Endpoint                             | Descripción Corta                         |
| :----- | :--------------------------------------- | :---------------------------------------- |
| `POST` | `/users`                                 | Crea un nuevo usuario.                    |
| `GET`  | `/users`                                 | Obtiene todos los usuarios.               |
| `GET`  | `/users/{userId}`                        | Obtiene un usuario específico por su ID.  |
| `PUT`  | `/users/{userId}/role`                   | Asigna o actualiza el rol de un usuario.  |
| `DELETE`| `/users/{userId}`                        | Elimina un usuario por su ID.             |
| `DELETE`| `/users/{userId}/role/{roleName}`        | Remueve un rol específico de un usuario.  |

---

## Validaciones del Backend

La API implementa varias validaciones en el lado del servidor para asegurar la integridad de los datos y el cumplimiento de las reglas de negocio.

### Validación de Roles al Crear/Asignar

Al intentar crear un usuario o asignar un rol, se aplican las siguientes validaciones:

* **No se permite la creación de Administradores**:
    * No es posible asignar el rol `ROLE_ADMIN` a través de los endpoints públicos de creación o asignación de roles. Cualquier intento resultará en un error `400 Bad Request` con el mensaje: `"No se puede crear un usuario ADMIN a través de este endpoint."`
* **Roles Válidos para Creación/Asignación**:
    * Los únicos roles que se pueden asignar o con los que se puede crear un usuario son `ROLE_CLIENT` y `ROLE_CREATIVE`.
    * Si se proporciona un rol diferente a estos (y que no sea `ROLE_ADMIN`), se devolverá un error `400 Bad Request` con el mensaje: `"Rol inválido para creación: [roleNameString]. Los roles válidos para creación son ROLE_CLIENT, ROLE_CREATIVE."`
* **Manejo de Nombres de Rol Inválidos**:
    * Si el nombre del rol proporcionado no corresponde a ningún valor definido en el enumerador `ERole`, se generará una `IllegalArgumentException`. Esto resultará en una respuesta `400 Bad Request` con el mensaje: `"Nombre de rol inválido: [roleNameString]. Los roles válidos para creación son ROLE_CLIENT, ROLE_CREATIVE."`

### Validación de Campos de Usuario

Para la entidad de usuario, se aplican las siguientes validaciones a nivel de campo:

* **Nombre de Usuario (`username`)**:
    * **Obligatorio**: No puede estar vacío o ser nulo (`@NotBlank`). Mensaje: `"El nombre de usuario es obligatorio."`
    * **Longitud**: Debe tener entre 3 y 50 caracteres (`@Size(min = 3, max = 50)`). Mensaje: `"El nombre de usuario debe tener entre 3 y 50 caracteres."`
* **Contraseña (`password`)**:
    * **Obligatoria**: No puede estar vacía o ser nula (`@NotBlank`). Mensaje: `"La contraseña es obligatoria."`
    * **Longitud**: Debe tener entre 8 y 120 caracteres (`@Size(min = 8, max = 120)`). Mensaje: `"La contraseña debe tener entre 8 y 120 caracteres."`
* **Rol (nombre del rol en la solicitud)**:
    * **Obligatorio**: El campo que representa el rol en la solicitud no puede estar vacío o ser nulo (`@NotBlank`). Mensaje: `"El rol es obligatorio."` (Este mensaje se aplica al *string* que se recibe y que luego se intentará convertir a un `ERole`).
