javac -cp "common/src/:client/src/" -d build/client client/src/client/Main.java
jar cfm build/client.jar client/META-INF/MANIFEST.MF -C build/client .

