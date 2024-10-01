package com.hrblizz.fileapi.controller.filecontroller

import com.hrblizz.fileapi.data.entities.Entity
import com.hrblizz.fileapi.data.repository.EntityRepository
import com.hrblizz.fileapi.library.log.ExceptionLogItem
import com.hrblizz.fileapi.library.log.LogItem
import com.hrblizz.fileapi.library.log.Logger
import com.hrblizz.fileapi.rest.ErrorMessage
import com.hrblizz.fileapi.rest.ResponseEntity
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
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
    private val fileRepository: EntityRepository,
    private val logger: Logger
) {

    @RequestMapping("/file/{fileToken}", method = [RequestMethod.GET])
    fun getFile(@PathVariable fileToken: String, response: HttpServletResponse): ResponseEntity<Map<String, Any>>? {
        val fileEntity : Entity

        try {
            fileEntity = fileRepository.findByToken(fileToken)
        } catch (e: EmptyResultDataAccessException) {
            val errorMessage = "Token $fileToken not found"
            logger.warning(ExceptionLogItem(errorMessage, e))
            return ResponseEntity(null, listOf(ErrorMessage(errorMessage)), HttpStatus.BAD_REQUEST.value())
        }

        val filePath: Path = Paths.get(FileControllerConstants.FILES_DIRECTORY + fileEntity.fileName)

        if (Files.exists(filePath)) {
            response.contentType = fileEntity.contentType
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${fileEntity.fileName}\"")
            response.setHeader("X-Filename", fileEntity.fileName)
            response.setHeader("X-Filesize", Files.size(filePath).toString())
            response.setHeader("X-CreateTime", fileEntity.createTime.toString())

            Files.copy(filePath, response.outputStream)
            response.outputStream.flush()
            return null
        } else {
            val errorMessage = "File ${fileEntity.fileName} not found"
            logger.error(LogItem(errorMessage))
            return ResponseEntity(null, listOf(ErrorMessage(errorMessage)), HttpStatus.BAD_REQUEST.value())
        }
    }
}
