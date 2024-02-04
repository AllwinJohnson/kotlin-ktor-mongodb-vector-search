package com.mongodb.application.routes

import com.mongodb.domain.service.FitnessService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bson.types.ObjectId
import org.koin.ktor.ext.inject

fun Route.fitnessRouting() {
    val service by inject<FitnessService>()

    route("/fitness") {

        get {
            service.findAll()?.let {
                call.respond(it)
            } ?: call.respondText("No records found")
        }

        get("/id/{id?}") {
            val id = call.parameters["id"]
            if (id.isNullOrEmpty()) {
                return@get call.respondText(
                    text = "Missing id",
                    status = HttpStatusCode.BadRequest
                )
            }

            service.findById(ObjectId(id))?.let {
                call.respond(it)
            } ?: call.respondText("No records found for id $id")

        }

        get("/exerciseType/{type?}") {
            val type = call.parameters["type"]
            type?.takeIf { it.isNotEmpty() }?.let {
                call.respond(service.findByExerciseType(it).ifEmpty { "No records found for exerciseType $it" })
            } ?: call.respond(HttpStatusCode.BadRequest, "Missing exercise type param")
        }

        post {

        }
        delete("{id?") {

        }
    }
}



