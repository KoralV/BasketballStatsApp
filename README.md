Due to time constraints, I chose Spring MVC over WebFlux. While WebFlux supports higher concurrency with non-blocking I/O, it adds complexity and a learning curve. Spring MVC, though less scalable, is easier to implement and sufficient for handling dozens of concurrent requests.
For similar reasons, I chose PostgreSQL over Elasticsearch. While PostgreSQL supports joins and relational integrity, Elasticsearch could have offered a more scalable solution.
I chose Kafka to support high-throughput data ingestion and enable asynchronous background processing of player statistics.




