package fiddle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Enumeration;

public class Fiddle2 {
	public static void main(String[] args) throws KeyStoreException, NoSuchAlgorithmException, CertificateException,
			FileNotFoundException, IOException, UnrecoverableKeyException {

		String src = "C:\\Users\\User\\eclipse-workspace-tomcat\\Servers\\tomcat9-config\\localhost-rsa.jks";
		String dest = "C:\\Users\\User\\eclipse-workspace-tomcat\\Servers\\tomcat9-config\\localhost-rsa.pkcs";

		KeyStore jks = KeyStore.getInstance(KeyStore.getDefaultType());
		jks.load(new FileInputStream(new File(src)), "password".toCharArray());

		KeyStore pkcs = KeyStore.getInstance("PKCS12");
		pkcs.load(null, "password".toCharArray());

		Enumeration<String> aliases = jks.aliases();
		while (aliases.hasMoreElements()) {
			String alias = aliases.nextElement();
			if (jks.isKeyEntry(alias)) {
				Key key = jks.getKey(alias, "password".toCharArray());
				Certificate[] chain = jks.getCertificateChain(alias);
				pkcs.setKeyEntry(alias, key, "password".toCharArray(), chain);
			}
			if (jks.isCertificateEntry(alias)) {
				Certificate cert = jks.getCertificate(alias);
				pkcs.setCertificateEntry(alias, cert);
			}
		}

		FileOutputStream fos = new FileOutputStream(new File(dest));
		pkcs.store(fos, "password".toCharArray());
		fos.close();

	}
}
