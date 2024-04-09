package com.example.notes

data class NotesData(
    val id: String,

    var title: String? = null,
    var content: String? = null
) {

    // No-argument constructor
    constructor() : this( "","", "")
}
