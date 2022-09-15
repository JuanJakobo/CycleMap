package com.johannsn.cyclemapbackend.repository

import com.johannsn.cyclemapbackend.entity.Trip
import org.springframework.data.jpa.repository.JpaRepository

interface TripRepository : JpaRepository<Trip, Long> {
    fun findByTourId(tour_id: Long?):  MutableList<Trip>
}
