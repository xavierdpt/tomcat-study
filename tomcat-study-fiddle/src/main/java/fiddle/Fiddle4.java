package fiddle;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.io.IOUtils;

public class Fiddle4 {

	private static final String WORKING = "https://localhost:9999/authenticate";
	private static final String TARGET = "https://localhost:8443/tomcat9/protected";

	public static void main(String[] args) throws MalformedURLException, IOException, KeyStoreException,
			NoSuchAlgorithmException, KeyManagementException, CertificateException, UnrecoverableKeyException {

		System.setProperty("javax.net.debug", "ssl");

		connect(TARGET);

	}

	private static void connect(String target) throws NoSuchAlgorithmException, KeyManagementException,
			UnrecoverableKeyException, KeyStoreException, CertificateException, IOException {

		SSLContext sc = SSLContext.getInstance("TLS");
		sc.init(getKeyManagers(), getTrustManagers(), new SecureRandom());

		HttpsURLConnection con = (HttpsURLConnection) new URL(target).openConnection();

		con.setSSLSocketFactory(sc.getSocketFactory());

		con.connect();

		IOUtils.copy(con.getInputStream(), System.out);

	}

	private static KeyManager[] getKeyManagers() throws KeyStoreException, NoSuchAlgorithmException,
			CertificateException, IOException, UnrecoverableKeyException {

		KeyStore clientKeyStore = KeyStore.getInstance("PKCS12");

		FileInputStream clientFis = new FileInputStream(new File(
				"C:\\Users\\User\\Documents\\xavierdpt\\tomcat-study\\tomcat-study-fiddle\\nodejstest\\alice.p12"));
		clientKeyStore.load(clientFis, "password".toCharArray());
		clientFis.close();

		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
		keyManagerFactory.init(clientKeyStore, "password".toCharArray());

		return keyManagerFactory.getKeyManagers();
	}

	private static TrustManager[] getTrustManagers()
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {

		KeyStore serverKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());

		FileInputStream serverFis = new FileInputStream(
				new File("C:\\Users\\User\\Documents\\xavierdpt\\tomcat-study\\tomcat-study-fiddle\\server.jks"));
		serverKeyStore.load(serverFis, "password".toCharArray());
		serverFis.close();

		TrustManagerFactory serverTrustManagerFactory = TrustManagerFactory.getInstance("SunX509");
		serverTrustManagerFactory.init(serverKeyStore);

		return serverTrustManagerFactory.getTrustManagers();
	}

}
