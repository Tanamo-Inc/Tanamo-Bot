package com.tanamo.mybot.model


import java.io.Serializable

class Messo : Serializable {
    var id: String = ""
    var message: String = ""


    constructor(id: String, message: String) {
        this.id = id
        this.message = message


    }

    constructor()

}

