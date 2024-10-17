# Url shortener service
## Overview
The URL Shortener Service is a microservice designed to enable users to convert lengthy referral links from our application into shortened, compact URLs. This feature is particularly beneficial for sharing links on social media, where long URLs can be cumbersome and visually unappealing.
## Features
- While the primary function of the service is URL shortening, it also offers several advanced features, including:
- Caching: Utilizes both local (in-memory) caching and Redis to enhance performance.
- Multithreading: Supports concurrent handling of multiple requests for efficient processing.
- Database Integration: Compatible with both Postgres and Redis databases.
- REST API: Provides a straightforward RESTful interface for seamless interaction.
- Resource Reuse Scheduler: Enhances resource management by scheduling the reuse of resources.
## Performance Optimization
To further enhance the performance of the microservice, I have implemented a local cache in memory using a thread-safe data structure. This significantly boosts overall efficiency and reduces latency.
## Microservice Interaction
The main service will interact with the URL Shortener via its REST API, facilitating genuine microservice-based communication. This architecture enables us to tackle common challenges in microservice environments, such as scalability, reliability, and service discovery.
