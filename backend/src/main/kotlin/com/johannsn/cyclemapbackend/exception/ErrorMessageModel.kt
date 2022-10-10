package com.johannsn.cyclemapbackend.exception

import org.springframework.http.HttpStatus
import java.time.LocalDateTime

class ErrorMessageModel {
    var status : HttpStatus
    var message : String? = null
    var timeStamp: LocalDateTime? = null
    var debugMessage: String? = null

    init {
        timeStamp = LocalDateTime.now()
    }
    constructor(status: HttpStatus, message: String?){
        this.status = status
        this.message = message
    }

    constructor(status: HttpStatus, e: Throwable){
        this.status = status
        this.message = "Unknown error"
        this.debugMessage = e.localizedMessage
    }

    constructor(status: HttpStatus, message: String?, e: Throwable){
        this.status = status
        this.message = message
        this.debugMessage = e.localizedMessage
    }
}

