package com.johannsn.cyclemapbackend.controller

import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletRequest

@Controller
class BasicErrorController : ErrorController {

    @RequestMapping("/error")
    @ResponseBody
    fun error(request: HttpServletRequest?): String {
        return "Not available"
    }
}