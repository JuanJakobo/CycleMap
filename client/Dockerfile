# build stage
FROM node:lts-alpine as build-stage
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# production stage
FROM nginx:stable-alpine as production-stage
COPY --from=build-stage /app/dist /usr/share/nginx/html
EXPOSE 3001
CMD ["nginx", "-g", "daemon off;"]]
CMD ["/bin/sh", "-c", "sed -i 's/listen  .*/listen 3001;/g' /etc/nginx/conf.d/default.conf && exec nginx -g 'daemon off;'"]