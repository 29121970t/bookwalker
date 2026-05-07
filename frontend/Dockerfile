FROM node:24-alpine as build
WORKDIR /app
ARG API_URL
ENV VITE_API_URL=$API_URL

COPY . .

RUN npm i
RUN npm run build

FROM nginx
ARG PORT="8080"
ENV PORT=$PORT

COPY --from=build /app/dist /usr/share/nginx/html
COPY ./nginx/templates/ /etc/nginx/templates/
EXPOSE $PORT

