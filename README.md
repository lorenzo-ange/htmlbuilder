# Kotlin HTMLBuilder
HTMLBuilder is an open source library extracted from the project [Kara Web Framework](http://karaframework.com/)

It provides a DSL for generating HTML using the Kotlin language.

With HTMLBuilder you have:
- All the power of Kotlin to generate HTML (idioms, abstractions, functional goodies, if clauses, loops...)
- Simple Kotlin functions as reusable HTML templates
- HTML elements always placed correctly (ex: 'li' tag outside of 'ul' tag will result in a compiler error)
- Tags automatically closed and indented
- Extreme flexibility: do you need a tag/attribute not expected by HTMLBuilder? No problem!

## Basic usage
```kotlin
import com.example.angelinilorenzo.htmlbuilder.*

fun main(args: Array<String>) {
    val html = html {
        head { +"Hello World title" }
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
  <head>Hello World title</head>
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
        head { +"Sample title" }
        body {
            h1 { +"Writing typesafe HTML with Kotlin" }
            p { +"Lorem ipsum dolor sit amet, consectetur adipiscing." }
            a {
                href = DirectLink("http://www.example.com")
                +"Elit"
            }
            p {
                val something = 44
                +"Ut enim ad minim $something, quis nostrud exercitation ullamco "
                (0..3).forEach { +"laboris, " }
                strong { +"commodo" }
                +" consequat."
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
  <head>Sample title</head>
  <body>
    <h1>Writing typesafe HTML with Kotlin</h1>
    <p>Lorem ipsum dolor sit amet, consectetur adipiscing </p>
    <a href="http://www.example.com">elit</a>
    <p>Ut enim ad minim 44, quis nostrud exercitation ullamco laboris laboris laboris laboris <strong>commodo </strong>consequat.</p>
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
No more Cut & Paste!

Use all the tools and abstractions Kotlins gives you to create your reusable components.
```kotlin
import com.example.angelinilorenzo.htmlbuilder.*

fun main(args: Array<String>) {
    fun HtmlBodyTag.student(name: String, hobbies: List<String>) {
        p {
            +"Hello my name is $name! and these are my hobbies:"
            img { src = DirectLink("http://www.example.com/students/photos/$name.png") }
            ul {
                hobbies.forEach { li { +it } }
            }
        }
    }

    val html = html {
        head { +"Students hobbies" }
        body {
            h1 { +"Students hobbies" }
            p {
                student("Alex McKay", listOf("Surf the internet", "Play PC games"))
                student("Marc Bale", listOf("Play guitar", "Read a book"))
                student("John Peterson", listOf("Have a walk", "Go to gym"))
                student("Gina Carter", listOf("Cooking", "Play football"))
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
  <head>Students hobbies</head>
  <body>
    <h1>Students hobbies</h1>
    <p>
      <p>
        Hello my name is Alex McKay! and these are my hobbies:
        <img src="http://www.example.com/students/photos/Alex McKay.png"/>
        <ul>
          <li>Surf the internet</li>
          <li>Play PC games</li>
        </ul>
      </p>
      <p>
        Hello my name is Marc Bale! and these are my hobbies:
        <img src="http://www.example.com/students/photos/Marc Bale.png"/>
        <ul>
          <li>Play guitar</li>
          <li>Read a book</li>
        </ul>
      </p>
      <p>
        Hello my name is John Peterson! and these are my hobbies:
        <img src="http://www.example.com/students/photos/John Peterson.png"/>
        <ul>
          <li>Have a walk</li>
          <li>Go to gym</li>
        </ul>
      </p>
      <p>
        Hello my name is Gina Carter! and these are my hobbies:
        <img src="http://www.example.com/students/photos/Gina Carter.png"/>
        <ul>
          <li>Cooking</li>
          <li>Play football</li>
        </ul>
      </p>
    </p>
  </body>
</html>
```

## Custom attributes
```kotlin
import com.example.angelinilorenzo.htmlbuilder.*

fun main(args: Array<String>) {
    val html = html {
        head {
            +"Custom attributes"
        }
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
  <head>Custom attributes</head>
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
        head {
            +"Custom tags"
        }
        body {
            tag("custom-tag"){
                p {
                    +"Hello world!"
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
  <head>Custom tags</head>
  <body>
    <custom-tag>
      <p>Hello world!</p>
    </custom-tag>
  </body>
</html>
```