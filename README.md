# Student Registration System

Professional Spring Boot student registration system with:

- PostgreSQL persistence
- HQL data access (via `EntityManager`)
- Clean MVC routes (no template filename in URL)
- Internationalization: English (`en`), Kinyarwanda (`rw`), French (`fr`)

## Tech Stack

- Java 17
- Spring Boot 3
- Spring MVC + Thymeleaf
- Spring Data JPA / Hibernate
- PostgreSQL

## Run

1. Create PostgreSQL database:
   - `student_registration`
2. Update DB credentials in `src/main/resources/application.properties` if needed.
3. Start the app:
   - `mvn spring-boot:run`
4. Open:
   - `http://localhost:8080/students`

## Language Switching

Use `lang` query parameter:

- `http://localhost:8080/students?lang=en`
- `http://localhost:8080/students?lang=rw`
- `http://localhost:8080/students?lang=fr`

Language selection is stored in a cookie.

