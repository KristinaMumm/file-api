package com.hrblizz.fileapi.controller

import com.hrblizz.fileapi.data.entities.File
import com.hrblizz.fileapi.data.repository.FileRepository
import com.hrblizz.fileapi.rest.ResponseEntity
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
class UploadController(
    private val fileRepository: FileRepository
) {
    @RequestMapping("/files", method = [RequestMethod.POST])
    fun postFile(@RequestParam("pdfFile") file: MultipartFile): ResponseEntity<Map<String, Any>> {

        val fileToken = UUID.randomUUID().toString()

        fileRepository.save(
            File().also {
                it.content = file.bytes
                it.fileName = file.originalFilename
                it.token = fileToken
            }
        )

        return ResponseEntity(
            mapOf(
                "token" to fileToken,
            ),
            HttpStatus.OK.value()
        )
    }
}