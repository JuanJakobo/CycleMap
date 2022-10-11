# CycleMap
Blog for bicycle tours displayed on a map

Tourview: <br>
<img src="/screenshots/map.png" width="70%" height="70%"><br>
Details for a trip: <br>
&nbsp;&nbsp;<img src="/screenshots/tripDetails.png" width="70%" height="70%">

## Features
* Display tours stored in the backend in a map view
* Show details regarding the stage
* Show pictures for the stage (WIP)

## Installation

### How do build

To build without using docker compose

Frontend:
`docker build -t cyclemap/frontend .` <br>
Backend:
`./gradlew bootBuildImage --imageName=cyclemap/backend`

Then upload the images to the server, edit the .env files so that it fits to your needs and run 
`docker-compose up`

## Usage
To upload tours to the backend you can use the [Cylcemap-Android-Upload-Client](https://github.com/JuanJakobo/Cyclemap-Android-Upload-Client).
Once upload the tours will appear in the map.
