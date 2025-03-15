package com.example.mvicourse.api

import com.example.mvicourse.model.Animal
import retrofit2.http.GET

interface AnimalAPI {
    @GET("animals.json")
    suspend fun getAnimals() : List<Animal>
}