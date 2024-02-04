package com.mongodb.infrastructure.repository

import com.mongodb.MongoException
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import com.mongodb.domain.entity.Fitness
import com.mongodb.domain.entity.FitnessDetails
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.BsonValue
import org.bson.types.ObjectId


class FitnessRepositoryImpl(private val mongoDatabase: MongoDatabase) : FitnessRepository {

    companion object {
        const val FITNESS_COLLECTION = "fitness"
    }

    override suspend fun findAll(): List<Fitness> {
        return mongoDatabase.getCollection<Fitness>(FITNESS_COLLECTION).find().toList()
    }

    override suspend fun findById(objectId: ObjectId): Fitness? =
        mongoDatabase.getCollection<Fitness>(FITNESS_COLLECTION).withDocumentClass<Fitness>()
            .find(eq("_id", objectId))
            .firstOrNull()

    override suspend fun findByExerciseType(type: String): List<Fitness> =
        mongoDatabase.getCollection<Fitness>(FITNESS_COLLECTION).withDocumentClass<Fitness>()
            .find(eq(Fitness::exerciseType.name, type))
            .toList()

    override suspend fun insertOne(fitness: Fitness): BsonValue? {

        try {
            val result = mongoDatabase.getCollection<Fitness>(FITNESS_COLLECTION).insertOne(
                fitness
            )

            return result.insertedId
        } catch (e: MongoException) {
            System.err.println("Unable to insert due to an error: $e")
        }

        return null
    }

    override suspend fun deleteById(objectId: ObjectId): Long {

        try {
            val result = mongoDatabase.getCollection<Fitness>(FITNESS_COLLECTION).deleteOne(eq("_id", objectId))
            return result.deletedCount
        } catch (e: MongoException) {
            System.err.println("Unable to delete due to an error: $e")
        }

        return 0

    }

    override suspend fun updateOne(objectId: ObjectId, fitness: Fitness): Long {
        try {
            val query = eq("_id", objectId)
            val updates = Updates.combine(
                Updates.set(Fitness::exerciseType.name, fitness.exerciseType),
                Updates.set(Fitness::startTime.name, fitness.startTime),
                Updates.set(Fitness::endTime.name, fitness.endTime),
                Updates.set(Fitness::notes.name, fitness.notes),
                Updates.set(FitnessDetails::durationMinutes.name, fitness.details.durationMinutes),
                Updates.set(FitnessDetails::distance.name, fitness.details.distance),
                Updates.set(FitnessDetails::caloriesBurned.name, fitness.details.caloriesBurned),
            )

            val options = UpdateOptions().upsert(true)

            val result =
                mongoDatabase.getCollection<Fitness>(FITNESS_COLLECTION)
                    .updateOne(query, updates, options)

            return result.modifiedCount
        } catch (e: MongoException) {
            System.err.println("Unable to update due to an error: $e")
        }

        return 0
    }
}