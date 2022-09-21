package com.johannsn.cyclemapbackend.controller

import com.johannsn.cyclemapbackend.entity.Coordinates
import com.johannsn.cyclemapbackend.exception.EntityNotFoundException
import com.johannsn.cyclemapbackend.repository.CoordinatesRepository
import com.johannsn.cyclemapbackend.repository.TourRepository
import com.johannsn.cyclemapbackend.repository.TripRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
//TODO
@CrossOrigin("http://localhost:3001")
class CoordinatesController(val coordinatesRepository : CoordinatesRepository,val tripRepository: TripRepository,val tourRepository: TourRepository) {

    @GetMapping("/tours/{tourId}/coordinates")
    fun getCoordinatesForTours(@PathVariable("tourId") tourId: Long): ResponseEntity<List<Coordinates>>? {
        return if (tourRepository.existsById(tourId)) {
            ResponseEntity(coordinatesRepository.findByTourId(tourId), HttpStatus.OK)
        } else {
            throw EntityNotFoundException("The Trip $tourId does not exist.")
        }
    }
    //TODO only every second?
    //get coordinates for tour and not for trip?
    @GetMapping("/trips/{tripId}/coordinates")
    fun getCoordinates(@PathVariable("tripId") tripId: Long): ResponseEntity<List<Coordinates>>? {
        return if (tripRepository.existsById(tripId)) {
            ResponseEntity(coordinatesRepository.findByTripId(tripId), HttpStatus.OK)
        } else {
            throw EntityNotFoundException("The Trip $tripId does not exist.")
        }
    }
    @PostMapping("/trips/{tripId}/coordinates")
    fun postCoordinates(@PathVariable("tripId") tripId: Long, @RequestBody coordinates: List<Coordinates>): ResponseEntity<List<Coordinates>>? {
        return tripRepository.findById(tripId).map { trip ->
            try {
                for(coordinate in coordinates) {
                    coordinate.trip = trip
                    coordinate.tour = trip.tour
                }
                ResponseEntity(coordinatesRepository.saveAll(coordinates), HttpStatus.CREATED)
            }catch (e : Exception) {
                throw Exception(e.message)
            }
        }.orElseThrow {
            throw EntityNotFoundException("TripId $tripId does not exist.")
        }
    }
}