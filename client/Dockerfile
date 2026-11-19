FROM node:alpine

ENV APP=/usr/client
WORKDIR $APP

RUN npm install -g yarn

COPY ./client .
RUN yarn install
RUN yarn run build

EXPOSE 3000

CMD ["yarn", "serve", "-s", "dist"]