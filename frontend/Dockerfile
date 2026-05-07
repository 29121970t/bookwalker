
ARG SERVER_NAME

FROM node:24-alpine as build
WORKDIR /app

ARG SERVER_NAME
ENV VITE_API_BASE_URL="http://${SERVER_NAME}/api"

COPY . .

RUN npm i
RUN npm run build

FROM nginx
ARG SERVER_NAME
ARG BACKEND_URL
ENV SERVER_NAME=$SERVER_NAME
ENV BACKEND_URL=$BACKEND_URL

COPY --from=build /app/dist /usr/share/nginx/html
COPY ./nginx/templates/ /etc/nginx/templates/


