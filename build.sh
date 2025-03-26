javac -cp "common/src/:client/src/" -d build/client client/src/client/Main.java common/src/common/network/responses/* common/src/common/network/requests/*
jar cfm build/client.jar client/META-INF/MANIFEST.MF -C build/client .

javac -cp "common/src/:server/src/" -d build/server server/src/server/Main.java common/src/common/network/responses/* common/src/common/network/requests/*
jar cfm build/server.jar server/META-INF/MANIFEST.MF -C build/server .
