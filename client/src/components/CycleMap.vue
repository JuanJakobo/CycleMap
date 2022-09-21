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
            <td>
              <b-button variant="light" href="homepage">
                <b-icon icon="arrow-left-square"></b-icon>
             </b-button>
            </td>
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
        <b-button variant="light" v-on:click="openConfig()"
          ><b-icon icon="gear-wide-connected"></b-icon
        ></b-button>
      </l-control>

      <!-- bottom right -->
      <!-- TODO LINK TO Private policy -->
      <l-control-zoom position="bottomright"></l-control-zoom>
      <l-control-scale
        position="bottomright"
        :imperial="false"
        :metric="true"
      ></l-control-scale>
      <l-tile-layer :url="url" :attribution="attribution" />

      <!-- Polylines -->
      <!-- TODO change error process -->
      <section v-if="FailedToLoad">
        <p>
        <!-- TODO to as overlay, as is never shown as is "overwritten" by the map -->
          We're sorry, we're not able to retrieve this information at the
          moment, please try back later
        </p>
      </section>
      <section v-else>
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
      <!-- handle the close button upper right -->
      <b-modal
        id="bv-modal-trip"
        hide-footer
        scrollable
        title="Scrollable Content"
        size="xl"
      >
        <template #modal-title>
          <div v-if="showTripDetails">
            {{tripsForTour[currentTour].title}}
          </div>
        </template>
          <div v-if="showTripDetails">
              <div>
              <b-carousel
              id="carousel-fade"
              style="text-shadow: 0px 0px 2px #000"
              fade
              indicators
              img-width="1024"
              img-height="480"
              >
              <b-carousel-slide
              caption="First slide"
              img-src="https://picsum.photos/1024/480/?image=10"
              ></b-carousel-slide>
              <b-carousel-slide
              caption="Second Slide"
              img-src="https://picsum.photos/1024/480/?image=12"
              ></b-carousel-slide>
              <b-carousel-slide
              caption="Third Slide"
              img-src="https://picsum.photos/1024/480/?image=22"
              ></b-carousel-slide>  <b-carousel-slide
              caption="Third Slide"
              img-src="https://picsum.photos/1024/480/?image=22"
              ></b-carousel-slide>
              </b-carousel>
              </div>
            {{tripsForTour[currentTour].text}}
        </div>
        <b-button class="mt-3" block @click="closeContent()" >Close Me</b-button >
      </b-modal>
    </l-map>
  </div>
</template>

<script>
import { latLng } from "leaflet";
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
  name: "Map",
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
       zoom: 9,
       center: latLng(52.432608, 12.133209),
       url: "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
       attribution:
        '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors',
      showParagraph: false,
      mapOptions: {
        zoomSnap: 0.5,
        zoomControl: false,
      },
      loading: true,
      FailedToLoad: false,
      tourHasTrips: false,
      showTripDetails: false,
      currentTour: null,
      tripsForTour:[],
      selectedTour: null,
      tourDropdown: [
        {
          value: null,
          text: "Select tour",
        },
      ],
      selectedTrip: null,
      tripDropdown: [
        {
          value: null,
          text: "Blog entry",
        },
      ],
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
    openConfig() {
      alert("v.0.1");
    },
    loadContent(itemId) {
        console.log(itemId);
        this.showTripDetails = true;
        this.currentTour = itemId-1;
        this.tripsForTour[this.currentTour].color = 'yellow';
        this.$bvModal.show("bv-modal-trip");
        //TODO zoom into
    },
    closeContent() {
        this.showTripDetails = false;
        this.tripsForTour[this.currentTour].color = 'black';
        this.$bvModal.hide('bv-modal-trip');
        this.selectedTrip = null;
    },
    changeColor(itemId) {
        if(this.tripsForTour[itemId-1].color == 'black')
        {
            this.tripsForTour[itemId-1].color = 'yellow';
            this.selectedTrip = itemId;
        }
        else
        {
            this.tripsForTour[itemId-1].color = 'black';
            this.selectedTrip = null;
        }
    },
    async getTours() {
        let response = await fetch("http://localhost:8989/tours",{
            "method":"GET"
        }).catch(err=>{
                console.log(err);
                this.failedToLoad = true;
        });

        if(response.status === 200){
            let data = await response.json();
            data.forEach((v) =>{
                this.tourDropdown.push({value : v.id, text: v.title});
            });
        console.log(this.tourDropdown.length);
        //if(this.tourDropdown.length == 2)
            //this.getCoordinates(this.tourDropdown[1].value);
        }
        else
        {
            console.log(response.status);
        }
    },
    async getCoordinates(tourId) {
        let response = await fetch("http://localhost:8989/tours/" + tourId + "/trips",{
                "method":"GET"
                })
        if (response.status === 200) {
            let data = await response.json();
            console.log(data);
            this.tripsForTour = [];
            data.forEach((value) =>{
                var coordinates = [];
                value.coordinates.forEach((v) => {
                        coordinates.push([v.lat,v.lng]);
                        });
                    this.tripsForTour.push({id: value.id, title: value.title, text: value.text, coordinates: coordinates, color: 'black'});
                    this.tripDropdown.push({value : value.id, text: value.title});
            });

            this.tourHasTrips = true;
            //TODO
            //this.ab = this.$refs.test.leaftletObject;
            //this.ab.getBounds();
            this.$refs.map.mapObject.fitBounds(this.polyline.getBounds());
        }
        else
        {
            console.log(response.status);
        }
    },
  },
  async beforeMount() {
        this.getTours();
  },
  mounted() {
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
