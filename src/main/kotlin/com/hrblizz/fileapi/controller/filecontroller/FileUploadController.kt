package com.hrblizz.fileapi.controller.filecontroller

import com.hrblizz.fileapi.data.entities.Entity
import com.hrblizz.fileapi.data.repository.EntityRepository
import com.hrblizz.fileapi.library.log.ExceptionLogItem
import com.hrblizz.fileapi.library.log.LogItem
import com.hrblizz.fileapi.library.log.Logger
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
    private val fileRepository: EntityRepository,
    private val logger: Logger
) {
    @RequestMapping("/files", method = [RequestMethod.POST])
    fun postFile(
        @RequestParam("content") content: MultipartFile,
        @RequestParam("name") fileName: String,
        @RequestParam("contentType") contentType: String,
        @RequestParam("meta") meta: String,
        @RequestParam("source") source: String,
        @RequestParam("expireTime") expireTime: String
    ): ResponseEntity<Map<String, Any>> {

        if (content.isEmpty) {
            return ResponseEntity(
                null,
                listOf(ErrorMessage("Content not provided")),
                HttpStatus.BAD_REQUEST.value()
            )
        }

        val fileToken = randomUUID().toString()

        try {
            File(FileControllerConstants.FILES_DIRECTORY).mkdirs()
            val originalFilename = content.originalFilename ?: fileToken
            val filePath = Paths.get(FileControllerConstants.FILES_DIRECTORY + originalFilename)
            content.transferTo(filePath)

            val formatter = SimpleDateFormat(FileControllerConstants.DATE_FORMAT)

            fileRepository.save(
                Entity().also {
                    it.token = fileToken
                    it.fileName = originalFilename
                    it.size = Files.size(filePath)
                    it.contentType = contentType
                    it.meta = meta
                    it.source = source
                    it.expireTime = formatter.parse(expireTime)
                    it.createTime = Date()
                }
            )

            logger.info(LogItem("Uploaded file with token $fileToken"))

            return ResponseEntity(
                mapOf("token" to fileToken),
                HttpStatus.CREATED.value()
            )
        } catch (e: IOException) {
            val errorMessage = "Failed to upload file: $fileName"
            logger.error(ExceptionLogItem(errorMessage, e))
            return ResponseEntity(
                null,
                listOf(ErrorMessage(errorMessage)),
                HttpStatus.SERVICE_UNAVAILABLE.value()
            )
        }
    }
}
