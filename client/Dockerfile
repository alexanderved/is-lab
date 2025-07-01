FROM node:alpine

ENV APP=/usr/client
WORKDIR $APP

COPY ./client .
RUN yarn install
RUN yarn run build

EXPOSE 3000

CMD ["yarn", "serve", "-s", "dist"]