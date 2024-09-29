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
import java.nio.file.Files
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID.randomUUID

@RestController
class FileUploadController(
    private val fileRepository: EntityRepository
) {
    @RequestMapping("/files", method = [RequestMethod.POST])
    fun postFile(
        @RequestParam("content") content: MultipartFile,
        @RequestParam("name") fileName: String,
        @RequestParam("contentType") contentType: String,
        @RequestParam("meta") metadata: String,
        @RequestParam("source") source: String,
        @RequestParam("expireTime") expireTime: String
    ): ResponseEntity<Map<String, Any>> {

        if (content.isEmpty) {
            return ResponseEntity(
                null,
                listOf(ErrorMessage("File not provided")),
                HttpStatus.OK.value()
            )
        }

        val fileToken = randomUUID().toString()

        try {
            File(FileControllerConstants.FILES_DIRECTORY).mkdirs()
            val originalFilename = content.originalFilename ?: "file"
            val fileToSave = File(FileControllerConstants.FILES_DIRECTORY + originalFilename)
            content.transferTo(fileToSave)

            val filePath = Paths.get(FileControllerConstants.FILES_DIRECTORY + originalFilename)
            val formatter = SimpleDateFormat(FileControllerConstants.DATE_FORMAT)

            fileRepository.save(
                Entity().also {
                    it.token = fileToken
                    it.fileName = originalFilename
                    it.size = Files.size(filePath)
                    it.contentType = contentType
                    it.metadata = metadata
                    it.source = source
                    it.expireTime = formatter.parse(expireTime)
                    it.createTime = Date()
                }
            )

            return ResponseEntity(
                mapOf("token" to fileToken),
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
