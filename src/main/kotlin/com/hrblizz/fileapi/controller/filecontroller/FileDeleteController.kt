package com.hrblizz.fileapi.controller.filecontroller

import com.hrblizz.fileapi.data.entities.Entity
import com.hrblizz.fileapi.data.repository.EntityRepository
import com.hrblizz.fileapi.library.log.ExceptionLogItem
import com.hrblizz.fileapi.library.log.LogItem
import com.hrblizz.fileapi.library.log.Logger
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
    private val fileRepository: EntityRepository,
    private val logger: Logger
) {
    @RequestMapping("/file/{fileToken}", method = [RequestMethod.DELETE])
    fun deleteFile(@PathVariable fileToken: String): ResponseEntity<Map<String, Any>>? {
        val fileEntity : Entity

        try {
            fileEntity = fileRepository.findByToken(fileToken)
        } catch (e: EmptyResultDataAccessException) {
            val warningMessage = "Token $fileToken not found"
            logger.warning(ExceptionLogItem(warningMessage, e))
            return ResponseEntity(null, listOf(ErrorMessage(warningMessage)), HttpStatus.BAD_REQUEST.value())
        }

        val file = File(FileControllerConstants.FILES_DIRECTORY, fileEntity.fileName)

        if (file.exists()) {
            try {
                file.delete()
                logger.info(LogItem("File with token $fileToken and name ${fileEntity.fileName} deleted"))
            } catch (e: SecurityException) {
                val errorMessage = "File can not be deleted"
                logger.crit(ExceptionLogItem(errorMessage + " ${file.name}", e))
                return ResponseEntity(null, listOf(ErrorMessage(errorMessage)), HttpStatus.SERVICE_UNAVAILABLE.value())
            }
        } else {
            logger.error(LogItem("File ${file.name} not found"))
        }

        fileRepository.delete(fileEntity)

        return ResponseEntity(HttpStatus.OK.value())
    }
}
