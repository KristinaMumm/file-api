package com.hrblizz.fileapi.controller.filecontroller

import com.hrblizz.fileapi.data.repository.EntityRepository
import com.hrblizz.fileapi.rest.ErrorMessage
import com.hrblizz.fileapi.rest.ResponseEntity
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

    private val uploadDir = "C:\\Users\\Kristina\\Documents\\mercans_test\\test-files\\uploads\\"

    @RequestMapping("/file/{fileToken}", method = [RequestMethod.DELETE])
    fun deleteFile(@PathVariable fileToken: String): ResponseEntity<Map<String, Any>>? {
        val fileEntity = fileRepository.findByToken(fileToken)
        val file = File(uploadDir, fileEntity.fileName)

        return if (file.exists()) {
            file.delete()
            ResponseEntity(HttpStatus.OK.value())
        } else {
            val errorMessage = listOf(ErrorMessage("File not found"))
            ResponseEntity(null, errorMessage, HttpStatus.NOT_FOUND.value())
        }

    }
}