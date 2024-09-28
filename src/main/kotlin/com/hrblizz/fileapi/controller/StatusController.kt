package com.hrblizz.fileapi.controller

import com.hrblizz.fileapi.data.repository.EntityRepository
import com.hrblizz.fileapi.rest.ResponseEntity
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
class StatusController(
    private val entityRepository: EntityRepository
) {
    @RequestMapping("/status", method = [RequestMethod.GET])
    fun getStatus(): ResponseEntity<Map<String, Any>> {
        return ResponseEntity(
            mapOf(
                "ok" to true,
            ),
            HttpStatus.OK.value()
        )
    }
}
