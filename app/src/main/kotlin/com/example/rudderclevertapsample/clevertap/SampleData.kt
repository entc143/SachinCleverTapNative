package com.example.rudderclevertapsample.clevertap

import java.util.Date

object SampleData {

    fun sampleProfile(): HashMap<String, Any> = hashMapOf(
        "Identity" to "sachin_001",
        "Name" to "Sachin G",
        "Email" to "sachin.test@example.com",
        "Phone" to "+919999999999",
        "Gender" to "M",
        "DOB" to Date(),
        "MSG-email" to true,
        "MSG-push" to true,
        "MSG-sms" to false,
        "MSG-whatsapp" to true,
        "Plan" to "Premium",
        "Favourite Team" to "Mumbai Indians"
    )

    const val EVENT_ADD_TO_SACHIN = "College_add_to_sachin"

    fun sampleEventProperties(): HashMap<String, Any> = hashMapOf(
        "Product Name" to "CleverTap Hoodie",
        "Product ID" to "SKU-12345",
        "Price" to 1499.0,
        "Quantity" to 1,
        "Currency" to "INR",
        "Added On" to Date(),
        "courses_in_cart" to arrayListOf(
            hashMapOf(
                "college_id" to 10,
                "College_name" to "Greenfield Institute of Technology",
                "courses" to arrayListOf(
                    hashMapOf("id" to 101, "name" to "B.Tech in Data Science")
                )
            ),
            hashMapOf(
                "college_id" to 12,
                "College_name" to "Sunrise University, Jaipur",
                "courses" to arrayListOf(
                    hashMapOf("id" to 102, "name" to "B.Tech in Computer Science and Engineering"),
                    hashMapOf("id" to 103, "name" to "B.E. in IT")
                )
            )
        )
    )

    const val EVENT_NATIVE_DISPLAY_SCREEN_VIEWED = "Native Display Viewed"
    const val DEFAULT_NATIVE_DISPLAY_IMAGE_URL =
        "https://drive.google.com/uc?export=view&id=1mJ-ECKrfJXZCBWilb9UvX1dKOWFi9dIz"
}
