package com.johannsn.cyclemapbackend.controller

import com.johannsn.cyclemapbackend.entity.Tour
import com.johannsn.cyclemapbackend.exception.EntityNotFoundException
import com.johannsn.cyclemapbackend.repository.TourRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.Optional

@RestController
@RequestMapping("/tours")
class TourController(val tourRepository: TourRepository){

    @GetMapping("")
    fun getTours(): ResponseEntity<MutableList<Tour>> = ResponseEntity(tourRepository.findAll(),HttpStatus.OK)

    @PostMapping("")
    fun postTour(@RequestBody tour: Tour): ResponseEntity<Tour> = ResponseEntity(tourRepository.save(tour), HttpStatus.CREATED)

    @GetMapping("/{tourId}")
    fun getTour(@PathVariable("tourId") tourId: Long): ResponseEntity<Optional<Tour>> {
        return if(tourRepository.existsById(tourId)) {
            ResponseEntity(tourRepository.findById(tourId),HttpStatus.OK)
        } else {
            throw EntityNotFoundException("Tour $tourId not found.")
        }
    }

    @DeleteMapping("/{tourId}")
    fun deleteTour(@PathVariable("tourId") tourId : Long): ResponseEntity<Tour>{
        return if(tourRepository.existsById(tourId)){
            tourRepository.deleteById(tourId)
            ResponseEntity(HttpStatus.NO_CONTENT)
        } else {
            throw EntityNotFoundException("The Tour $tourId does not exist.")
        }
    }
}
