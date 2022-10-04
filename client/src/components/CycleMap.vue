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
        <table>
          <tr>
            <section v-show="!failedToLoad">
            <td>
              <b-form-select
                v-model="selectedTour"
                id="tourDropdown"
                :options="tourDropdown"
                v-on:change="updateTourDropdown"
              />
            </td>
            <td>
             <section v-show="tourHasTrips">
              <b-form-select
                v-model="selectedTrip"
                :options="tripDropdown"
                v-on:change="updateTripDropdown"
              />
              </section>
            </td>
            </section>
          </tr>
        </table>
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

      <!-- Polylines -->
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
      <section v-if="!failedToLoad">
        <div v-if="tourHasTrips">
        <l-polyline
          v-for="item in tripsForTour"
          :key="item.id"
          :lat-lngs="item.coordinates"
          :color="item.color"
          v-on:click="loadContent(item.id)"
          v-on:mouseover="changeColor(item.id)"
          v-on:mouseleave="changeColor(item.id)"/>
        </div>
      </section>

      <!-- card -->
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
            {{tripsForTour[currentTour].title}}
          </div>
        </template>
          <div v-if="showTripDetails">
          <!--TODO add images-->
            {{tripsForTour[currentTour].text}}
        </div>
        <b-button class="mt-3" block @click="closeContent()">Close Me</b-button >
      </b-modal>
    </l-map>
  </div>
</template>

<script>
import { latLng, latLngBounds } from "leaflet";
import {
  LMap,
  LTileLayer,
  LControl,
  LControlScale,
  LControlZoom,
  LPolyline,
} from "vue2-leaflet";
import { BButton } from "bootstrap-vue";

export default {
  components: {
    LMap,
    LTileLayer,
    LControl,
    LControlScale,
    LControlZoom,
    BButton,
    LPolyline,
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
      loading: true,
      failedToLoad: false,
      tourHasTrips: false,
      showTripDetails: false,
      errorText: null,
      tourBounds: null,
      currentTour: null,
      selectedTour: null,
      tourDropdown: [
        {
          value: null,
          text: "Select tour",
        },
      ],
      tripsForTour:[],
      selectedTrip: null,
      tripDropdown: [],
    };
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
        for(let coordinate in this.tripsForTour[itemId].coordinates){
            tripBounds.extend(this.tripsForTour[itemId].coordinates[coordinate]);
        }
        if(tripBounds.isValid())
            this.$refs.map.mapObject.fitBounds(tripBounds);
    },
    closeContent() {
        this.showTripDetails = false;
        this.tripsForTour[this.currentTour].color = 'black';
        this.$bvModal.hide('bv-modal-trip');
        this.selectedTrip = null;
        if(this.tourBounds.isValid())
            this.$refs.map.mapObject.fitBounds(this.tourBounds);
    },
    retryConnect() {
        this.$bvModal.hide('bv-modal-error');
        this.getTours();
    },
    changeColor(itemId) {
        if(this.tripsForTour[itemId].color == 'black')
        {
            this.tripsForTour[itemId].color = 'red';
            this.selectedTrip = itemId;
        }
        else if(!this.showTripDetails)
        {
            this.tripsForTour[itemId].color = 'black';
            this.selectedTrip = null;
        }
    },
    async getTours() {
        fetch(this.endpoint + "/tours",{ "method":"GET" })
            .then((response) => {
                if(response.status === 200){
                       return response.json();
                    }else{
                        throw new Error("HTTP status " + response.status);
                    }})
            .then((data) =>
            {
                data.forEach((v) =>{
                        this.tourDropdown.push({value : v.id, text: v.title});
                        });
                if(this.tourDropdown.length > 1){
                    this.selectedTour = 1;
                    this.getCoordinates(this.selectedTour);
                }
            })
            .catch(err=>{
                this.failedToLoad = true;
                this.errorText = "Could not connect to the API. (" + err + ")";
                this.$bvModal.show("bv-modal-error");
                })
    },
    async getCoordinates(tourId) {
        fetch(this.endpoint + "/tours/" + tourId + "/trips",{"method":"GET" })
            .then((response) => {
                if(response.status === 200){
                       return response.json();
                    }else{
                        throw new Error("HTTP status " + response.status);
                    }})
            .then((data) => {
                this.tripsForTour = [];
                var i = 0;
                this.tripDropdown = [];
                this.tripDropdown.push({value: null, text: "Blog entry"});
                this.tourBounds = latLngBounds();
                data.forEach((value) =>{
                        var coordinates = [];
                        value.coordinates.forEach((v) => {
                                coordinates.push([v.lat,v.lng]);
                                this.tourBounds.extend([v.lat,v.lng]);
                                });
                        this.tripsForTour.push({id: i, title: value.title, text: value.text, coordinates: coordinates, color: 'black'});
                        this.tripDropdown.push({value : i, text: value.title});
                        i++;
                        });

                if(this.tourBounds.isValid())
                    this.$refs.map.mapObject.fitBounds(this.tourBounds);
                this.tourHasTrips = true;
            })
            .catch(err=>{
                    this.errorText = "Could not connect to the API. (" + err + ")";
                    this.$bvModal.show("bv-modal-error");
            });
            }
  },
  async beforeMount() {
        this.getTours();
  },
};
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
