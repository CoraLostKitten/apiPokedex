# Error 401 en Login - Analisis y Solucion
## PROBLEMA IDENTIFICADO
Error: El usuario recibe error 401 UNAUTHORIZED al intentar acceder a endpoints protegidos despues de hacer login.
## CAUSA RAIZ
### 1. JWT Secret Key se Regenera en Cada Reinicio
En JwtService.java (linea 19), la clave secreta se generaba aleatoriamente:
- Iniciar app -> genera clave aleatoria A
- Usuario hace login -> token firmado con clave A
- App reinicia -> genera clave aleatoria B (DIFERENTE)
- Validar token -> falla porque firma no coincide
- Resultado: Error 401 UNAUTHORIZED
### 2. Conflicto en las Rutas de Seguridad
- SecurityConfig permitia: /auth/**
- AuthController exponía: /api/auth/**
- Inconsistencia causaba conflictos
## SOLUCION IMPLEMENTADA
### 1. Persistencia de la Clave JWT
En application.properties se agregaron:
`
jwt.secret.key=bXktc2VjcmV0LWp3dC1rZXktZm9yLXBva2VkZXgtYXBpLWlzLXZlcnktbG9uZy1hbmQtc2VjdXJl
jwt.token.expiration=86400000
`
En JwtService.java, el constructor ahora inyecta propiedades:
`
public JwtService(@Value("\") String secretKeyBase64,
                 @Value("\") long tokenExpiration) {
    byte[] decodedKey = Base64.getDecoder().decode(secretKeyBase64);
    this.secretKey = Keys.hmacShaKeyFor(decodedKey);
    this.tokenExpiration = tokenExpiration;
}
`
### 2. Alineacion de Rutas
En SecurityConfig.java:
- ANTES: .requestMatchers("/auth/**").permitAll()
- AHORA: .requestMatchers("/api/auth/**").permitAll()
En AuthController.java:
- @RequestMapping("/api/auth") ahora es consistente
## FLUJO DE FUNCIONAMIENTO
1. Cliente -> POST /api/auth/login
   - Envia email + password
2. AuthController.login()
   - Autentica con AuthenticationManager
   - Genera JWT con clave PERSISTENTE
   - Retorna token + datos usuario
3. Cliente almacena token en localStorage
   - Token: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
4. Cliente -> GET /api/entrenadores
   - Header: "Authorization: Bearer {token}"
5. SecurityFilterChain valida
   - Decodifica token con MISMA clave (persistente)
   - Verifica firma
   - Extrae roles
   - Autentica al usuario
6. Endpoint procesa request
   - Acceso permitido OK
## COMO PROBAR
Test 1: Verificar Persistencia
1. mvn spring-boot:run
2. POST /api/auth/login -> obtener token
3. GET /api/entrenadores -> debe funcionar
4. Detener app (Ctrl+C)
5. Reiniciar app
6. GET /api/entrenadores con token anterior -> DEBE FUNCIONAR AHORA
Test 2: Verificar Rutas
- POST   /api/auth/login   OK
- POST   /api/auth/register  OK
- GET    /api/entrenadores  OK (con token)
- GET    /api/pokemon/1     OK (publico)
## ARCHIVOS MODIFICADOS
1. application.properties
   - Agregadas propiedades JWT persistentes
2. JwtService.java
   - Constructor inyecta propiedades
   - Lee clave secreta desde @Value
   - Usa tokenExpiration configurable
3. SecurityConfig.java
   - Cambio de /auth/** a /api/auth/**
4. AuthController.java
   - Confirmado @RequestMapping("/api/auth")
## NOTAS IMPORTANTES
Produccion:
- Cambiar clave JWT por una mas segura
- Usar variables de entorno
- Reducir jwt.token.expiration a 1-2 horas
- Implementar refresh tokens
Desarrollo:
- Clave actual es segura para development
- 24 horas de expiracion es comodo para testing
- Persiste entre reinicios
CORS:
- Verificar que http://localhost:5173 es correcto
- Si usa otro puerto, cambiar en SecurityConfig linea 67
## VERSION
- Version: 1.1
- Ultima Actualizacion: 2026-04-09
- Estado: Solucion Implementada
- Problema Resuelto: Error 401 en Login
Si todo funciona -> Error resuelto!
