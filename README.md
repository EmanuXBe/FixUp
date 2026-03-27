<div align="center">
  <img src="https://github.com/user-attachments/assets/84ea4d47-cc9e-4342-a1e1-df4e50397cee" width="800"/>
</div>

FixUp es una aplicación en la cual un susuario podra rentar y remodelar apartamentos, tendra una gran variedad de categorias que podra seleccionar para solicitar servicios de remodelacion. A su misma vez dispondra de gran variedad de publicaciones de otros usuarios para que pueda encontrar la mejor opcion para dormir..
Descripción
FixUp es una aplicación que conecta usuarios que buscan apartamentos en renta con servicios de remodelación, permitiendo gestionar todo el proceso desde una sola aplicación. La app ofrece funcionalidades diferenciadas para clientes y administradores.
Registrarse e iniciar sesion como cliente o administrador.

<img width="1049" height="735" alt="image" src="https://github.com/user-attachments/assets/0dd3ca73-66ac-443d-8283-d5958985a3c1" />


¿Por que somos diferentes?
FixUp integra en una sola plataforma la renta de apartamentos y la solicitud de servicios de remodelación, ofreciendo una solución completa y eficiente tanto para clientes como para administradores.
* Los usuarios pueden encontrar apartamentos que se ajusten a sus necesidades específicas, visualizar fotos, reseñas, comentarios y precios antes de tomar una decisión. Esto les permite comparar opciones y elegir la mejor alternativa de forma rápida y segura. Ademas, la posibilidad de solicitar servicios de remodelación dentro de la misma aplicación garantiza que el espacio pueda adaptarse completamente a sus preferencias.
* Los administradores pueden publicar apartamentos y servicios de remodelación, gestionar solicitudes y llevar un control de las ganancias generadas. FixUp ofrece una plataforma segura y organizada para ampliar la visibilidad de sus servicios y optimizar su gestión.
Repositorio inicial para el proyecto FixUp con dos áreas principales:
* docs/: estructura para la documentación que se publicará en la wiki.
* flutter_app/: estructura base para el código fuente de la app en Flutter.
🛠️ Guía de Arquitectura y Sprint (Referencia para Auditoría IA)
Este proyecto sigue los lineamientos técnicos de los sprints de desarrollo móvil y backend definidos en el curso.
1. Arquitectura del Backend (API REST)
El backend está construido con Node.js, Express y PostgreSQL siguiendo una estructura de capas [sprint info, 338]:
* Models (models/): Definición de esquemas con Sequelize para Usuario, Artículo (Apartamento) y Review.
* Controllers (controllers/): Lógica de negocio para procesar peticiones y devolver respuestas JSON.
* Routes (routes/): Definición de endpoints (URLs) y métodos HTTP (GET, POST, PUT, DELETE).
* Relaciones: Un Usuario tiene muchas Reviews; un Artículo tiene muchas Reviews. La Review pertenece a ambos mediante llaves foráneas (userId, articleId) [sprint info, 363, 365].
* Seeding: Carga automática de datos iniciales en index.js mediante bulkCreate [sprint info, 349, 356].
2. Arquitectura Móvil (Android/MVVM)
Se implementa el patrón MVVM y Clean Architecture:
* UI/Screens: Composables encargados únicamente de la representación visual.
* ViewModels: Gestión del estado (UI State) y reacción a eventos mediante MutableStateFlow.
* Repositories: Orquestadores de datos que consumen de fuentes externas.
* Data Sources: Consumo de API REST mediante Retrofit.
* Patrones: Uso de DTOs (Data Transfer Objects) para el mapeo de datos y Hilt para Inyección de Dependencias.
3. Requerimientos del Sprint Actual
* CRUD completo de Reviews: Crear (asociado a usuario y artículo), leer por artículo/usuario, editar y eliminar [sprint info].
* Lectura de Usuarios y Artículos: Búsqueda por ID y listado general [sprint info].
* Sin Autenticación: En esta fase no se requiere seguridad tipo JWT o Firebase para el backend [sprint info].
Estructura de FixUp
.
├── .idea/
│   └── *.xml
├── app/
│   ├── build.gradle.kts
│   └── src/
├── docs/
│   ├── branding/
│   ├── diagrams/
│   │   ├── class/
│   │   └── entity-relationship/
│   ├── figma/
│   ├── requirements/
│   └── wiki/
├── flutter_app/
│   ├── assets/
│   ├── lib/
│   └── test/
├── gradle/
│   └── wrapper/
├── build.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
├── settings.gradle.kts
└── README.md
Ramas
* main (o flutter): Mantener aquí el código fuente de Flutter.
* Es toda la rama del proyecto, mantiene la version estable y lista para producción.
* Develop: Rama de integración, aca se unen todas las funcionalidades antes de pasar al main.
* Feature: Rama para desarrollar nuevas funcionalidades como el login.
* docs/wiki: Mantener aquí la documentación que se sincronizará con la wiki. Se puede mover la carpeta docs/ a la rama de documentación y dejar flutter_app/ en la rama de código.
Tecnologias
* Flutter (Android Studio)
* Kotlin
* Gradle
* Figma
* Express.js / Sequelize (PostgreSQL)
Equipo
* Juan Sebastian Rodriguez Pabon
* Emmanuel
* Violeta
* Mateo Madrigal
ANIMATE CAMARADA
INSTALA YAAA FixUp, no es solo una aplicación, es una solución diseñada para simplificar procesos y mejorar la experiencia del usuario. Estamos construyendo una plataforma escalable, moderna y enfocada en la eficiencia.
