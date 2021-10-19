# ModernBukkit
###### An annotation based plugin framework for bukkit.
___
## Table of contents
- [Why should I use ModernBukkit](#why-should-i-use-modernbukkit)
- [Code comparison](#code-comparison)
  - [Commands](#commands)
- [Getting started](#getting-started)
  - [Maven](#example-for-maven)
  - [Gradle (groovy)](#example-for-gradle-groovy)
  - [Gradle (kotlin)](#example-for-gradle-kotlin)


## Why should I use ModernBukkit?
___
ModernBukkit helps you to achieve your goals with less code
and makes your code cleaner and easier to understand.
Furthermore it is actively developed and supported.


## Code comparison
___
### Commands
#### Code without using ModernBukkit
```java
public class HelloWorldCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length >= 1) {
            if(args[0].equals("arg1")) {
                if(sender.hasPermission("system.test.arg1")) {
                    sender.sendMessage("Hello, " + sender.getName());
                }else {
                    sender.sendMessage("§cYou do not have the permission system.test.arg1");
                }
                return false;
            }
        }
        if(sender.hasPermission("system.test")) {
            sender.sendMessage("Hello, World!");
        }else {
            sender.sendMessage("§cYou do not have the permission system.test");
        }
        return false;
    }
}
```

#### Code using ModernBukkit
_With this code we are getting tabcompletion too._
```java
import de.neo.modernbukkit.command.Command;
import de.neo.modernbukkit.command.Subcommand;
import de.neo.modernbukkit.command.TabCompletion;
import org.bukkit.command.CommandSender;

public class TestCommand {

    @Command(command = "test", permission = "system.test")
    @TabCompletion(format = "$subCommand $players dummy")
    public void test(CommandSender sender, String[] args) {
        sender.sendMessage("Hello, world!");
    }

    @Subcommand(rootCommand = "test", subCommand = "arg1", permission = "system.test.arg1", position = 1)
    public void testArg1(CommandSender sender, String[] args) {
        sender.sendMessage("Hello, " + sender.getName());
    }

}
```

## Getting started
___
You have to set ModernBukkit as a dependency in maven or gradle.

### Example for Maven
___
Repository:
```xml
<repository>
    <id>neo-repo</id>
    <url>https://repo.neo8.de/</url>
</repository>
```

Dependency:
```xml
<dependency>
    <groupId>de.neo</groupId>
    <artifactId>ModernBukkit</artifactId>
    <version>VERSION</version>
    <scope>provided</scope>
</dependency>
```

### Example for Gradle (groovy)
___
Repository:
```groovy
maven {
    name 'neo-repo'
    url 'https://repo.neo8.de/'
}
```

Dependency:
```groovy
compileOnly 'de.neo:ModernBukkit:VERSION'
```

### Example for Gradle (kotlin)
___
Repository:
```kotlin
maven("https://repo.neo8.de/")
```

Dependency:
```kotlin
compileOnly("de.neo:ModernBukkit:VERSION")
```