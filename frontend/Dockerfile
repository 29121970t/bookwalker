FROM node:24-alpine as build
WORKDIR /app

ENV VITE_API_URL=$BACKEND_URL

COPY . .

RUN npm i
RUN npm run build

FROM nginx

COPY --from=build /app/dist /usr/share/nginx/html
COPY ./nginx/templates/ /etc/nginx/templates/


