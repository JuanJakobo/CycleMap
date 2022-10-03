# build stage
FROM node:lts-alpine as build-stage

ARG VUE_APP_BACKEND_API_URL
ENV VUE_APP_BACKEND_API_URL ${VUE_APP_BACKEND_API_URL}

WORKDIR /app
COPY ./client/package*.json .
RUN npm install

COPY ./client/ .
RUN npm run build

# production stage
FROM nginx:stable-alpine as production-stage

COPY --from=build-stage /app/dist /usr/share/nginx/html
EXPOSE 3001
CMD ["nginx", "-g", "daemon off;"]]
