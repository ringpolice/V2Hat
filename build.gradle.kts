plugins {
    id("com.gradleup.shadow") version "9.0.0-beta2"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("java")
}


group = "net.df1015"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.celerry.com/releases")
    maven("https://jitpack.io")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation("com.github.stefvanschie.inventoryframework:IF:0.10.18")
    compileOnly("dev.kokiriglade:popcorn:3.3.5")
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}



tasks {
    runServer {
        minecraftVersion("1.21.1")
        downloadPlugins {
            hangar("popcorn", "3.3.5")
            github("MilkBowl", "Vault", "1.7.3", "Vault.jar")
            url("https://download.luckperms.net/1556/bukkit/loader/LuckPerms-Bukkit-5.4.141.jar")
        }
        doFirst {
            file("run/plugins/hats").deleteRecursively()
        }
    }
    shadowJar {
        relocate("com.github.stefvanschie.inventoryframework", "net.df1015.hats.lib.if")
    }
}
