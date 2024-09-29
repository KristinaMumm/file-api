package com.hrblizz.fileapi.controller.filecontroller

import com.hrblizz.fileapi.data.entities.Entity
import com.hrblizz.fileapi.data.repository.EntityRepository
import com.hrblizz.fileapi.rest.ErrorMessage
import com.hrblizz.fileapi.rest.ResponseEntity
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.util.UUID.randomUUID

@RestController
class FileUploadController(
    private val fileRepository: EntityRepository
) {
    @RequestMapping("/files", method = [RequestMethod.POST])
    fun postFile(@RequestParam("pdfFile") file: MultipartFile): ResponseEntity<Map<String, Any>> {

        if (file.isEmpty) {
            return ResponseEntity(
                null,
                listOf(ErrorMessage("File not provided")),
                HttpStatus.OK.value()
            )
        }

        val fileToken = randomUUID().toString()

        try {

            File(FileControllerConstants.FILES_DIRECTORY).mkdirs()
            val originalFilename = file.originalFilename ?: "file"
            val fileToSave = File(FileControllerConstants.FILES_DIRECTORY + originalFilename)
            file.transferTo(fileToSave)

            fileRepository.save(
                Entity().also {
                    it.fileName = file.originalFilename!!
                    it.token = fileToken
                }
            )

            return ResponseEntity(
                mapOf(
                    "token" to fileToken,
                ),
                HttpStatus.CREATED.value()
            )
        } catch (e: IOException) {
            e.printStackTrace()
            return ResponseEntity(
                null,
                listOf(ErrorMessage("Failed to upload file: ${e.message}")),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
            )
        }
    }
}
