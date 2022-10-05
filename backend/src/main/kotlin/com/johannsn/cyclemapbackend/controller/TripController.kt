package com.johannsn.cyclemapbackend.controller

import com.johannsn.cyclemapbackend.entity.Trip
import com.johannsn.cyclemapbackend.exception.EntityNotFoundException
import com.johannsn.cyclemapbackend.repository.TourRepository
import com.johannsn.cyclemapbackend.repository.TripRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class TripController(val tripRepository: TripRepository, val tourRepository: TourRepository) {

    @GetMapping("/trips")
    fun getTrips(): ResponseEntity<MutableList<Trip>> = ResponseEntity(tripRepository.findAll(),HttpStatus.OK)

    //TODO returns also the coordinates, therefore not the best method for android?
    @GetMapping("/tours/{tourId}/trips")
    fun getTripsForTour(@PathVariable("tourId") tourId: Long): ResponseEntity<MutableList<Trip>> {
        return if (tourRepository.existsById(tourId)) {
            ResponseEntity(tripRepository.findByTourId(tourId), HttpStatus.OK)
        } else {
            throw EntityNotFoundException("The Tour $tourId does not exist.")
        }
    }

    @PostMapping("/tours/{tourId}/trips")
    fun postTrip(@PathVariable("tourId") tourId: Long, @RequestBody trip: Trip): ResponseEntity<Trip> {
        return tourRepository.findById(tourId).map { tour ->
            trip.tour = tour
            for(coordinate in trip.coordinates) {
                coordinate.trip = trip
            }
            ResponseEntity(tripRepository.save(trip), HttpStatus.CREATED)
        }.orElseThrow {
            EntityNotFoundException("The Tour $tourId does not exist.")
        }
    }

    @DeleteMapping("/trips/{tripId}")
    fun deleteTrip(@PathVariable("tripId") tripId: Long): ResponseEntity<Trip> {
        return if (tripRepository.existsById(tripId)) {
            tripRepository.deleteById(tripId)
            ResponseEntity(HttpStatus.NO_CONTENT)
        } else {
            throw EntityNotFoundException("The Trip $tripId does not exist.")
        }
    }
}