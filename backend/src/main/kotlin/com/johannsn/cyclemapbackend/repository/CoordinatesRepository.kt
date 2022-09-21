package com.johannsn.cyclemapbackend.repository

import com.johannsn.cyclemapbackend.entity.Coordinates
import org.springframework.data.jpa.repository.JpaRepository

interface CoordinatesRepository : JpaRepository<Coordinates, Long> {
    fun findByTripId(trip_id: Long?): MutableList<Coordinates>
    fun findByTourId(tour_id: Long?): MutableList<Coordinates>
}
