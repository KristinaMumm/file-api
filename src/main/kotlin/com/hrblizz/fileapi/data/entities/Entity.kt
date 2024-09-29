package com.hrblizz.fileapi.data.entities

import org.springframework.data.annotation.Id
import java.util.*

class Entity {
    @Id
    lateinit var token: String
    lateinit var fileName: String
    lateinit var contentType: String
    lateinit var metadata: String
    lateinit var source: String
    lateinit var expireTime: Date
    lateinit var createTime: Date
}
