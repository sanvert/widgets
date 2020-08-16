# Widgets

To run project the command below can be used.

`mvn spring-boot:run`

There are two main endpoints such as "widgets" and "rateLimit", widgets handles all required 
widget operations and rateLimit is used to update limit for each operation which all maps to a 
specific endpoint. Default rate limit is 250 with 1 minute refresh period.

Headers set related to rate limit like below.
X-Rate-Limit :"250""
X-Rate-Limit-Remaining :"245"
X-Rate-Limit-Time-To-Refresh :"0"

Aside rate limit, pagination implemented as another optional requirement.

Test coverage is above 30% as it is stated in requirements document.

Swagger UI provided to test endpoints via http://localhost:8080/swagger-ui/