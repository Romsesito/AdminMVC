# Studio Managment

Esta API REST le permite al usuario controlar sus clientes y usuarios asignándoles el rol de cliente o de creativo para ofrecer un sistema que le permita recibir comisiones a una empresa.

##Enlace en Youtube

## APIs

>[!IMPORTANT]
>**URL Base (`{{baseUrl}}`)**
Todas las rutas de la API comienzan con `/api/admin`.

>[!NOTE]
>**Headers Comunes**
* Para solicitudes `POST` y `PUT` que envían datos JSON, incluye el header:
    `Content-Type: application/json`

## Resumen de Endpoints API

| Método | Endpoint                                 | Descripción Corta                                 |
| :----- | :--------------------------------------- | :------------------------------------------------ |
| `POST` | `/users`                                 | Crea un nuevo usuario.                            |
| `GET`  | `/users`                                 | Obtiene todos los usuarios.                       |
| `GET`  | `/users/{userId}`                        | Obtiene un usuario específico por su ID.          |
| `PUT`  | `/users/{userId}/role`                   | Asigna o actualiza el rol de un usuario.          |
| `DELETE`| `/users/{userId}`                        | Elimina un usuario por su ID.                     |
| `DELETE`| `/users/{userId}/role/{roleName}`        | Remueve un rol específico de un usuario.          |

---
