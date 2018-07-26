package fiddle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Enumeration;

public class Fiddle5 {
	public static void main(String[] args) throws KeyStoreException, NoSuchAlgorithmException, CertificateException,
			FileNotFoundException, IOException, UnrecoverableKeyException {
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		ks.load(new FileInputStream(new File("server.jks")), "password".toCharArray());
		Enumeration<String> aliases = ks.aliases();
		while (aliases.hasMoreElements()) {
			String alias = aliases.nextElement();
			System.out.println(alias);
			System.out.println(ks.isKeyEntry(alias));
			System.out.println(ks.isCertificateEntry(alias));
			System.out.println(ks.getKey(alias, "password".toCharArray()) != null);
			System.out.println(ks.getCertificate(alias) != null);
			Certificate[] chain = ks.getCertificateChain(alias);
			System.out.println(chain != null);
			if (chain != null) {
				System.out.println(chain.length);
			}

		}
	}
}
