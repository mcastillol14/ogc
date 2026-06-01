<div align="center">

# OGC — Backend API

**Sistema de gestión integral para tiendas de productos de cannabis legal (CBD/THC)**

[![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.x-brightgreen?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=flat-square&logo=postgresql)](https://www.postgresql.org/)
[![Tests](https://img.shields.io/badge/Tests-153%20passing-success?style=flat-square&logo=github-actions)]()
[![License](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)](LICENSE)

[Documentación API](doc/API.md) · [Reportar bug](issues) · [Solicitar feature](issues)

</div>

---

## ¿Qué es OGC?

OGC es una API REST construida con **Spring Boot 4** y **Java 21** que cubre el ciclo de vida completo de un comercio de productos de cannabis legal. Desde el registro y autenticación de clientes hasta la trazabilidad completa del inventario por lotes, pasando por la gestión de ventas, compras a proveedores y ajustes manuales de stock.

Diseñada con un enfoque **code-first**, **segura por defecto** y lista para ser consumida por cualquier frontend (React, Vue, Angular, apps móviles, etc.).

---

## Índice

- [Stack tecnológico](#stack-tecnológico)
- [Arquitectura](#arquitectura)
- [Modelo de dominio](#modelo-de-dominio)
- [Seguridad](#seguridad)
- [Requisitos previos](#requisitos-previos)
- [Instalación y configuración](#instalación-y-configuración)
- [Variables de entorno](#variables-de-entorno)
- [Ejecución](#ejecución)
- [Tests](#tests)
- [Módulos y funcionalidades](#módulos-y-funcionalidades)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Referencia de la API](#referencia-de-la-api)
- [Contribuir](#contribuir)

---

## Stack tecnológico

| Capa | Tecnología | Versión |
|---|---|---|
| Lenguaje | Java | 21 (LTS) |
| Framework principal | Spring Boot | 4.x |
| Persistencia | Spring Data JPA + Hibernate | via Boot |
| Base de datos | PostgreSQL | 16+ |
| Autenticación | JJWT (JWT) | 0.12.6 |
| Hashing | jBCrypt | 0.4 |
| Validación | Jakarta Bean Validation | via Boot |
| Email | Spring Mail (SMTP) | via Boot |
| Boilerplate | Lombok | via Boot |
| Testing | JUnit 5 + Mockito + AssertJ | via Boot |
| Build | Maven Wrapper | — |

---

## Arquitectura

La aplicación sigue una **arquitectura en capas clásica** adaptada a Spring Boot:

```
┌─────────────────────────────────────────────────┐
│                   HTTP Client                   │
└────────────────────────┬────────────────────────┘
                         │
┌────────────────────────▼────────────────────────┐
│          Filtro JWT  +  Rate Limiter             │  ← config/
│          Interceptor de Roles (@RequiresRole)    │
└────────────────────────┬────────────────────────┘
                         │
┌────────────────────────▼────────────────────────┐
│                  Controllers                    │  ← controller/
│   (AuthController, SaleController, ...)         │
└────────────────────────┬────────────────────────┘
                         │
┌────────────────────────▼────────────────────────┐
│                   Services                      │  ← service/
│   (AuthServiceImpl, SaleServiceImpl, ...)       │
└────────────────────────┬────────────────────────┘
                         │
┌────────────────────────▼────────────────────────┐
│                 Repositories                    │  ← repository/
│         (Spring Data JPA interfaces)            │
└────────────────────────┬────────────────────────┘
                         │
┌────────────────────────▼────────────────────────┐
│               PostgreSQL Database               │
└─────────────────────────────────────────────────┘
```

**Principios de diseño:**
- Los controladores solo validan la entrada y delegan en servicios.
- Los servicios contienen toda la lógica de negocio.
- Las excepciones extienden `AppException(HttpStatus, message)` con constructores privados y **factory methods** públicos.
- Los DTOs de entrada usan Jakarta Validation para garantizar la integridad de los datos en el borde del sistema.
- Los DTOs de salida nunca exponen campos sensibles (contraseña, tokens internos).

---

## Modelo de dominio

```
┌──────────────┐          ┌──────────────┐
│    User      │◄─extends─│   Customer   │
│  (superclass)│          │              │
└──────────────┘          └──────┬───────┘
                                 │ 1
                                 │
                           sells │ *
                          ┌──────▼───────┐       ┌────────────────┐
                          │    Sale      │───────►│   SaleLine     │
                          │              │  1  *  │ (lotId, weight)│
                          └──────────────┘        └───────┬────────┘
                                                          │ *
┌──────────────┐          ┌──────────────┐               │
│   Provider   │          │   Purchase   │               │ 1
│              │◄─────────│              │          ┌────▼────────┐
└──────────────┘  1     * │              │          │    Lot      │
                          └──────┬───────┘          │             │
                                 │ 1                └──────┬──────┘
                                 │                         │ 1
                           *     │                         │
                    ┌────────────▼──┐              ┌───────▼─────────┐
                    │ PurchaseLine  │──assignLot──►│   LotStock      │
                    │(productId,    │              │ (remainingWeight)│
                    │ orderedWeight)│              └─────────────────┘
                    └───────────────┘

┌────────────────────┐    ┌─────────────────────┐
│  StockMovement     │    │ InventoryAdjustment  │
│ (ENTRADA / SALIDA) │    │    + AdjustmentLine  │
└────────────────────┘    └─────────────────────┘
```

**Tablas de la base de datos:** `users`, `customers`, `products`, `categories`, `providers`, `lots`, `lot_stocks`, `sales`, `sale_lines`, `purchases`, `purchase_lines`, `stock_movements`, `inventory_adjustments`, `adjustment_lines`, `verification_codes`, `password_history`.

---

## Seguridad

### Autenticación JWT
- El token se genera al hacer login y se incluye en la cabecera `Authorization: Bearer <token>`.
- Expiración configurable (por defecto **24 horas**).
- El `JwtFilter` extrae y valida el token en cada petición, inyectando `userId`, `username` y `role` como atributos de la request.

### Autorización por roles
Se usa la anotación `@RequiresRole` a nivel de método o controlador, comprobada por `RoleInterceptor`. Los tres roles disponibles son:

| Rol | Permisos |
|---|---|
| `ADMIN` | Acceso total a todos los endpoints |
| `VENDOR` | Ventas, lotes, movimientos de stock y consulta de compras |
| `CUSTOMER` | Solo su propio perfil, sus ventas y sus líneas de venta |

### Gestión de contraseñas
- Hashing con **BCrypt** (factor de coste estándar, máx. 72 bytes).
- **Política:** `^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{8,72}$`
  - Mínimo 8 caracteres, al menos una mayúscula, una minúscula y un dígito.
- **Historial:** no se puede reutilizar ninguna de las **últimas 5 contraseñas**.
- **Cambio autenticado:** requiere verificación por email con código de 6 dígitos (válido 15 min, un solo uso).
- **Recuperación:** flujo seguro de dos pasos por email sin revelar si el correo existe en el sistema.

### Rate limiting
Limitador de tasa por IP implementado en memoria:
- **60 peticiones** por ventana de **60 segundos**.
- Responde `429 Too Many Requests` cuando se supera el límite.
- Limpieza automática de entradas expiradas cada 2 minutos.

### Verificación de email
Códigos de 6 dígitos con propósito específico (`EMAIL_VERIFICATION`, `PASSWORD_CHANGE`, `PASSWORD_RESET`). Cada código es de **un solo uso** y expira en **15 minutos**.

---

## Requisitos previos

| Herramienta | Versión mínima | Notas |
|---|---|---|
| JDK | 21 | OpenJDK o Temurin |
| PostgreSQL | 14+ | Crear la base de datos manualmente |
| Maven | 3.9+ | O usar `./mvnw` incluido |
| Cuenta SMTP | — | Gmail App Password recomendado |

---

## Instalación y configuración

### 1. Clonar el repositorio

```bash
git clone <repo-url>
cd ogc
```

### 2. Crear la base de datos en PostgreSQL

```sql
CREATE DATABASE ogc;
```

Hibernate creará el esquema automáticamente al arrancar (`ddl-auto=update`).

### 3. Configurar las variables de entorno

Copia el fichero de ejemplo y rellénalo:

```bash
cp .env.example .env
```

> Si no tienes `.env.example`, créalo con el contenido de la sección siguiente.

---

## Variables de entorno

| Variable | Requerida | Ejemplo | Descripción |
|---|---|---|---|
| `DB_URL` | ✅ | `jdbc:postgresql://localhost:5432/ogc` | URL JDBC de PostgreSQL |
| `DB_USERNAME` | ✅ | `postgres` | Usuario de la base de datos |
| `DB_PASSWORD` | ✅ | `mi_password` | Contraseña de la base de datos |
| `JWT_SECRET` | ✅ | `c2VjcmV0b211eXNlZ3Vybw==` | Clave HMAC para firmar JWT (mínimo 32 chars, recomendado Base64 de 64 bytes) |
| `MAIL_USERNAME` | ✅ | `app@gmail.com` | Cuenta de correo para envío de emails |
| `MAIL_PASSWORD` | ✅ | `abcd efgh ijkl mnop` | Contraseña de aplicación SMTP |
| `MAIL_HOST` | ❌ | `smtp.gmail.com` | Servidor SMTP (por defecto Gmail) |
| `MAIL_PORT` | ❌ | `587` | Puerto SMTP (por defecto 587) |

### Fichero `.env` de ejemplo

```env
# Base de datos
DB_URL=jdbc:postgresql://localhost:5432/ogc
DB_USERNAME=postgres
DB_PASSWORD=mi_password_seguro

# JWT — genera un secreto aleatorio seguro, por ejemplo:
# openssl rand -base64 64
JWT_SECRET=TU_SECRETO_SEGURO_MINIMO_32_CARACTERES

# Email SMTP
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=tu_app@gmail.com
MAIL_PASSWORD=abcd efgh ijkl mnop
```

> **Importante:** Nunca commitees el fichero `.env` con credenciales reales. Añádelo al `.gitignore`.

### Parámetros opcionales en `application.properties`

| Propiedad | Por defecto | Descripción |
|---|---|---|
| `jwt.expiration` | `86400000` (24 h) | Expiración del token JWT en milisegundos |
| `rate.limit.capacity` | `60` | Máximo de peticiones por ventana de rate-limit |
| `rate.limit.window-seconds` | `60` | Duración de la ventana de rate-limit en segundos |
| `rate.limit.cleanup-delay-ms` | `120000` | Intervalo de limpieza de entradas expiradas (ms) |
| `job.thread-pool.core-size` | `2` | Hilos mínimos del pool de tareas asíncronas |
| `job.thread-pool.max-size` | `4` | Hilos máximos del pool de tareas asíncronas |

---

## Ejecución

### Modo desarrollo

```bash
./mvnw spring-boot:run
```

### Modo producción (JAR)

```bash
# Compilar y empaquetar (sin tests)
./mvnw package -DskipTests

# Ejecutar
java -jar target/ogc-0.0.1-SNAPSHOT.jar
```

### Con perfil específico

```bash
java -jar target/ogc-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

La API queda disponible en **`http://localhost:8080`**.

---

## Tests

```bash
# Ejecutar todos los tests
./mvnw test

# Ejecutar un test específico
./mvnw test -Dtest=NombreDelTest

# Ver resultados en HTML
# target/surefire-reports/index.html
```

**Estado actual:** `153 tests — 0 fallos — BUILD SUCCESS`

Los tests siguen el patrón `@ExtendWith(MockitoExtension.class)` con **JUnit 5**, **Mockito** para mocks y **AssertJ** para aserciones fluidas. Cubren todas las capas de servicio y los casos límite de negocio (historial de contraseñas, verificación de códigos, acceso por rol, etc.).

---

## Módulos y funcionalidades

### Autenticación y gestión de usuarios (`/api/auth`, `/api/customers`)

| Funcionalidad | Descripción |
|---|---|
| Registro | Crea una cuenta de cliente con validación completa y envía email de verificación |
| Verificación de email | Activa la cuenta con el código de 6 dígitos recibido por email |
| Login | Devuelve un JWT válido 24 h |
| Cambio de contraseña autenticado | Flujo de dos pasos: solicitar código → confirmar con contraseña actual + código + nueva contraseña |
| Recuperación de contraseña | Flujo de dos pasos: solicitar código por email → confirmar con código + nueva contraseña |
| Historial de contraseñas | No se pueden reutilizar las últimas 5 contraseñas |
| Perfil de cliente | Consulta y actualización del propio perfil |

### Catálogo (`/api/products`, `/api/categories`, `/api/providers`)

| Funcionalidad | Descripción |
|---|---|
| Productos | CRUD con porcentajes de cannabinoides (CBD, THC, OH10, MS, Nano10, DeltaHC), stock calculado automáticamente |
| Categorías | CRUD con slug y descripción, con activación/desactivación |
| Proveedores | CRUD con datos de contacto, con activación/desactivación |
| Soft-delete | Los productos, categorías y proveedores se desactivan, nunca se eliminan físicamente |

### Ventas (`/api/sales`)

| Funcionalidad | Descripción |
|---|---|
| Crear venta | Asociada a un cliente, con fecha, notas y descuento opcional |
| Descuentos | Soporta descuento en porcentaje (`PERCENTAGE`) o importe fijo (`FIXED_AMOUNT`) |
| Líneas de venta | Cada línea referencia un lote, indica peso en kg y precio por kg |
| Trazabilidad | La creación de una línea genera automáticamente un `StockMovement` de tipo `SALIDA` |
| Control de acceso | CUSTOMER solo ve sus propias ventas; ADMIN y VENDOR ven todas |

### Compras (`/api/purchases`)

| Funcionalidad | Descripción |
|---|---|
| Orden de compra | Asociada a un proveedor, con fecha esperada y notas |
| Líneas de compra | Producto, peso pedido (kg) y precio por kg |
| Asignación de lote | Al recibir la mercancía, se asigna un lote existente a la línea de compra |
| Trazabilidad | La asignación genera automáticamente un `StockMovement` de tipo `ENTRADA` |

### Almacén e inventario (`/api/lots`, `/api/stock-movements`, `/api/inventory-adjustments`)

| Funcionalidad | Descripción |
|---|---|
| Lotes | Cada lote tiene producto, categoría, proveedor, peso total, fecha de caducidad y `LotStock` con peso restante |
| Movimientos de stock | Registro automático de `ENTRADA` (compra) y `SALIDA` (venta), filtrable por lote, compra, venta o tipo |
| Ajustes de inventario | Correcciones manuales de stock (positivos o negativos) con motivo y líneas por lote |

---

## Estructura del proyecto

```
d:\Proyectos\ogc\
├── src/
│   ├── main/
│   │   ├── java/com/ogc_prototype/ogc/
│   │   │   ├── config/
│   │   │   │   ├── JwtFilter.java              # Filtro que valida el JWT en cada request
│   │   │   │   ├── JwtUtils.java               # Generación y validación de tokens JWT
│   │   │   │   ├── RoleInterceptor.java         # Interceptor que comprueba @RequiresRole
│   │   │   │   ├── RequiresRole.java            # Anotación de control de acceso por rol
│   │   │   │   ├── PasswordManager.java         # BCrypt hash y verificación
│   │   │   │   └── RateLimitFilter.java         # Rate limiter por IP en memoria
│   │   │   ├── controller/
│   │   │   │   ├── clientes/
│   │   │   │   │   ├── AuthController.java      # /api/auth/**
│   │   │   │   │   └── CustomerController.java  # /api/customers/**
│   │   │   │   ├── catalogo/
│   │   │   │   │   ├── ProductController.java   # /api/products/**
│   │   │   │   │   ├── CategoryController.java  # /api/categories/**
│   │   │   │   │   └── ProviderController.java  # /api/providers/**
│   │   │   │   ├── ventas/
│   │   │   │   │   └── SaleController.java      # /api/sales/**
│   │   │   │   ├── compras/
│   │   │   │   │   └── PurchaseController.java  # /api/purchases/**
│   │   │   │   └── almacen/
│   │   │   │       ├── LotController.java       # /api/lots/**
│   │   │   │       ├── StockMovementController  # /api/stock-movements/**
│   │   │   │       └── InventoryAdjustment...   # /api/inventory-adjustments/**
│   │   │   ├── dto/
│   │   │   │   ├── request/                     # DTOs de entrada (validados con Jakarta)
│   │   │   │   └── response/                    # DTOs de salida (sin datos sensibles)
│   │   │   ├── exception/
│   │   │   │   └── AppException.java            # Excepción base + subclases con factory methods
│   │   │   ├── model/
│   │   │   │   ├── enums/
│   │   │   │   │   ├── Role.java                # ADMIN, VENDOR, CUSTOMER
│   │   │   │   │   ├── DiscountMode.java         # PERCENTAGE, FIXED_AMOUNT
│   │   │   │   │   ├── MovementType.java         # ENTRADA, SALIDA
│   │   │   │   │   └── VerificationCodePurpose  # EMAIL_VERIFICATION, PASSWORD_CHANGE, PASSWORD_RESET
│   │   │   │   └── *.java                       # Entidades JPA (User, Customer, Product, Sale...)
│   │   │   ├── repository/                      # Interfaces Spring Data JPA
│   │   │   └── service/                         # Interfaces + implementaciones de negocio
│   │   └── resources/
│   │       └── application.properties
│   └── test/                                    # 153 tests unitarios de servicios
├── pom.xml
├── mvnw / mvnw.cmd
└── README.md
```

---

## Referencia de la API

Consulta **[doc/API.md](doc/API.md)** para la referencia completa de todos los endpoints, incluyendo:
- Método HTTP, URL y requisitos de autenticación
- Cuerpo de petición con tabla de validaciones campo a campo
- Cuerpo de respuesta con ejemplos JSON reales
- Todos los códigos HTTP posibles y sus causas
- Enums de referencia
- Flujos de autenticación paso a paso recomendados para el frontend

---

## Contribuir

1. Haz fork del repositorio.
2. Crea una rama desde `main`: `git checkout -b feature/nombre-feature`.
3. Escribe tests para cualquier funcionalidad nueva o modificada.
4. Asegúrate de que `./mvnw test` pasa al 100%.
5. Abre una Pull Request con descripción clara del cambio.
