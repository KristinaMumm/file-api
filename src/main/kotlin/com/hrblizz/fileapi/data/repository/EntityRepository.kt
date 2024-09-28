package com.hrblizz.fileapi.data.repository

import com.hrblizz.fileapi.data.entities.Entity
import org.springframework.data.mongodb.repository.MongoRepository

interface EntityRepository : MongoRepository<Entity, Long> {
    fun findByToken(fileToken: String): Entity
}
