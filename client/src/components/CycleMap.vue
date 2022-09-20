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
            <section v-show="!errored">
            <td>
              <b-form-select
                v-model="selectedTour"
                id="tourDropdown"
                :options="tourDropdown"
                v-on:change="updateTourDropdown"
              />
            </td>
             <section v-show="tourHasTrips">
            <!-- TODO show only if route is selected, then blog entries can be opened-->
            <td>
              <b-form-select
                v-model="selectedTrip"
                :options="tripDropdown"
                v-on:change="updateTripDropdown"
              />
            </td>
            </section>
            </section>
          </tr>
        </table>
      </l-control>

      <!-- top right -->
      <l-control position="topright">
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
      <section v-if="errored">
        <p>
        <!-- TODO to as overlay, as is never shown as is "overwritten" by the map -->
          We're sorry, we're not able to retrieve this information at the
          moment, please try back later
        </p>
      </section>
      <section v-else>
        <div v-if="gotCoordinates">
        <l-polyline
        :lat-lngs="polyline.latlngs"
        :color="polyline.color"
        v-on:click="loadContent()"
        v-on:mouseover="changeColor(true)"
        v-on:mouseleave="changeColor(false)">
        </l-polyline>
        </div>
      </section>

      <!-- card -->
      <b-modal
        id="bv-modal-trip"
        hide-footer
        scrollable
        title="Scrollable Content"
        size="xl"
      >
        <template #modal-title> Ciudad de Mexico - Puebla</template>
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
                <template #footer>

          <small class="text-muted">Last updated 3 mins ago</small>
        </template>
        <b-button class="mt-3" block @click="$bvModal.hide('bv-modal-trip')" >Close Me</b-button >
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
       zoom: 5,
       center: latLng(19.432608, -99.133209),
       url: "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
       attribution:
        '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors',
      showParagraph: false,
      mapOptions: {
        zoomSnap: 0.5,
        zoomControl: false,
      },
      content: null,
      gotCoordinates: false,
      loading: true,
      errored: false,
      tourHasTrips: false,
      latl: [],
      polyline: {
        color: 'black',
      },
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
        console.log(this.selectedTour);
    },
    updateTripDropdown() {
    },
    openConfig() {
      alert("v.0.1");
    },
    loadContent() {
        this.$bvModal.show("bv-modal-trip");
    },
    changeColor(change) {
        if(change)
            this.polyline.color = "yellow";
        else
            this.polyline.color = "black";
    },
    async getTours() {
        let response = await fetch("http://localhost:8989/tours",{
            "method":"GET"
        }).catch(err=>{
                console.log(err);
                this.errored = true;
        });

        if(response.status === 200){
            let data = await response.json();
            data.forEach((value,index) =>{
                console.log(index);
                this.tourDropdown.push(value.title);
            });

        }
        else
        {
            console.log(response.status);
        }
    },
    async getCoordinates() {
        let response = await fetch("http://localhost:8989/trips/1/coordinates",{
                "method":"GET"
                })
        if (response.status === 200) {
            let data = await response.json();
            data.forEach((value) =>{
                    this.latl.push([value.lat,value.long]);
                    });
            this.gotCoordinates = true;
            this.polyline.latlngs = this.latl;
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
        this.getCoordinates();
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
