plugins {
    id("java")
    id("xyz.jpenilla.run-paper") version "2.3.1"
}


group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.celerry.com/releases")
    maven("https://jitpack.io")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("dev.kokiriglade:popcorn:3.3.4")
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    implementation("net.kyori:adventure-text-minimessage:4.17.0")

}
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}
tasks {
    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("1.21.1")
        downloadPlugins {
            hangar("popcorn", "3.3.4")
        }
    }
}
tasks.test {
    useJUnitPlatform()
}
