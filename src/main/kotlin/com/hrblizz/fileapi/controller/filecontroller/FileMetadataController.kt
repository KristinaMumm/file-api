package com.hrblizz.fileapi.controller.filecontroller

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hrblizz.fileapi.data.entities.Entity
import com.hrblizz.fileapi.data.repository.EntityRepository
import com.hrblizz.fileapi.library.log.ExceptionLogItem
import com.hrblizz.fileapi.library.log.Logger
import com.hrblizz.fileapi.rest.ResponseEntity
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
class FileMetadataController(
    private val fileRepository: EntityRepository,
    private val logger: Logger
) {

    fun getFileMetadata(token: String): Entity? {
        var fileEntity : Entity? = null

        try {
            fileEntity = fileRepository.findByToken(token)
        } catch (e: EmptyResultDataAccessException) {
            logger.warning(ExceptionLogItem("Token $token not found", e))
        }

        return fileEntity
    }

    @RequestMapping("/files/metas", method = [RequestMethod.POST])
    fun getMetadata(@RequestBody body: String): ResponseEntity<Map<String, Any>>? {
        val gson = Gson()
        val jsonObject = gson.fromJson(body, JsonObject::class.java)
        val tokensArray = jsonObject.getAsJsonArray("tokens")
        val tokensList = tokensArray.map { it.asString }

        val entityMap = mutableMapOf<String, Entity>()
        for (token in tokensList) {
            val entity = getFileMetadata(token)
            if (entity != null) {
                entityMap[token] = entity
            }
        }

        return ResponseEntity(mapOf("files" to entityMap), HttpStatus.OK.value())
    }
}
