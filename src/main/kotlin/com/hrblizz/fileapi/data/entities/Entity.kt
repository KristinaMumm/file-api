package com.hrblizz.fileapi.data.entities

import org.springframework.data.annotation.Id

class Entity {
    @Id
    lateinit var token: String
    lateinit var fileName: String
}
