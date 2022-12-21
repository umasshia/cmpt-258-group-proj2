# Read Me First
The following was discovered as part of building this project:

* The JVM level was changed from '11' to '17', review the [JDK Version Range](https://github.com/spring-projects/spring-framework/wiki/Spring-Framework-Versions#jdk-version-range) on the wiki for more details.

# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.0.0/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.0.0/maven-plugin/reference/html/#build-image)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/3.0.0/reference/htmlsingle/#using.devtools)


### Terminal based university database using Maven and Hibernate ORM

Please create the following database before executing

<code> 

    create database university;

    create table instructor(
        id varchar(5), 
        name varchar(20), 
        dept_name varchar(4), 
        primary key (id));
    
    create table department(
        dept_name varchar(4),
        location varchar(4), 
        budget varchar(10), 
        primary key(dept_name))

</code>