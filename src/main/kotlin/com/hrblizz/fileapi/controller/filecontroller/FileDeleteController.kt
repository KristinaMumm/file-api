package com.hrblizz.fileapi.controller.filecontroller

import com.hrblizz.fileapi.data.entities.Entity
import com.hrblizz.fileapi.data.repository.EntityRepository
import com.hrblizz.fileapi.rest.ErrorMessage
import com.hrblizz.fileapi.rest.ResponseEntity
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.io.File

@RestController
class FileDeleteController(
    private val fileRepository: EntityRepository
) {
    @RequestMapping("/file/{fileToken}", method = [RequestMethod.DELETE])
    fun deleteFile(@PathVariable fileToken: String): ResponseEntity<Map<String, Any>>? {
        val fileEntity : Entity

        try {
            fileEntity = fileRepository.findByToken(fileToken)
        } catch (e: EmptyResultDataAccessException) {
            val errorMessage = listOf(ErrorMessage("Token not found"))
            return ResponseEntity(null, errorMessage, HttpStatus.NOT_FOUND.value())
        }

        val file = File(FileControllerConstants.FILES_DIRECTORY, fileEntity.fileName)

        if (file.exists()) {
            file.delete()
        }

        fileRepository.delete(fileEntity)

        return ResponseEntity(HttpStatus.OK.value())
    }
}
