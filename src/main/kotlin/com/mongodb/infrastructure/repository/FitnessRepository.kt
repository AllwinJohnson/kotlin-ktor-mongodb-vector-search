package com.mongodb.infrastructure.repository

import com.mongodb.domain.entity.Fitness
import org.bson.types.ObjectId

interface FitnessRepository {

    suspend fun findAll(): List<Fitness>?

    suspend fun findById(objectId: ObjectId): Fitness?

    suspend fun findByExerciseType(type: String): List<Fitness>

 }