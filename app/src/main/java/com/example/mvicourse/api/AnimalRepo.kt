package com.example.mvicourse.api

class AnimalRepo(private val api: AnimalAPI) {

    suspend fun getAnimals() = api.getAnimals()
}