package com.example.angelinilorenzo.htmlbuilder

enum class Wrap : StringEnum<Wrap> {
    soft, hard, off;

    override val value: String get() = name
}
