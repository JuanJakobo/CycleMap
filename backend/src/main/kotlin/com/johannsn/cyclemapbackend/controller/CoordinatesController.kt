package com.johannsn.cyclemapbackend.controller

import com.johannsn.cyclemapbackend.entity.Coordinates
import com.johannsn.cyclemapbackend.exception.EntityNotFoundException
import com.johannsn.cyclemapbackend.repository.CoordinatesRepository
import com.johannsn.cyclemapbackend.repository.TripRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class CoordinatesController(val coordinatesRepository : CoordinatesRepository, val tripRepository: TripRepository) {

    @PostMapping("/trips/{tripId}/coordinates")
    fun postCoordinates(@PathVariable("tripId") tripId: Long, @RequestBody coordinates: List<Coordinates>): ResponseEntity<List<Coordinates>>? {
        return tripRepository.findById(tripId).map { trip ->
            try {
                for(coordinate in coordinates)
                    coordinate.trip = trip
                ResponseEntity(coordinatesRepository.saveAll(coordinates), HttpStatus.CREATED)
            }catch (e : Exception) {
                throw Exception()
            }
        }.orElseThrow {
            throw EntityNotFoundException("TripId $tripId does not exist.")
        }
    }
}