- TextBasedSocialMedia API is created using Maven, Java, Hibernate, Postgresql, Javalin and flyway (for automated database schema generation). Moreover lombok , Google Gson, Apache HttpClient, Hibernate Validator are used. 

- Software design of TextBasedSocialMedia API demonstrates Hexagonal architecture

- Before using the API , create database textbasedsocialmediadb

- In order to generate database schema with flyway (and insert a few pre registered users in db needed by the tests) run in project root "mvn clean flyway:migrate -Dflyway.configFiles=flywayConfig.conf"

- All REST endpoints and the corresponding controller methods that are invoked uppon triggering, can be found in ai.datawise.textbasedsocialmedia.app.utils.InputControllerEndPoints

- Each http POST controller method consumes json as input in the http body of the request, which is mapped to a Command instance which can be found 
in package "ai.datawise.textbasedsocialmedia.app.usercases.application.ports.in.model" and produces json ouput which is modeled by a Response instance that can be found 
in package "ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.responses" 

- Each http GET controller method accepts a path parameter (in url) as input and produces json output (array of json objects) which is modeled by Views instances/lists which can be found in package
ai.datawise.textbasedsocialmedia.app.usercases.application.domain.model.views

- There are actual examples for each REST endpoint use under controller tests folder 
"\src\test\java\ai\datawise\textbasedsocialmedia\app\usercases\adapters\in\web\"

- API can start accepting requests by running the main method, which starts all REST endpoints.

- In order to build and run all tests , run "mvn clean install"
