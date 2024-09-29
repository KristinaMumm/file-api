package com.hrblizz.fileapi.controller.filecontroller

import com.hrblizz.fileapi.data.repository.EntityRepository
import com.hrblizz.fileapi.rest.ErrorMessage
import com.hrblizz.fileapi.rest.ResponseEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import javax.servlet.http.HttpServletResponse

@RestController
class FileGetController(
    private val fileRepository: EntityRepository
) {

    @RequestMapping("/file/{fileToken}", method = [RequestMethod.GET])
    fun getFile(@PathVariable fileToken: String, response: HttpServletResponse): ResponseEntity<Map<String, Any>>? {
        val file = fileRepository.findByToken(fileToken)

        val filePath: Path = Paths.get(FileControllerConstants.FILES_DIRECTORY + file.fileName)

        return if (Files.exists(filePath)) {
            response.contentType = MediaType.APPLICATION_PDF_VALUE
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${file.fileName}\"")
            response.setHeader("X-Filename", file.fileName)
            response.setHeader("X-Filesize", Files.size(filePath).toString())
            response.setHeader("X-CreateTime", file.createTime.toString())
            
            Files.copy(filePath, response.outputStream)
            response.outputStream.flush()
            return null
        } else {
            val errorMessage = listOf(ErrorMessage("File not found"))
            ResponseEntity(null, errorMessage, HttpStatus.NOT_FOUND.value())
        }
    }
}
