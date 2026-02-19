<div align="center">
  <img src="https://github.com/user-attachments/assets/84ea4d47-cc9e-4342-a1e1-df4e50397cee" width="300"/>
</div>

FixUp es una aplicación en la cual un susuario podra rentar y remodelar apartamentos, tendra una gran variedad de categorias que podra seleccionar para solicitar servicios de remodelacion. A su misma vez dispondra de gran variedad de publicaciones de otros usuarios para que pueda encontrar la mejor opcion para dormir..

## Descripción
FixUp es una aplicación que conecta usuarios que buscan apartamentos en renta con servicios de remodelación, permitiendo gestionar todo el proceso desde una sola aplicación. La app ofrece funcionalidades diferenciadas para clientes y administradores.

Registrarse e iniciar sesion como cliente o administrador.

<img width="1049" height="735" alt="image" src="https://github.com/user-attachments/assets/0dd3ca73-66ac-443d-8283-d5958985a3c1" />


## ¿Por que somos diferentes?
FixUp integra en una sola plataforma la renta de apartamentos y la solicitud de servicios de remodelación, ofreciendo una solución completa y eficiente tanto para clientes como para administradores.

- Los usuarios pueden encontrar apartamentos que se ajusten a sus necesidades específicas, visualizar fotos, reseñas, comentarios y precios antes de tomar una decisión. Esto les permite comparar opciones y elegir la mejor alternativa de forma rápida y segura. Ademas, la posibilidad de solicitar servicios de remodelación dentro de la misma aplicación garantiza que el espacio pueda adaptarse completamente a sus preferencias.

- Los administradores pueden publicar apartamentos y servicios de remodelación, gestionar solicitudes y llevar un control de las ganancias generadas. FixUp ofrece una plataforma segura y organizada para ampliar la visibilidad de sus servicios y optimizar su gestión.

Repositorio inicial para el proyecto FixUp con dos áreas principales:

- `docs/`: estructura para la documentación que se publicará en la wiki.
- `flutter_app/`: estructura base para el código fuente de la app en Flutter.

## Estructura de FixUp

```
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

```

### Ramas

- **main** (o `flutter`): Mantener aquí el código fuente de Flutter.
- Es toda la rama del proyecto, mantiene la version estable y lista para producción.
- **Develop**: Rama de integración, aca se unen todas las funcionalidades antes de pasar al main.
- **Feature**: Rama para desarrollar nuevas funcionalidades como el login
- **docs/wiki**: Mantener aquí la documentación que se sincronizará con la wiki.
Se puede mover la carpeta `docs/` a la rama de documentación y dejar `flutter_app/` en la rama de código.
