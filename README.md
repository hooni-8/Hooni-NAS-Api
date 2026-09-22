# Hooni Base API

Auth API가 발급한 JWT Access Token을 검증하여 일반 서비스 API를 빠르게 시작할 수 있는 Spring Boot 템플릿입니다.

## 포함 기능

- Java 17 / Spring Boot 3.5
- Spring Security 기반 Stateless JWT 인증
- `@AuthenticationPrincipal DefaultUserInfo` 사용자 정보 주입
- JWT 서명, 알고리즘, 만료, subject, issuer, audience 및 필수 Claim 검증
- `role` Claim을 Spring Security Authority로 등록
- 공통 API 응답 및 예외 응답
- Controller 반환값을 `code`, `message`, `data`로 자동 변환
- Swagger/OpenAPI
- PostgreSQL, MyBatis 기반 구성
- 인증 흐름 통합 테스트

## 실행

Windows:

    .\gradlew.bat bootRun

macOS/Linux:

    ./gradlew bootRun

기본 포트는 `8200`입니다.

Auth API가 `http://localhost:8080`에서 실행되어야 JWT 공개키를 조회할 수 있습니다.

- 공개 상태 확인: `GET /api/v1/health`
- 인증 사용자 확인: `GET /api/v1/example/me`
- DB/MyBatis 확인: `GET /api/v1/example/database-time`
- Swagger UI: `/swagger-ui.html`

## JWT 계약

Auth API, Gateway 및 이 API의 다음 JWT 계약이 맞아야 합니다.

- 알고리즘: `RS256`
- `JWT_ISSUER`
- `JWT_AUDIENCE`
- Auth API JWKS 주소

개인키는 Auth API에만 존재합니다. 이 API는 `AUTH_API_JWK_SET_URI`에서 받은 공개키만
사용하므로 별도의 JWT 비밀키를 보유하지 않습니다.

Access Token에는 다음 값이 필요합니다.

- subject: `accessToken`
- `jti`, `exp`, `iss`, `aud`
- `userCode`, `role`
- `userName`은 선택값

Gateway를 통과하는 경우에도 API가 검증할 수 있도록 원본 Access Token이 `Authorization: Bearer ...` 헤더로 전달되어야 합니다.

## Controller에서 사용자 정보 사용

    public AuthenticatedUserResponse example(
            @AuthenticationPrincipal DefaultUserInfo userInfo
    ) {
        return exampleService.getAuthenticatedUser(userInfo);
    }

Controller는 DTO나 `Map`을 그대로 반환합니다. `ApiResponseAdvice`가 JSON 응답을
`code`, `message`, `data` 구조로 자동 변환합니다. 파일, 바이트 배열, 스트리밍 및
문자열 응답은 콘텐츠가 손상되지 않도록 자동 변환 대상에서 제외됩니다.

공개 경로에서는 Principal이 없을 수 있습니다. `DefaultUserInfo`를 사용하는 API는 인증 경로로 유지하세요.

## 주요 환경변수

| 변수 | 설명 |
|---|---|
| `SERVER_PORT` | API 포트 |
| `DB_URL` | PostgreSQL JDBC URL |
| `DB_USERNAME` | DB 계정 |
| `DB_PASSWORD` | DB 비밀번호 |
| `JWT_ALGORITHM` | RS256 |
| `JWT_ISSUER` | 토큰 발급자 |
| `JWT_AUDIENCE` | 토큰 대상 서비스 |
| `AUTH_API_JWK_SET_URI` | Auth API의 JWKS 공개키 주소 |

기본 JWKS 주소는 `http://localhost:8080/oauth2/jwks`입니다.

## 새 프로젝트로 복사할 때

1. `settings.gradle`의 프로젝트명을 변경합니다.
2. `spring.application.name`과 기본 포트를 변경합니다.
3. 필요하면 `org.hooni.api` 패키지를 서비스 전용 패키지로 변경합니다.
4. 예제 Controller, Service, DTO를 실제 도메인으로 교체합니다.
5. 운영 환경변수를 배포 시스템에서 주입합니다.

## 검증

    .\gradlew.bat test
