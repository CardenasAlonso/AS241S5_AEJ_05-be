# AS241S5_AEJ_05-be

Microservicio Spring WebFlux reactivo que consume 2 APIs de Inteligencia Artificial para procesamiento de voz, captura los resultados y los almacena en una base de datos cloud (MongoDB Atlas).

## 1. APIs de Inteligencia Artificial

### API 1 — ZeroBounce (Validador de emails)
- **Fuente**: zerobounce.net
- **Descripción**: Valida direcciones de correo electrónico en tiempo real y detecta si son válidas, inválidas o temporales usando machine learning.
- **Autenticación**: API Key (query parameter)
- **Endpoint**: GET `/v2/validate?api_key={KEY}&email={EMAIL}`
- **Respuesta**: Status del email (valid, invalid, catch-all, spamtrap, etc.)

### API 2 — ElevenLabs (Síntesis de voz e efectos de sonido)
- **Fuente**: elevenlabs.io (via RapidAPI)
- **Descripción**: Genera audio de alta calidad a partir de texto (TTS - Text-to-Speech) y crea efectos de sonido mediante IA.
- **Autenticación**: RapidAPI Key + Host header
- **Endpoints propios**:
  - **TTS**: POST `/v1/text-to-speech/{voiceId}`
  - **Sound Effects**: POST `/v1/sound-effects`
- **Respuesta**: Audio en base64 (formato MP3 o WAV)

## 2. Herramientas y versiones

- **Java**: JDK 17
- **IDE**: Visual Studio Code / IntelliJ IDEA
- **Maven**: Apache Maven 3.x
- **Framework**: Spring Boot 3.5.13
- **Base de datos cloud**: MongoDB Atlas
- **Swagger**: SpringDoc OpenAPI 2.5.0
- **Tiempo real**: Project Reactor / Spring WebFlux

## 3. Dependencias Maven

```xml
<!-- Spring WebFlux -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>

<!-- MongoDB Reactivo -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb-reactive</artifactId>
</dependency>

<!-- Swagger / OpenAPI -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webflux-ui</artifactId>
    <version>2.5.0</version>
</dependency>

<!-- Lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<!-- Reactor Test -->
<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-test</artifactId>
    <scope>test</scope>
</dependency>
```

## 4. Endpoints disponibles

### ZeroBounce — Validación de emails
| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/v1/zerobounce/validate` | Validar email y guardar resultado |

**Body**:
```json
{
  "email": "usuario@ejemplo.com"
}
```

### ElevenLabs — Síntesis de voz y efectos de sonido
| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/v1/elevenlabs/tts` | Generar voz desde texto |
| POST | `/api/v1/elevenlabs/sound-effect` | Generar efecto de sonido |
| POST | `/api/v1/elevenlabs/sound-effect/play` | Generar y reproducir efecto de sonido |

**Body TTS**:
```json
{
  "text": "Hola, este es un mensaje de prueba"
}
```

**Body Sound Effect**:
```json
{
  "prompt": "Spacious brass suitable for high-impact movie trailer moments",
  "durationSeconds": 5,
  "promptInfluence": 0.3
}
```

## 5. Configuración

Todas las credenciales se configuran en `src/main/resources/application.yaml`:

```yaml
server:
  port: 8080

spring:
  application:
    name: vallegrande
  data:
    mongodb:
      uri: mongodb+srv://user:password@cluster.mongodb.net/database?ssl=true

apis:
  zerobounce:
    url-validate-email: https://api.zerobounce.net/v2
    key: ${ZEROBOUNCE_KEY}
  elevenlabs:
    url-text-to-speech: https://api.elevenlabs.io/v1
    url-sound-effects: https://elevenlabs-sound-effects.p.rapidapi.com/generate-sound
    key: ${ELEVENLABS_KEY}
    rapidapi-key: ${RAPIDAPI_KEY}
    rapidapi-host: elevenlabs-sound-effects.p.rapidapi.com
```

## 6. Swagger UI

Disponible en: `http://localhost:8080/swagger-ui.html`

---

**Autor**: Rikardo Hancel  
**Última actualización**: Abril 2026