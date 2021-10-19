# ModernBukkit
###### An annotation based plugin framework for bukkit.
___
## Table of contents
- [Why should I use ModernBukkit](#why-should-i-use-modernbukkit)
- [Code comparison](#code-comparison)
  - [Commands](#commands)
- [Getting started with the basics](#getting-started)
  - [Maven](#example-for-maven)
  - [Gradle (groovy)](#example-for-gradle-groovy)
  - [Gradle (kotlin)](#example-for-gradle-kotlin)
  - [Using ModernBukkit](#using-modernbukkit)
  - [Commands](#commands-with-modernbukkit)
    - [Registering commands](#registering-commands)
    - [Registering subcommands](#registering-subcommands)
    - [Using @TabCompletion](#using-tabcompletion)
    - [Custom TabCompleter](#custom-tabcompleter)


## Why should I use ModernBukkit?
ModernBukkit helps you to achieve your goals with less code
and makes your code cleaner and easier to understand.
Furthermore it is actively developed and supported.


## Code comparison
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
You have to set ModernBukkit as a dependency in maven or gradle.

### Example for Maven
Repository:
```xml
<repository>
    <id>keinsurvival-repo</id>
    <url>https://repo.nononitas.eu/ui/native/keinsurvival-public</url>
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
Repository:
```groovy
maven {
    name 'keinsurvival-repo'
    url 'https://repo.nononitas.eu/artifactory/keinsurvival-public'
}
```

Dependency:
```groovy
compileOnly 'de.neo:ModernBukkit:VERSION'
```

### Example for Gradle (kotlin)
Repository:
```kotlin
maven("https://repo.nononitas.eu/artifactory/keinsurvival-public")
```

Dependency:
```kotlin
compileOnly("de.neo:ModernBukkit:VERSION")
```

### Using ModernBukkit

To use the benefits of ModernBukkit you have to extend
``ModernJavaPlugin`` instead of ``JavaPlugin``. You
also have to override ``onStart()`` instead of ``onEnable()``.
When you override ``onEnable()`` your plugin may not work.

```java
public class TestMain extends ModernJavaPlugin {
    @Override
    public void onStart() {
        // Your code goes here...
    }
}
```

### Commands with ModernBukkit

### Registering commands

If you want to register commands using ModernBukkit you have to create a command class.
To do so you only need to create a new class. In this class you can create a method
with one of the following signatures.

```java
public class TestCommand {
    public void sig1(CommandSender sender, String[] args) {} // short signature

    public void sig2(CommandSender sender, Command cmd, String label, String[] args) {} // long signature
}
```

Now you need to mark the method as a command. You can do this by simply use the
``@Command`` annotation. The ``@Command`` annotation needs a few parameters so that
it can set the command up.

``@Command(String command, String permission, String[] aliases)``
- command - the name of the command.
- permission - the permission required to execute the command.
- aliases - all aliases that this command should have.

The method is executed when the command was executed.

_The following code registers the command ``/test`` and you need the permission
``system.test`` to use the command._

```java
public class TestCommand {
    @Command(command = "test", permission = "system.test")
    public void test(CommandSender sender, String[] args) {
        sender.sendMessage("Hello, world!");
    }
}
```

### Registering subcommands
You can also register subcommands using ModernBukkit. You need the same setup as
for a normal command. You just need to use the ``@Subcommand`` annotation instead of
the ``@Command`` annotations. The ``@Subcommand`` annotation has the following
parameters.

``@Subcommand(String rootCommand, String subCommand, String permission, int position)``
- rootCommand - the name of the parentcommand.
- subCommand - the name of the subcommand.
- permission - the permission required to execute the command.
- position - the position of the subcommand. _Not implemented yet. Default: 1_

_The following code registers the subcommand ``/test arg1`` and you need the permission
``system.test.arg1`` to use the command._

```java
public class TestCommand {
    @Command(command = "test", permission = "system.test")
    public void test(CommandSender sender, String[] args) {
        sender.sendMessage("Hello, world!");
    }

    @Subcommand(rootCommand = "test", subCommand = "arg1", permission = "system.test.arg1", position = 1)
    public void testArg1(CommandSender sender, String[] args) {
        sender.sendMessage("Hello, " + sender.getName());
    }

}
```

### Using ```@TabCompletion```
The default tab completion is not always good enough. When you want to improve the
tab completion. You only need to put the ``@TabCompletion`` annotation on the command
method. The ``@TabCompletion`` annotation takes the format as the only parameter.

The format works as follows. You provide one string. The string has a value for each
argument index separated by spaces. The following values are possible:

| Placeholder | Description                                     |
|-------------|-------------------------------------------------|
| $players    | Adds a list of all players to the tab complete. |

All placeholders that are invalid are visible as plain text.

```java
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

### Custom TabCompleter
If the tab completer provided by ModernBukkit is not good enough you can register
your own tab completer using the ``@TabCompleter`` annotation.

``@TabCompleter(command)``
- command - the name of the command this tab completer is for.

```java
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

    @TabCompleter(command = "test")
    public List<String> completeTest(CommandSender sender, String[] args) {
        ArrayList<String> completions = new ArrayList<>();
        if(args.length == 1) {
        completions.add("arg1");
        }
        return completions;
    }

}
```

That are the basics for ModernBukkit. I hope you have fun.

## Support
You can get support on the following platforms.

[Discord (german)](https://discord.gg/mbcAc3K9DJ)

[SpigotMC](https://www.spigotmc.org/conversations/add?to=Keksnet)