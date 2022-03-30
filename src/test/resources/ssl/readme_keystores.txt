
Since OpenJDK 13.0.2/11.0.6 it is required that CA certificates have the extension CA=true.

# keytool -genkey -keyalg RSA -keysize 2048 -validity 36500 -alias ourkey -keystore keystore.jks -storetype jks -storepass storepwd -keypass keypwd -dname "CN=localhost" -ext BC=CA:true

# keytool -exportcert -rfc -alias ourkey -keystore keystore.jks -storetype jks -storepass storepwd -keypass keypwd -file theircert.pem

# keytool -import -keystore truststore.p12 -storetype pkcs12 -storepass trustpwd -alias theirkey -noprompt -file theircert.pem

