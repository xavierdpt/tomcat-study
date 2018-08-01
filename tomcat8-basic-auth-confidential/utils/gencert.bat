keytool -genkey -alias localhost -keyalg RSA -keystore server.jks -storepass password -dname "CN=localhost" -keypass "password"
keytool -list -keystore server.jks -storepass password
keytool -importkeystore -srckeystore server.jks -destkeystore server.p12 -srcstoretype JKS -deststoretype PKCS12 -srcstorepass password -deststorepass password -srcalias localhost -destalias localhost -srckeypass password -destkeypass password
keytool -exportcert  -keystore server.jks -alias localhost -rfc -storepass password > server.pem
pause

