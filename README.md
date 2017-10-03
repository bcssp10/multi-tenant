Spring Boot Hibernate Multi-tenant Demo
---------------------------------------
This sample project uses hibernate "SEPARATE DATABASE" multi-tenant strategy, and it also allows you to plugin new database (datasources) at runtime.

Useful resources 
[tech.asimio.net](http://tech.asimio.net/2017/01/17/Multitenant-applications-using-Spring-Boot-JPA-Hibernate-and-Postgres.html)
[fizzylogic.nl](https://fizzylogic.nl/2016/01/24/make-your-spring-boot-application-multi-tenant-aware-in-2-steps)
[stuartingram.com](http://stuartingram.com/2016/10/02/spring-boot-schema-based-multi-tenancy/)
[anakiou.blogspot.ch](http://anakiou.blogspot.ch/2015/08/multi-tenant-application-with-spring.html)

## Running the demo
You need to add a table orders in database (PostgreSQL in this case but ofcourse you can choose anyone)

```
CREATE TABLE "orders" (
"id" int4 NOT NULL,
"date" date NOT NULL
);
```

### Available URLs

```
curl -v POST -H "X-TenantID: DB1" "http://localhost:8080/orders"
curl -v POST -H "X-TenantID: DB2" "http://localhost:8080/orders"
curl -v POST -H "X-TenantID: DB3" "http://localhost:8080/orders"
```
