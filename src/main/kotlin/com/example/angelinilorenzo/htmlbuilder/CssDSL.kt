package com.example.angelinilorenzo.htmlbuilder

import java.util.*

interface Selector {
    fun toExternalForm(): String
}

interface SelectorTrait {
    fun toExternalForm(): String
}

object EmptyTrait : SelectorTrait {
    override fun toExternalForm(): String {
        return ""
    }
}

interface StyleClass : SelectorTrait, Selector {
    val name: String

    override fun toExternalForm(): String {
        return ".${name}"
    }
}

class SimpleClassStyle(override val name : String) : StyleClass {
}

class CompositeStyleClass(val a: StyleClass, val b: StyleClass) : StyleClass {
    override val name: String get() = "${a.name} ${b.name}"

    override fun toExternalForm(): String {
        return "${a.toExternalForm()} ${b.toExternalForm()}"
    }
}

operator fun StyleClass?.plus(another: StyleClass?): StyleClass? = when {
    this == null && another == null -> null
    this == null -> another
    another == null -> this
    else -> CompositeStyleClass(this, another)
}

enum class PseudoClass : StyleClass {
    root, firstChild, lastChild, firstOfType, lastOfType, onlyChild, onlyOfType,
    empty, link, visited, active, focus, hover, target, enabled, disabled, checked;

    override fun toExternalForm(): String {
        return ":${name}"
    }
}

/**
 * Represents a single stylesheet element.
 */
open class CssElement() {
    val children = arrayListOf<StyledElement>()
    val attributes = HashMap<String, Any>()

    inner class IdSelector(val name: String) : SelectorTrait, Selector {
        operator fun invoke(body: StyledElement.() -> Unit) {
            any.invoke(this, body = body)
        }

        override fun toExternalForm(): String {
            return "#$name"
        }
    }

    inner open class TagSelector(val name: String) : Selector {
        fun id(name: String): Selector = invoke(IdSelector(name))
        fun id(name: String, body: StyledElement.() -> Unit) = id(IdSelector(name), body)
        fun id(id: IdSelector, body: StyledElement.() -> Unit) = invoke(id, body = body)

        fun c(vararg klass: StyleClass, body: StyledElement.() -> Unit) = invoke(*klass, body = body)
        fun c(vararg klass: StyleClass): Selector = invoke(*klass)

        operator fun invoke(vararg traits: SelectorTrait, body: StyledElement.() -> Unit) {
            s(SimpleSelector(this, traits).toExternalForm(), body)
        }

        operator fun invoke(classes: String, body: StyledElement.() -> Unit) {
            s(SimpleSelector(this, arrayOf(SimpleClassStyle(classes))).toExternalForm(), body)
        }

        operator fun invoke(vararg t: SelectorTrait): Selector {
            return SimpleSelector(this, t)
        }

        fun isAny(): Boolean = "*" == name

        override fun toExternalForm(): String {
            return name
        }
    }

    val any: TagSelector get() = TagSelector("*")
    val a: TagSelector get() = TagSelector("a")
    val b: TagSelector get() = TagSelector("b")
    val body: TagSelector get() = TagSelector("body")
    val button: TagSelector get() = TagSelector("button")
    val canvas: TagSelector get() = TagSelector("canvas")
    val div: TagSelector get() = TagSelector("div")
    val em: TagSelector get() = TagSelector("em")
    val fieldset: TagSelector get() = TagSelector("fieldset")
    val form: TagSelector get() = TagSelector("form")
    val h1: TagSelector get() = TagSelector("h1")
    val h2: TagSelector get() = TagSelector("h2")
    val h3: TagSelector get() = TagSelector("h3")
    val h4: TagSelector get() = TagSelector("h4")
    val h5: TagSelector get() = TagSelector("h5")
    val hr: TagSelector get() = TagSelector("hr")
    val i: TagSelector get() = TagSelector("i")
    val img: TagSelector get() = TagSelector("img")
    val input: TagSelector get() = TagSelector("input")
    val legend: TagSelector get() = TagSelector("legend")
    val label: TagSelector get() = TagSelector("label")
    val ol: TagSelector get() = TagSelector("ol")
    val p: TagSelector get() = TagSelector("p")
    val select: TagSelector get() = TagSelector("select")
    val span: TagSelector get() = TagSelector("span")
    val small: TagSelector get() = TagSelector("small")
    val strong: TagSelector get() = TagSelector("strong")
    val blockquote: TagSelector get() = TagSelector("blockquote")
    val table: TagSelector get() = TagSelector("table")
    val textarea: TagSelector get() = TagSelector("textarea")
    val ul: TagSelector get() = TagSelector("ul")
    val li: TagSelector get() = TagSelector("li")
    val option: TagSelector get() = TagSelector("option")
    val optgroup: TagSelector get() = TagSelector("optgroup")
    val tr: TagSelector get() = TagSelector("tr")
    val tbody: TagSelector get() = TagSelector("tbody")
    val td: TagSelector get() = TagSelector("td")
    val th: TagSelector get() = TagSelector("th")
    val dl: TagSelector get() = TagSelector("dl")
    val dt: TagSelector get() = TagSelector("dt")
    val dd: TagSelector get() = TagSelector("dd")


    fun id(name: String, body: StyledElement.() -> Unit) {
        any.id(name, body)
    }

    fun id(name: String): IdSelector = IdSelector(name)

    fun c(klass: StyleClass, body: StyledElement.() -> Unit) {
        any.invoke(klass, body = body)
    }

    fun att(name: String): Attribute = Attribute(name, HasAttribute(name))

    class Attribute internal constructor(val name: String, val filter: AttFilter) : SelectorTrait {
        infix fun startsWith(value: String): Attribute {
            return Attribute(name, StartsWith(value))
        }

        infix fun equalTo(value: String): Attribute {
            return Attribute(name, Equals(value))
        }

        infix fun endsWith(value: String): Attribute {
            return Attribute(name, EndsWith(value))
        }

        infix fun contains(value: String): Attribute {
            return Attribute(name, Contains(value, AttributeValueTokenizer.Substring))
        }

        infix fun containsInHypen(value: String): Attribute {
            return Attribute(name, Contains(value, AttributeValueTokenizer.Hypen))
        }

        infix fun containsInSpaces(value: String): Attribute {
            return Attribute(name, Contains(value, AttributeValueTokenizer.Spaces))
        }

        override fun toExternalForm(): String {
            return "[$name${filter.toExternalForm()}]"
        }
    }

    class SimpleSelector(val tag: TagSelector, val traits: Array<out SelectorTrait>) : Selector {
        override fun toExternalForm(): String = buildString {
            val isAny = tag.isAny()
            if (!isAny) {
                append(tag.name)
            }

            for (t in traits) {
                append(t.toExternalForm())
            }

            if (length == 0 && isAny) {
                return "*"
            }
        }
    }

    fun forAny(vararg selectors: Selector, body: StyledElement.() -> Unit) {
        s(UnionSelector(selectors).toExternalForm(), body)
    }

    fun forAny(vararg selectors: Selector): UnionSelector {
        return UnionSelector(selectors)
    }

    class UnionSelector(val selectors: Array<out Selector>) : Selector {
        override fun toExternalForm(): String {
            return "(${selectors.map ({ it.toExternalForm() }).joinToString(",")})"
        }
    }

    /**
     * Creates a new child element with the given selector and block.
     */
    fun s(selector: String, init: StyledElement.() -> Unit) {
        val element = StyledElement(selector)
        element.init()
        children.add(element)
    }
}


class StyledElement(val selector: String) : CssElement() {
    /**
     * Writes the element to the builder with the given indenation.
     */
    fun build(builder: StringBuilder, baseSelector: String) {
        val thisSelector = if (baseSelector.length > 0) if (selector.startsWith(':')) "$baseSelector$selector" else "$baseSelector $selector" else selector
        builder.append("$thisSelector {\n")
        for (a in attributes.keys) {
            val attr = attributes[a]!!
            builder.append("    $a: ${attr.toString()};\n")
        }
        builder.append("}\n")
        for (child in children) {
            child.build(builder, thisSelector)
        }
    }

    /** Strongly-typed method for pulling attributes out of the hash. */
    @Suppress("UNCHECKED_CAST")
    fun <T:Any?> getAttribute(name: String): T {
        if (attributes.containsKey(name))
            return attributes[name] as T
        else
            throw Exception("Element has no attribute $name")
    }

    /** Strongly-typed method for pulling attributes out of the hash, with a default return value. */
    @Suppress("UNCHECKED_CAST")
    fun <T> getAttribute(name: String, default: T): T {
        if (attributes.containsKey(name))
            return attributes[name] as T
        else
            return default
    }

    /** Shorthand for making a color inside a stylesheet. */
    fun c(colorString: String): Color = color(colorString)

    var backgroundAttachment: BackgroundAttachment?
        get() = getAttribute("background-attachment")
        set(value) {
            attributes["background-attachment"] = value.toString()
        }

    var backgroundColor: Color?
        get() = getAttribute("background-color")
        set(value) {
            attributes["background-color"] = value.toString()
        }

    var backgroundImage: String?
        get() = getAttribute("background-image")
        set(value) {
            attributes["background-image"] = value.toString()
        }

    var backgroundPosition: String?
        get() = getAttribute("background-position")
        set(value) {
            attributes["background-position"] = value.toString()
        }

    var backgroundRepeat: BackgroundRepeat?
        get() = getAttribute("background-repeat")
        set(value) {
            attributes["background-repeat"] = value.toString()
        }

    var border: String = ""
        set(value) {
            val tokens = value.split(' ')
            for (token in tokens) {
                if (isLinearDimension(token))
                    borderWidth = LinearDimension.fromString(token)
                else if (isColor(token))
                    borderColor = color(token)
                else if (isBorderStyle(token))
                    borderStyle = makeBorderStyle(token)
                else
                    throw Exception("Invalid border property: $token")
            }
        }

    var borderColor: Color?
        get() = getAttribute("border-color")
        set(value) {
            attributes["border-color"] = value.toString()
        }

    var borderRadius: LinearDimension
        get() = getAttribute("border-radius", 0.px)
        set(value) {
            attributes["border-radius"] = value.toString()
        }

    var borderBottomLeftRadius: LinearDimension
        get() = getAttribute("border-bottom-left-radius", 0.px)
        set(value) {
            attributes["border-bottom-left-radius"] = value.toString()
        }

    var borderBottomRightRadius: LinearDimension
        get() = getAttribute("border-bottom-right-radius", 0.px)
        set(value) {
            attributes["border-bottom-right-radius"] = value.toString()
        }

    var borderTopLeftRadius: LinearDimension
        get() = getAttribute("border-top-left-radius", 0.px)
        set(value) {
            attributes["border-top-left-radius"] = value.toString()
        }

    var borderTopRightRadius: LinearDimension
        get() = getAttribute("border-top-right-radius", 0.px)
        set(value) {
            attributes["border-top-right-radius"] = value.toString()
        }

    var borderStyle: BorderStyle?
        get() = getAttribute("border-style")
        set(value) {
            attributes["border-style"] = value.toString()
        }

    var borderWidth: LinearDimension
        get() = getAttribute("border-width", 0.px)
        set(value) {
            attributes["border-width"] = value.toString()
        }

    var clear: Clear?
        get() = getAttribute("clear")
        set(value) {
            attributes["clear"] = value.toString()
        }

    var color: Color?
        get() = getAttribute("color")
        set(value) {
            attributes["color"] = value.toString()
        }

    var display: Display?
        get() = getAttribute("display")
        set(value) {
            attributes["display"] = value.toString()
        }

    var float: FloatType?
        get() = getAttribute("float")
        set(value) {
            attributes["float"] = value.toString()
        }

    var fontFamily: String?
        get() = getAttribute("font-family")
        set(value) {
            attributes["font-family"] = value.toString()
        }

    var fontSize: LinearDimension?
        get() = getAttribute("font-size")
        set(value) {
            attributes["font-size"] = value.toString()
        }

    var fontWeight: FontWeight
        get() = getAttribute("font-weight")
        set(value) {
            attributes["font-weight"] = value.toString()
        }

    var height: LinearDimension?
        get() = getAttribute("height")
        set(value) {
            attributes["height"] = value.toString()
        }

    var lineHeight: LinearDimension?
        get() = getAttribute("line-height")
        set(value) {
            attributes["line-height"] = value.toString()
        }

    var margin: BoxDimensions?
        get() = getAttribute("margin")
        set(value) {
            attributes["margin"] = value.toString()
        }

    var marginTop: LinearDimension?
        get() = getAttribute("margin-top")
        set(value) {
            attributes["margin-top"] = value.toString()
        }

    var marginBottom: LinearDimension?
        get() = getAttribute("margin-bottom")
        set(value) {
            attributes["margin-bottom"] = value.toString()
        }

    var marginLeft: LinearDimension?
        get() = getAttribute("margin-left")
        set(value) {
            attributes["margin-left"] = value.toString()
        }

    var marginRight: LinearDimension?
        get() = getAttribute("margin-right")
        set(value) {
            attributes["margin-right"] = value.toString()
        }

    var maxHeight: LinearDimension?
        get() = getAttribute("max-height")
        set(value) {
            attributes["max-height"] = value.toString()
        }

    var maxWidth: LinearDimension?
        get() = getAttribute("max-width")
        set(value) {
            attributes["max-width"] = value.toString()
        }

    var minHeight: LinearDimension?
        get() = getAttribute("min-height")
        set(value) {
            attributes["min-height"] = value.toString()
        }

    var minWidth: LinearDimension?
        get() = getAttribute("min-width")
        set(value) {
            attributes["min-width"] = value.toString()
        }

    var overflow: Overflow
        get() = getAttribute("overflow", Overflow.inherit)
        set(value) {
            attributes["overflow"] = value.toString()
        }

    var padding: BoxDimensions?
        get() = getAttribute("padding")
        set(value) {
            attributes["padding"] = value.toString()
        }

    var paddingTop: LinearDimension?
        get() = getAttribute("padding-top")
        set(value) {
            attributes["padding-top"] = value.toString()
        }

    var paddingBottom: LinearDimension?
        get() = getAttribute("padding-bottom")
        set(value) {
            attributes["padding-bottom"] = value.toString()
        }

    var paddingLeft: LinearDimension?
        get() = getAttribute("padding-left")
        set(value) {
            attributes["padding-left"] = value.toString()
        }

    var paddingRight: LinearDimension?
        get() = getAttribute("padding-right")
        set(value) {
            attributes["padding-right"] = value.toString()
        }

    var textAlign: TextAlign
        get() = getAttribute("text-align", TextAlign.inherit)
        set(value) {
            attributes["text-align"] = value.toString()
        }

    var verticalAlign: VerticalAlign
        get() = getAttribute("vertical-align", VerticalAlign.inherit)
        set(value) {
            attributes["vertical-align"] = value.toString()
        }

    var width: LinearDimension?
        get() = getAttribute("width")
        set(value) {
            attributes["width"] = value.toString()
        }
}
