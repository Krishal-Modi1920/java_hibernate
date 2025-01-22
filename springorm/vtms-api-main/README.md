# Tour Management System

## Prerequisites

- OpenJDK 17 - You can download it [from this link](https://adoptopenjdk.net/releases.html)
- Postgres server - For MacOS, you can use [Homebrew](https://wiki.postgresql.org/wiki/Homebrew) or [Postgres.app](https://postgresapp.com/)
- Postgres client - [PgAdmin4](https://www.pgadmin.org/download/)

## Build service locally
### Using CLI
#### Build artifacts, run tests and perform checks
`./gradlew build`

#### Automatically re-executes build task when there are new changes
`./gradlew build --continuous`

#### Run Tests
`./gradlew test`

#### Automatically re-executes tests when there are new changes
`./gradlew test --continuous`

#### Run checkstyle and spotbugs checks
`./gradlew check`


## Run service locally

1. Run PostgreSQL server locally at port `5432`.

2. Configure pgAdmin4 to connect to Postgres to help you manage your DB.

3. Create a Postgres user. Find the username and password to use in `src/main/resources/application-local.yml`. Mark the user as `Can log in` in the UI.

4. Create the Postgres DB. Find the DB name to use in the same file

5. Grant the user you created `ALL` access to the DB.

3. Then, run:

```
SPRING_PROFILES_ACTIVE=local ./gradlew bootRun
```

The command above will automatically run SQL migrations (using Flyway). Then it will start a (Tomcat) web server at http://localhost:8080.

## Dependency Version Upgrades

Dependencies can be upgraded to their newer versions manually by editing `build.gradle` or by merging the PRs created by dependabot that looks for newer versions of dependencies daily. There are several measures we need to take before performing any dependency upgrades.

### Process to upgrade dependencies
1. Check that the dependency version does not exist in [dependency-version-upgrades-to-avoid](#dependency-version-upgrades-to-avoid) section below.
1. Read thru all the changelog/releaselog from the existing dependency version in `build.gradle` to the latest version of the dependency we are trying to upgrade to, for any breaking changes that might affect us.
1. Upgrade the dependency version in code manually or pull the branch created by the dependabot locally (the latter is preferred).
1. Build the package with the changes and verify that the build succeeds without any warnings and errors.
1. Start the service locally and verify that the server starts up without any warnings and errors.
1. If there are no issues when running the service, the changes are good to merge. Please create a PR or if the PR already exists, get your peers to review and approve it.
1. If there are issues in any of the above steps, update the [dependency-version-upgrades-to-avoid](#dependency-version-upgrades-to-avoid) section below with details so other engineers are aware of it.

#### Dependency Version Upgrades to Avoid
_Add backwards incompatible dependencies that we should avoid upgrading to here_


### Gradle

#### How to upgrade gradle?
> Make sure to update the gradle version in `Jenkinsfile` as well

`./gradlew wrapper --gradle-version=<version>`



