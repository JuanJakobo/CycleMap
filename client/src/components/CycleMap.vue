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

      <l-polyline :lat-lngs="polyline.latlngs" :color="polyline.color"
            v-on:click="$bvModal.show('bv-modal-example')">
      </l-polyline>

      <!-- top left -->
      <l-control position="topleft">
        <table>
          <tr>
            <td>
              <b-button variant="light" href="homepage">
                <b-icon icon="arrow-left-square"></b-icon>
             </b-button>
            </td>
            <td>
              <b-form-select
                v-model="selected"
                :options="dropdownmenu"
                v-on:change="updateDropdown"
              />
            </td>
            <!-- TODO show only if route is selected, then blog entries can be opened-->
            <td>
              <b-form-select
                v-model="selected"
                :options="blogEntry"
                v-on:change="updateDropdown"
              />
            </td>

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

      <!-- card -->
      <b-modal
        id="bv-modal-example"
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
        <p class="my-4" v-for="i in 20" :key="i">
          Cras mattis consectetur purus sit amet fermentum. Cras justo odio,
          dapibus ac facilisis in, egestas eget quam. Morbi leo risus, porta ac
          consectetur ac, vestibulum at eros.
        </p>
        <b-button class="mt-3" block @click="$bvModal.hide('bv-modal-example')" >Close Me</b-button >
      </b-modal>

      <!-- change error process -->
      <section v-if="errored">
        <p>
          We're sorry, we're not able to retrieve this information at the
          moment, please try back later
        </p>
      </section>

      <section v-else>
        <div v-if="test">
          <l-marker
            ref="marker"
            v-for="(item, index) in markers"
            :key="'marker-' + index"
            :lat-lng="item.location"
            v-on:click="$bvModal.show('bv-modal-example')"
          >
            <!--v-on:click="onClick(index)"-->
          </l-marker>
        </div>
      </section>
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
  LMarker,
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
    LMarker,
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
      test: false,
      info: null,
      loading: true,
      errored: false,
      markers: [],
      selected: null,
      polyline: {
        latlngs: [[52.40783,12.42027],[52.40780,19.42028]],
        color: 'green'
      },
      dropdownmenu: [
        {
          value: null,
          text: "Select tour",
        },
      ],
      blogEntry: [
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
    updateDropdown() {
      this.markers.splice(0, this.markers.length);
      this.markers.push({
        location: latLng(19.432608, -99.133209),
        content: "test" + 2,
      });

      this.$refs.map.mapObject.fitBounds(
        this.markers.map((m) => {
          return [m.location.lat, m.location.lng];
        })
      );
    },
    openConfig() {
      alert("v.0.1");
    },
    getTours() {
        fetch("http://localhost:8989/tours",{
            "method":"GET"
        })
        .then(response => response.json())
        .then(data => (this.dropdownmenu = data))
        .catch(err=>{
                this.dropdownmenu.push({ value: "a", text: "ERROR" });
            console.log(err);
        });
    }
  },
  mounted() {
        this.getTours();

        for (let index = 0; index < 3; index++) {
          this.markers.push({
            location: latLng(19.432608, -99.133209 + index),
            content: "test" + index,
          });
        }
        this.markers[0].location = latLng(19.432608, -99.133209);
        this.test = true;
        this.$refs.map.mapObject.fitBounds(
          this.markers.map((m) => {
            return [m.location.lat, m.location.lng];
          })
        );



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
