FROM node:10.16.1-alpine

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
ENV PATH /usr/src/app/node_modules/.bin:$PATH
COPY package*.json ./

RUN npm ci

COPY . /usr/src/app

RUN npm run build

EXPOSE 8282

CMD ["npm", "start"]
