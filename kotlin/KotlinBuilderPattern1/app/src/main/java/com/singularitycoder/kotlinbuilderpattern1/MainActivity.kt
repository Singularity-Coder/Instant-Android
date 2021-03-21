package com.singularitycoder.kotlinbuilderpattern1

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val person1 = Person1.Builder()
                .name("Singularity Coder")
                .email("codehithesh@gmail.com")
                .mobile("9999999999")
                .dateOfBirth("12/07/0000")
                .build()

        val person2 = Person2.Builder()
                .name("Hithesh")
                .email("codehithesh@gmail.com")
                .mobile("9999999999")
                .dateOfBirth("12/07/0000")
                .build()

        findViewById<TextView>(R.id.tv_details).text = """
            My name is ${person1.name}. 
            I was born on ${person1.dateOfBirth}.
            You can mail me at ${person1.email} or call me on ${person1.mobile}.
            
            My clone's name is ${person2.name}.
            He was also born on ${person2.dateOfBirth}.
            You can mail him at ${person2.email} or call me on ${person2.mobile}.
            """.trimIndent()
    }
}

/**
 * Refer - https://www.baeldung.com/kotlin-builder-pattern
 * Whats wrong with getter setter methods and data classes? The Object consists of mutable fields. To create an immutable object once we finish building it use Builder Pattern.
 * 1. Immutable fields: Read only fields. We dont want outer objects to access them directly.
 * 2. Private constructor: Only inner classes can access them. In this case the nested builder class.
 * 3. Nested Builder class: This allows us to build the object. This class mimics its parent with mutable fields, setter methods that return the Builder and a build method. In short its a classic POJO with private fields and setter like methods to set the fields. Difference is that those methods return the Builder itself.
 * 4. build() method: This returns the parent class with the values populated by the builder's setter like methods.
 *
 * WHEN TO USE THIS PATTERN:
 * Building of objects that may contain a lot of parameters
 * When we want to make the object immutable once we're done constructing it.
 * The Builder Pattern solves a very common problem in the object-oriented programming of how to flexibly create an immutable object without writing many constructors. This is addressed in Kotlin through default & named params to some extent.
 */
class Person1 private constructor(
        val name: String?,
        val email: String?,
        val mobile: String?,
        val dateOfBirth: String?,
) {

    data class Builder(
            private var name: String? = null,
            private var email: String? = null,
            private var mobile: String? = null,
            private var dateOfBirth: String? = null,
    ) {
        fun name(name: String) = apply { this.name = name }
        fun email(email: String) = apply { this.email = email }
        fun mobile(mobile: String) = apply { this.mobile = mobile }
        fun dateOfBirth(dateOfBirth: String) = apply { this.dateOfBirth = dateOfBirth }
        fun build() = Person1(name = name, email = email, mobile = mobile, dateOfBirth = dateOfBirth)
    }
}

class Person2 private constructor(builder: Person2.Builder) {

    val name: String?
    val email: String?
    val mobile: String?
    val dateOfBirth: String?

    init {
        this.name = builder.name
        this.email = builder.email
        this.mobile = builder.mobile
        this.dateOfBirth = builder.dateOfBirth
    }

    class Builder {
        var name: String? = null
            private set
        var email: String? = null
            private set
        var mobile: String? = null
            private set
        var dateOfBirth: String? = null
            private set

        fun name(name: String) = apply { this.name = name }
        fun email(email: String) = apply { this.email = email }
        fun mobile(mobile: String) = apply { this.mobile = mobile }
        fun dateOfBirth(dateOfBirth: String) = apply { this.dateOfBirth = dateOfBirth }
        fun build() = Person2(this)
    }
}