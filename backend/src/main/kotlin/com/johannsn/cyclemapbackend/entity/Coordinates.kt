package com.johannsn.cyclemapbackend.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import javax.persistence.*

@Entity
@Table(name = "COORDINATES")
@JsonPropertyOrder("id", "long", "lat", "altitude")
data class Coordinates (

@Column(nullable = true)
val lng: Double = 0.0,

@Column(nullable = true)
val lat: Double = 0.0,

@Column(nullable = true)
val altitude: Double = 0.0,

@ManyToOne()
@JoinColumn(name = "trip_id", nullable = false)
@JsonIgnore
var trip: Trip? = null,

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "coordinates_id")
val id: Long = 0,
)