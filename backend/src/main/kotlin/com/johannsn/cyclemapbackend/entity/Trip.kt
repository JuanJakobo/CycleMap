package com.johannsn.cyclemapbackend.entity

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import javax.persistence.*

@Entity
@Table(name = "TRIPS")
@JsonPropertyOrder("id", "text","tour", "coordinates")
data class Trip(
    @Column(nullable = false)
    val title: String,

    @Column(nullable = false)
    val text: String,

    //TODO
    //@Column(nullable = true)
    //val imageUrls: List<String>? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false)
    var tour: Tour,

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "trip", cascade = [CascadeType.ALL])
    val coordinates: MutableList<Coordinates> = mutableListOf(),

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trip_id")
    val id: Long = 0,
)
