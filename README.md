The coding task is to create a simple payment application. 

- You can use any front-end framework you want (e.g. React, Angular, Vue.js,
etc.). For the back end, you need to use Java 11+ with any framework of your
choice as well along with the database (MySQL, MongoDB, etc.).
- Your application should provide an API to create a payment, accepting first
name, last name, zip code, and card number. The card number should be
encrypted when stored on file.
- This application should be able to allow registering dynamic webhooks via
an API which will receive the corresponding endpoint HTTP to make a POST
request. This webhook should be called after each new payment, passing
details as Json content in the body. This process should be resilient to
failure.
- For API documentation, create an OpenAPI Specification and store it with
examples at the root of your project. Please ensure proper return codes
and meaningful information.
- Upload your completed project to your GitHub, and then share a link to your
repo with us in your reply to this email. Please include instructions on how
to run the application in your README.md.
- UI application is optional
- The expected time for the exercise is around 4 hours.
- You can use coding assistance services/copilots such as ChatGPT(we
encourage it) as needed. In case you do use them, please share the
transcript/link to the prompt along with your submission.

# Description
## Features
## How to Run
## API Documentation
## Architecture Decisions
### Hexagonal Architecture
### BDD Testing
### Queues and Resilience
## Future Improvements

- implement anti corruption layer: add a layer (dto object) between entities and database 
- 

# References
