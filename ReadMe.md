# Microservice Application


## Technologies:
* Java 17
* Spring Boot, MVC
* Spring Security 7
* Spring Data JPA
* H2
* Bootstrap
* Circuit Breaker, Retry Spring
* Config Server Service
* Microservices: Gateway, Discovery Service, Phone Code Service, Contact Service, Identity Service (Spring Security, token)
  * Phone Code Service (id, code, country)
  * Contact Service (id, phone_id, phoneNumber, email, ...)



### FUNCTIONALITY/FEATURES:
* Токен упрощенно можно получить запустив класс src/main/java/com/javaguru/gateway/util/Token.java
* Запросы:
  * Создание сущности в таблице h2 МС Phone Code Service:
    * POST http://localhost:8080/codes
    {
    "id": "1",
    "code": "12",
    "countryId": 45
    }
  * Получение id сущности из таблицы h2 МС Phone Code Service по code:
    * GET http://localhost:8080/codes/1
  * Получение code сущности из таблицы h2 МС Phone Code Service по id:
    * GET http://localhost:8080/codes?code=12
 
  * Создание сущности в таблице h2 МС Contact Service:
    * POST http://localhost:8080/contacts
    {
    "cvsId": "1",
    "phoneCode": "12",
    "phoneNumber": "23-456-446-85-85",
    "email": "ya@gmail.com"
    }
  * Получение сущности из таблицы h2 Contact Service по id: 
    * GET http://localhost:8080/contacts?cvsId=1

  * Получение журнала событий CircuitBreaker
  * http://localhost:8001/actuator/circuitbreakerevents

  * Получение поля configName класса PropertyController используя Server Config из папки CONTACT-SERVICE на GitHub
    * GET http://localhost:8001/properties/get2
 
  * Получение встроенного json ответа от Server Config с информацией об удачном подключении к УР и данных, найденных в
  * application.yml или application.properties, лежащего в корневой директории на УР
  * GET http://localhost:8004/properties/get

 

### Additional info

