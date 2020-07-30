## RC REST endpoint docs

Swagger UI
- http://localhost:8080/swagger-ui/index.html

OpenAPI descriptor
- http://localhost:8080/v3/api-docs.yaml


## Testing RC

Tests are self-contained and the is a docker-compose environment to deploy the unit and integration tests.

To execute the tests got to testing resources directory
```bash
cd src/test/resources
```

Launch the docker-compose environment
```bash
docker-compose up
```

Launch the unit tests (either using the IDE or from the command line as follows) from project root directory
```bash
sh mvnw test
```

Delete the testing environment
```bash
docker-compose down --volumes
```

## Copyright

This work has been done by Telcaria Ideas S.L. for the 5G EVE European project under the [Apache 2.0 License](LICENSE).
