# HTMLBuilder
HTMLBuilder is an Open Source library (Apache Licenses, version 2.0) extracted from the project [Kara Web Framework](http://karaframework.com/)

It provides a rich **DSL for generating typesafe HTML** using the Kotlin language.

HTMLBuilder is really **lightweight**! It doesn't have dependencies and the jar lib is ~ 230KB. 

With HTMLBuilder you get:
- All the power of Kotlin to generate HTML (idioms, abstractions, functional goodies, if/when clauses, loops...)
- Simple Kotlin functions as reusable HTML templates
- HTML elements always placed correctly (ex: 'li' tag outside of 'ul' tag will result in a compiler error)
- Tags automatically closed and indented
- Extreme flexibility: do you need a tag/attribute not expected by HTMLBuilder? No problem!

## Add to your project
Download the jar file from [Github Releases](https://github.com/lorenzo-ange/htmlbuilder/releases).

Adding the library to your project with Gradle is simple as:
```
dependencies {
    compile files("PATH_TO_LIB")
}
```

## Basic usage
```kotlin
import com.example.angelinilorenzo.htmlbuilder.*

fun main(args: Array<String>) {
    val html = html {
        head { title { +"Hello World title" } }
        body {
            p { +"Hello world!" }
        }
    }
    println(html)
}
```
The snippet of code above produces the following output:
```html
<!DOCTYPE html>
<html>
  <head>
    <title>Hello World title</title>
  </head>
  <body>
    <p>Hello world!</p>
  </body>
</html>
```

## Another example
```kotlin
import com.example.angelinilorenzo.htmlbuilder.*

fun main(args: Array<String>) {
    val html = html {
        head { title { +"Sample title" } }
        body {
            h1 { +"Writing typesafe HTML with Kotlin" }
            p { +"Lorem ipsum dolor sit amet, consectetur adipiscing."
                a {
                    href = DirectLink("http://www.example.com")
                    +"Elit"
                }
                +"Ut enim ad minim ${ 3 + 2 }, quis nostrud exercitation ullamco "
                (0..3).forEach { +"laboris, " }
                strong { +"commodo consequat" }
            }
            p {
                val listItems = listOf( "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                                        "Aliquam a est quis mauris eleifend cursus et et enim.",
                                        "Sed lobortis augue eget dignissim bibendum." )
                +"A list:"
                ul {
                    listItems.forEach { li { +it } }
                }
            }
        }
    }
    println(html)
}
```
The snippet of code above produces the following output:
```html
<!DOCTYPE html>
<html>
  <head>
    <title>Sample title</title>
  </head>
  <body>
    <h1>Writing typesafe HTML with Kotlin</h1>
    <p>Lorem ipsum dolor sit amet, consectetur adipiscing.<a href="http://www.example.com">Elit</a>Ut enim ad minim 5, quis nostrud exercitation ullamco laboris, laboris, laboris, laboris, <strong>commodo consequat</strong></p>
    <p>
      A list:
      <ul>
        <li>Lorem ipsum dolor sit amet, consectetur adipiscing elit.</li>
        <li>Aliquam a est quis mauris eleifend cursus et et enim.</li>
        <li>Sed lobortis augue eget dignissim bibendum.</li>
      </ul>
    </p>
  </body>
</html>
```

## Kotlin functions as HTML templates
```kotlin
import com.example.angelinilorenzo.htmlbuilder.*

fun defaultLayout(content: HtmlBodyTag.() -> Unit) =
    html {
        head {
            title { +"Sample title" }
        }
        body {
            div("container") {
                content()
            }
        }
    }

fun main(args: Array<String>) {
    val html = defaultLayout {
        h1 { +"My content inside a layout :)" }
        p { +"Lorem ipsum dolor sit amet, consectetur adipiscing." }
    }
    println(html)
}
```
The snippet of code above produces the following output:
```html
<!DOCTYPE html>
<html>
  <head>
    <title>Sample title</title>
  </head>
  <body>
    <div class="container">
      <h1>My content inside a layout :)</h1>
      <p>Lorem ipsum dolor sit amet, consectetur adipiscing.</p>
    </div>
  </body>
</html>
```

## Kotlin functions as HTML reusable components
No more Cut & Paste!

Use all the tools and abstractions Kotlins gives you to create your reusable components.
```kotlin
import com.example.angelinilorenzo.htmlbuilder.*

fun main(args: Array<String>) {
    fun HtmlBodyTag.student(name: String, hobbies: List<String>) {
        p {
            +"Hello my name is $name! and these are my hobbies:"
            ul {
                hobbies.forEach { li { +it } }
            }
        }
    }

    val html = html {
        head { title { +"Students hobbies" } }
        body {
            h1 { +"Students hobbies" }
            student("Alex McKay", listOf("Surf the internet", "Play PC games"))
            student("Marc Bale", listOf("Play guitar", "Read a book"))
            student("John Peterson", listOf("Have a walk", "Go to gym"))
        }
    }
    println(html)
}
```
The snippet of code above produces the following output:
```html
<!DOCTYPE html>
<html>
  <head>
    <title>Students hobbies</title>
  </head>
  <body>
    <h1>Students hobbies</h1>
    <p>
      Hello my name is Alex McKay! and these are my hobbies:
      <ul>
        <li>Surf the internet</li>
        <li>Play PC games</li>
      </ul>
    </p>
    <p>
      Hello my name is Marc Bale! and these are my hobbies:
      <ul>
        <li>Play guitar</li>
        <li>Read a book</li>
      </ul>
    </p>
    <p>
      Hello my name is John Peterson! and these are my hobbies:
      <ul>
        <li>Have a walk</li>
        <li>Go to gym</li>
      </ul>
    </p>
  </body>
</html>
```

## Custom attributes
```kotlin
import com.example.angelinilorenzo.htmlbuilder.*

fun main(args: Array<String>) {
    val html = html {
        head { title { +"Custom attributes" } }
        body {
            div {
                attribute("my-attribute", "my-value")
            }
        }
    }
    println(html)
}
```
The snippet of code above produces the following output:
```html
<!DOCTYPE html>
<html>
  <head>
    <title>Custom attributes</title>
  </head>
  <body>
    <div my-attribute="my-value"></div>
  </body>
</html>
```

## Custom tags
```kotlin
import com.example.angelinilorenzo.htmlbuilder.*

fun main(args: Array<String>) {
    val html = html {
        head { title { +"Custom tags" } }
        body {
            tag("custom-tag"){
                p { +"Hello world!" }
            }
        }
    }
    println(html)
}
```
The snippet of code above produces the following output:
```html
<!DOCTYPE html>
<html>
  <head>
    <title>Custom tags</title>
  </head>
  <body>
    <custom-tag>
      <p>Hello world!</p>
    </custom-tag>
  </body>
</html>
```