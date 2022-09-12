package com.example.Cyclemapbackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*

@SpringBootApplication
class CyclemapBackendApplication

fun main(args: Array<String>) {
	runApplication<CyclemapBackendApplication>(*args)
}

@RestController
//TODO temp
@CrossOrigin("http://localhost:3001")
class TourRessource(val service: TourService){
	@GetMapping("/tours")
	fun getTours(): List<Tour> = service.findTours()
	@GetMapping("/tours/{id}")
	fun getTour(@PathVariable("id") id : Int): Tour = service.findTour(id)
	@PostMapping("/tours")
	fun postTour(@RequestBody tour: Tour){
		service.addTour(tour)
	}
	@GetMapping("/tours/{tour_id}/trips")
	fun getTrip(@PathVariable("tour_id") tour_id: Int ): List<Trip> = service.findTrips(tour_id)
}

//Data class
@Table("Tours")
data class Tour(@Id val id: Int, val text: String)
@Table("Trips")
data class Trip(@Id val id: Int, val text: String, val tour_id: Int)

interface TourRepository : CrudRepository<Tour, String> {
	@Query("select * from tours")
	fun findTours(): List<Tour>
	@Query("select * from tours where id = :id")
	fun findTour(id: Int): Tour
	@Query("select * from trips where tour_id = :tour_id")
	fun findTrips(tour_id: Int): List<Trip>
}

@Service
class TourService(val db: TourRepository) {
	fun findTours(): List<Tour> = db.findTours()
	fun findTour(id: Int): Tour = db.findTour(id)
	fun addTour(tour: Tour) {
		db.save(tour)
	}
	fun findTrips(tour_id: Int): List<Trip> = db.findTrips(tour_id)
}