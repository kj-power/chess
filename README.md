# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
## Chess Server Design

https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYHSQ4AAaz5HRgyQyqRgotGMGACClHDCKAAHtCNIziSyTqDcSpyvyoIycSIVKbCkdLjAFJqUMBtfUZegAKK6lTYAiJW3HXKnbLmcoAFicAGZuv1RupgOTxlMfVBvGUVR07uq3R6wvJpeg+gd0BxMEbmeoHUU7ShymgfAgECG8adqyTVKUQFLMlbaR1GQztMba6djKUFBwOHKBdp2-bO2Oaz2++7MgofGBUrDgDvUiOq6vu2ypzO59vdzawcvToCLtmEdDkWoW1hH8C607s9dc2KYwwOUqxPAeu71BAJZoJMIH7CGlB1hGGDlAATE4TgJgMAGjLBUyPFM4GpJB0F4as5acKYXi+P4ATQOw5IwAAMhA0RJAEaQZFkyDmGyv7lNUdRNK0BjqAkaAJqqKCzC8bwcAcv5sl+zo9FJMn6K8SwAuc35mg25QIKxPKwixbGouisQwIZZkoGiGLYnehgrkSa5khSg5SaOLlnpOHLcryVqCsKOZDIBJ7eSaYaOuajbztai6OWyXYsr2-YoNee5EceyUToUF6zi6h63jFdbKdmpk8pEqgfpgZU-sUKnYaFuHAfhXxESRpateRCEguGPEoTA6GYb0TW3IBZEEX0HVQV1cFlmYlGeN4fiBF4KDoMxrG+MwHHpJkmDIcweklAJ0g+kxPr1D6zQtKJqjid0M3Qb1Sk6Spz3oNpQKISd4JWdtO4mYDYDmRiAP2DtYOxA5JXOUy3ZuWAGX7oenVoF5CORXlfk8leRXaEKYSfWg4VYxOf0WoVN4JXDBKnilRgoNwW6HqjEGzRjZPjqyvmlNIzMUoYGXFQ2pXveVINVTVdVRfWp0wD0CkNX1hRHWhGEJhRlbLTRgSQnOTHQjAADigGsntXGHQNx3RdQzoVCbV23fYgFPWjnOvQ+EtXCT31PnxMUGdCJnG9DzDILE4ew2L8M80jKMk5jPPnrjvIi4TwUk9za71fp1NHrTsf0xFpJWdCZtJrCye53z-lzpXKCi1QHbez95XQtLCCfhLecKz0Uyu0maYVP0Q8oAAktIaYAIyobGUZPJxA44fcfRfDoCCgNKVp3CsXzjwAcoBExljAjTK-bqvwDbGsjQPfTj6oI9j4BU+z-Pi9TMvKC7xNp9PE3tvP+uEAGD0AsfUYp89jn21lRFatFsA+CgNgbg8A0qm0Aikfa3Eci23lg7WoDQXZu2CB7aCCYj6AUvqGQoZUrhUNGP7XSdsW5Uw3HIFAsI4BpXDjADhmRo5LiciXcmPYYDkjAI3WEjCUA1x8jjLkeNMGjCCmEWROcfKU1io3ZuHZRHx34WlaRsj5HY3ZNOAquii5sKcm3J85QeGbhQF3Hu7c+4qXAaMd+5Q54LxgDQ36-U8F3wTF4ye09fGfwCYtHW1FVoBEsMzQyyQYAACkIA8hUYYAIQCQDSmtngwOV8BJVEpMJFo493Ycwob0VBwAklQDgBAQyUBZjjyngcSppCSaUIgdQr2dCfaK1kcw36rD-oACtMloFhBkyqtkLLMGmQsuyMNhFJQZmXSRJj+mjDMblCx9dslqIkXssQOVebaPKNY+QGy46uRVNgLQmRdmjEgXIzR5j8oN0Ano+8Qz27lHmWgVxtVe5y34orQJ191ZDU1r0OBusEleAaTfd0sBgDYFQYQeIiRsFWyOsUhWFRzqXWurdYwgyzhAuheC9x1ymYsy4QLZlQjEoPMRvw7geBq5fMOVOVlQsTmLkuanfmgtMgFxtGKvmQqpWBUXBWTAQA
