<template>
  <div>
    <!-- draw the map -->
    <l-map
    ref="map"
    id="mapContainer"
    :zoom="zoom"
      :center="center"
      :options="mapOptions"
      @update:center="centerUpdate"
      @update:zoom="zoomUpdate"
    >
      <!-- top left -->
      <l-control position="topleft">
      <section v-show="loaded">
        <table>
          <tr>
            <td>
              <b-form-select
                v-model="selectedTour"
                :options="tours"
                v-on:change="updateTourDropdown"
              />
            </td>
            <td>
      <section v-if="tourHasTrips">
              <b-form-select
                v-model="selectedTrip"
                :options="tours[currentTourIndex].trips"
                value-field="id"
                text-field="title"
                v-on:change="updateTripDropdown"
              />
            </section>
            </td>
          </tr>
        </table>
            </section>
      </l-control>

      <!-- bottom left -->
      <l-control position="bottomleft">
        <b-button variant="light" v-on:click="openInfo()"
          ><b-icon icon="info"></b-icon
        ></b-button>
      </l-control>

      <!-- bottom right -->
      <l-control-zoom position="bottomright"></l-control-zoom>
      <l-control-scale
        position="bottomright"
        :imperial="false"
        :metric="true"
      ></l-control-scale>
      <l-tile-layer :url="url" :attribution="attribution" />

      <!-- Info modal -->
        <p>
        <b-modal
        id="bv-modal-info"
        hide-footer
        scrollable
        hide-header-close
        title="Scrollable Content"
        size="xl"
        >
        <template #modal-title>
          Info
        </template>
        Version 0.1
        <b-button class="mt-3" block @click="closeInfo()">Close</b-button >
      </b-modal>
        </p>

      <!-- loading Modal -->
      <b-modal
        id="bv-modal-loading"
        hide-header
        hide-footer
        scrollable
        hide-header-close
        title="Scrollable Content"
        size="sm"
      >
      <center>
        <b-button class="mt-3" disabled>
        <b-spinner small type="grow"></b-spinner>
        Loading...
        </b-button>
      </center>
      </b-modal>

      <!-- Error modal -->
        <p>
        <b-modal
        id="bv-modal-error"
        hide-footer
        scrollable
        hide-header-close
        title="Scrollable Content"
        size="xl"
        >
        <template #modal-title>
          Error
        </template>
        {{errorText}}
        <b-button class="mt-3" block @click="retryConnect()">Retry</b-button >
      </b-modal>
        </p>
      <!-- Polylines -->
      <section v-if="tourHasTrips">
        <l-polyline
          v-for="item in tours[currentTourIndex].trips"
          :key="item.id"
          :lat-lngs="item.coordinates"
          :color="item.color"
          :weight=6
          v-on:click="loadContent(item.id)"
          v-on:mouseover="changeColor(item.id)"
          v-on:mouseleave="changeColor(item.id)"/>
          <l-marker
            v-for="item in tours[currentTourIndex].trips"
            :key="item.title"
            :lat-lng="item.loc"
            v-on:click="loadContent(item.id)"
          >
          <l-tooltip>
            {{item.title}} <br>
            Click for more info.
            </l-tooltip>
          </l-marker>
      </section>

      <!-- trip info modal -->
      <b-modal
        id="bv-modal-trip"
        hide-footer
        scrollable
        hide-header-close
        title="Scrollable Content"
        size="xl"
      >
        <template #modal-title>
          <div v-if="showTripDetails">
            {{tours[currentTourIndex].trips[currentTrip].title}}
          </div>
        </template>
          <div v-if="showTripDetails">
          <!--TODO add images-->
            {{tours[currentTourIndex].trips[currentTrip].text}}
        </div>
        <b-button class="mt-3" block @click="closeContent()">Close Me</b-button >
      </b-modal>
    </l-map>
  </div>
</template>

<script>
import {
    latLng,
    latLngBounds
} from "leaflet";
import {
  LMap,
  LTileLayer,
  LTooltip,
  LControl,
  LControlScale,
  LControlZoom,
  LPolyline,
  LMarker,
} from "vue2-leaflet";
import { BButton } from "bootstrap-vue";

export default {
  components: {
    LMap,
    LTileLayer,
    LControl,
    LControlScale,
    LControlZoom,
    LPolyline,
    LMarker,
    LTooltip,
    BButton,
  },
  data() {
    return {
       image: null,
       zoom: 10,
       center: latLng(0.0, 0.0),
       endpoint: process.env.VUE_APP_BACKEND_API_URL,
       url: "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
       attribution:
        '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors',
      showParagraph: false,
      mapOptions: {
        zoomSnap: 0.5,
        zoomControl: false,
      },
      loaded: false,
      tourHasTrips: false,
      showTripDetails: false,
      errorText: null,
      tourBounds: null,
      currentTrip: null,
      currentTourIndex: null,
      selectedTour: null,
      tours: [],
      selectedTrip: null,
      tripDropdown: [],
    }
  },
  methods: {
    zoomUpdate(zoom) {
      this.currentZoom = zoom;
    },
    centerUpdate(center) {
      this.currentCenter = center;
    },
    updateTourDropdown() {
        this.getCoordinates(this.selectedTour);
    },
    updateTripDropdown() {
        if(this.selectedTrip != null)
            this.loadContent(this.selectedTrip);
    },
    openInfo() {
        this.$bvModal.show("bv-modal-info");
    },
    closeInfo() {
        this.$bvModal.hide("bv-modal-info");
    },
    loadContent(itemId) {
        this.showTripDetails = true;
        this.currentTrip = itemId;
        this.tours[this.currentTourIndex].trips[this.currentTrip].color = 'red';
        this.$bvModal.show("bv-modal-trip");
        let tripBounds = latLngBounds();
        for(let coordinate in this.tours[this.currentTourIndex].trips[itemId].coordinates){
            tripBounds.extend(this.tours[this.currentTourIndex].trips[itemId].coordinates[coordinate]);
        }
        if(tripBounds.isValid())
            this.$refs.map.mapObject.fitBounds(tripBounds);
    },
    closeContent() {
        this.showTripDetails = false;
        this.tours[this.currentTourIndex].trips[this.currentTrip].color = "black";
        this.$bvModal.hide('bv-modal-trip');
        this.selectedTrip = null;
        if(this.tourBounds.isValid())
            this.$refs.map.mapObject.fitBounds(this.tourBounds);
    },
    drawBounds(index, tourId) {
        if(this.tourBounds.isValid()){
            this.$refs.map.mapObject.fitBounds(this.tourBounds);
            this.tourHasTrips = true;
            this.loaded = true;
            this.currentTourIndex = index;
        }else{
            this.errorText = "There are no Coordinates available for Tour " + tourId + ".";
            this.$bvModal.show("bv-modal-error");
        }
    },
    retryConnect() {
        this.$bvModal.hide('bv-modal-error');
        this.getTours();
    },
    changeColor(itemId) {
        if(this.tours[this.currentTourIndex].trips[itemId].color === "black")
        {
            this.tours[this.currentTourIndex].trips[itemId].color = "red";
            this.selectedTrip = itemId;
        }
        else if(!this.showTripDetails)
        {
            this.tours[this.currentTourIndex].trips[itemId].color = "black";
            this.selectedTrip = null;
        }
    },
    async getTours() {
        fetch(this.endpoint + "/tours",{ "method":"GET" })
        .then((response) => {
            this.$bvModal.show("bv-modal-loading");
            if(response.status === 200){
                return response.json();
            }else{
                    throw new Error("HTTP status " + response.status);
                    }})
        .then((data) =>
                {
                data.forEach((v) =>{
                        this.tours.push({value : v.id, text: v.title, trips: []});
                        });
                if(this.tours.length > 0){
                this.selectedTour = this.tours[this.tours.length-1].value;
                this.getCoordinates(this.tours.length);
                }else{
                this.errorText = "There are no Tours saved on the server.";
                this.$bvModal.show("bv-modal-error");
                }
                })
        .catch(err=>{
                this.$bvModal.hide("bv-modal-loading");
                this.errorText = "Could not connect to the API. (" + err + ")";
                this.$bvModal.show("bv-modal-error");
                });
    },

    async getCoordinates(tourId) {
        let index = this.tours.map(function(e) { return e.value; }).indexOf(tourId);
        this.tourBounds = latLngBounds();
        if(this.tours[index].trips.length > 0){
            for(let trip in this.tours[index].trips){
                for(let coordinate in this.tours[index].trips[trip].coordinates){
                    this.tourBounds.extend(this.tours[index].trips[trip].coordinates[coordinate]);
                }
            }
                    this.drawBounds(index, tourId);
        }else{
            fetch(this.endpoint + "/tours/" + tourId + "/trips",{"method":"GET" })
                .then((response) => {
                        this.$bvModal.show("bv-modal-loading");
                        if(response.status === 200){
                        return response.json();
                        }else{
                        throw new Error("HTTP status " + response.status);
                        }
                        })
            .then((data) => {
                    let i = 0;
                    //this.tours[index].trips.push({id: null, title: "Select a stage"});
                    data.forEach((value) =>{
                            var coordinates = [];
                            value.coordinates.forEach((v) => {
                                    coordinates.push([v.lat,v.lng]);
                                    this.tourBounds.extend([v.lat,v.lng]);
                                    });
                            //TODO id should be value.id

                            this.tours[index].trips.push({id: i, title: value.title, text: value.text, coordinates: coordinates, color: "black", loc: coordinates[parseInt(coordinates.length/2)]});
                            i++;
                            });
                    this.drawBounds(index, tourId);
            })
            .catch(err=>{
                    this.errorText = "Could not connect to the API. (" + err + ")";
                    this.$bvModal.show("bv-modal-error");
                    })
            .finally(() => {
                this.$bvModal.hide("bv-modal-loading");
            });
        }
        },
    },
    async beforeMount() {
        this.getTours();
    },
}
</script>

<style scoped>
#mapContainer {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
}
h3 {
  margin: 40px 0 0;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
</style>
