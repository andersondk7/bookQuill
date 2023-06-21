# learning Quill 

## Purpose
This project is a proof of concept work to encapsulate database access using zio and quill.


## Goals
 - investigate how zio and quill work
 - investigate possible code organization patterns
 - investigate test approaches
 - learn more about integration with Postgres

## Database
This project uses a database hosted on a postgres server.  

Instructions on how to set up a local docker instance of postgres are found [here](localPostgres.md)

The database is called book_biz and represents a fictitious publishing company.  

### Environment
The following environment variables are required:
- *BZ_USER* -- the username for access to the book_biz database
- *BZ_PASSWORD* -- the password for access to the book_biz database
- *BZ_SCHEMA* -- the schema in the book_biz database

this schema is typically:
- *local* for individual testing
- *dev* for the shared development environment
- *qa* for separate qa testing
- *prod* for production deployments

## Code structure

## Performance tests