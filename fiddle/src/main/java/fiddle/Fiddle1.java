package fiddle;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import xdptdr.asn.OIDS;
import xdptdr.asn.builders.X509Builder;

public class Fiddle1 {
	private static final String ALIAS = "youpi";
	private static final char[] PASSWORD = "password".toCharArray();

	public static void main(String[] args) throws KeyStoreException, NoSuchAlgorithmException, CertificateException,
			IOException, InvalidKeyException, SignatureException, UnrecoverableKeyException {

		String dest = "C:\\Users\\User\\eclipse-workspace-tomcat\\Servers\\tomcat9-config\\localhost-rsa.jks";
		File file = new File(dest);
		if (file.exists()) {
			file.delete();
		}

		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		KeyPair keyPair = kpg.generateKeyPair();

		X509Builder b = new X509Builder();
		b.setEncodedPublicKey(keyPair.getPublic().getEncoded());
		b.setSubjectName("CN=localhost");
		b.setIssuerName("CN=localhost");
		b.setSubjectKeyIdentifier(new byte[] { 0 });
		b.setAuthorityKeyIdentifier(new byte[] { 0 });
		b.setNotBefore(time(2018, Calendar.JANUARY, 1, 0, 0, 0));
		b.setNotAfter(time(2018, Calendar.DECEMBER, 31, 23, 59, 59));
		b.setSerial(1);
		b.getExtKeyUsages().add(OIDS.CLIENT_AUTH);
		b.getExtKeyUsages().add(OIDS.SERVER_AUTH);
		b.addSubjectAlternativeNameExtension("DNS", "localhost");
		byte[] certb = b.encode(keyPair.getPrivate());

		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		Certificate cert = cf.generateCertificate(new ByteArrayInputStream(certb));

		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		ks.load(null, PASSWORD);
		Certificate[] chain = new Certificate[] { cert };
		System.out.println(cert.getClass().getName());
		ks.setKeyEntry(ALIAS, keyPair.getPrivate(), PASSWORD, chain);
		System.out.println(ks.getCertificate(ALIAS) != null);
		System.out.println(ks.getKey(ALIAS, PASSWORD) != null);

		FileOutputStream fos = new FileOutputStream(file);
		ks.store(fos, PASSWORD);
		fos.close();

	}

	private static Date time(int year, int month, int day, int hour, int minute, int second) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cal.set(year, month, day, hour, minute, second);
		return cal.getTime();
	}

}
