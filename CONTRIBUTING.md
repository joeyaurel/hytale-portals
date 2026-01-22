# Contribution

Please read these guidelines carefully before contributing to this project.

## Conventional Commits

The project uses [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/)
to generate changelogs. Please make sure to follow the convention when committing!

## Requirements

Please ensure all the requirements are met before getting started.

1. Download the latest `HytaleServer.jar`. See below for a script to do this.
2. Have Intellij IDEA installed. Community edition is fine.
3. Download Java 25 and set it as the SDK in IDEA.

## Downloading the latest HytaleServer.jar

Have Docker installed and run the following command in the root of the project:

```shell
docker run --rm -it -v "$(pwd)/server:/app/server" alpine:latest sh -c "\
    apk add --no-cache curl unzip libc6-compat && \
    curl -L -o hytale-downloader.zip https://downloader.hytale.com/hytale-downloader.zip && \
    unzip hytale-downloader.zip && \
    chmod +x hytale-downloader-linux-amd64 && \
    ./hytale-downloader-linux-amd64 -download-path server.zip && \
    unzip -p server.zip Server/HytaleServer.jar >/app/server/HytaleServer.jar"
```

## Manifest file

The manifest file provides important information about the plugin to Hytale.
One should update every property in this file to reflect the project at hand.
The most important property to set is `Main` which tells the game which class
file to load as the entry point for the plugin. The file can be found at
`src/main/resources/manifest.json`.

**This project has configured Gradle to automatically update the `Version` and
`IncludesAssetPack` property to reflect the Gradle properties every time one
runs the game in development, or builds the plugin. This is a workaround to allow
the in-game asset editor to be used when working on the project.**

## Importing into IDEA

When opening the project in IDEA it should automatically create the
`HytaleServer` run configuration and a `./run` folder. When you run the game it
will generate all the relevant files in there. It will also load the default
assets from the games.

**If you do not see the `HytaleServer` run configuration, you may need to open
the dropdown or click `Edit Configurations...` once to unhide it.**

## Importing into VSCode

While VSCode is not officially supported, you can generate launch configs by
running `./gradlew generateVSCodeLaunch`.

## Connecting to Server

Once the server is running in IDEA you should be able to connect to
`Local Server` using your standard Hytale client. If the server does not show
up automatically, add the IP as `127.0.0.1` manually.

## You MUST authenticate your test server!

In order to connect to the test server, you must authenticate it with Hytale.
This is done by running the `/auth login device` command in the server terminal.
This command will print a URL that you can use to authenticate the server using
your Hytale account. Once authenticated, you can run the
`/auth persistence Encrypted` command to keep your server authenticated after
restarting it.

**Never share your encrypted auth file!**

If you are unable to run commands from the IDEA terminal, you can also run the
command from code like this. Make sure to remove the code after your server is
authenticated.

```java
    @Override
    protected void start() {
        CommandManager.get().handleCommand(ConsoleSender.INSTANCE, "auth login device");
    }
```
