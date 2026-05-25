URL Shortener

A scalable URL shortening service inspired by Bitly/TinyURL.

Tech Stack
Java 17
Spring Boot
Maven
Planned Features
Short URL generation
Redirect handling
Expiration support
Analytics
Redis caching
Kafka async logging
Rate limiting

## Database Design

Stores:
- original URL
- short code
- creation time
- expiration time
- click count