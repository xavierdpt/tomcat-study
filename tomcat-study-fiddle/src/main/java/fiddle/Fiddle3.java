package fiddle;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.apache.commons.io.IOUtils;

import xdptdr.asn.builders.X509Builder;
import xdptdr.asn.pem.PEMUtils;
import xdptdr.asn.utils.KeyIdentifierUtils;
import xdptdr.common.Common;

public class Fiddle3 {

	private static final char[] PASSWORD = "password".toCharArray();
	private static Random rnd = new Random(System.currentTimeMillis());

	public static void main(String[] args)
			throws FileNotFoundException, IOException, CertificateException, NoSuchAlgorithmException,
			InvalidKeySpecException, InvalidKeyException, SignatureException, KeyStoreException {

		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		KeyPair serverKeyPair = kpg.generateKeyPair();
		KeyPair aliceKeyPair = kpg.generateKeyPair();

		byte[] serverBytes = buildCACertificate(serverKeyPair.getPublic(), serverKeyPair.getPrivate());

		byte[] aliceBytes = buildAliceCertificate(aliceKeyPair.getPublic(), serverKeyPair.getPublic(),
				serverKeyPair.getPrivate());

		CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
		Certificate aliceCert = certificateFactory.generateCertificate(new ByteArrayInputStream(aliceBytes));
		Certificate serverCert = certificateFactory.generateCertificate(new ByteArrayInputStream(serverBytes));

		saveServerPEM(serverBytes, serverKeyPair.getPrivate());

		saveAliceP12(aliceKeyPair.getPrivate(), aliceCert);
		saveServerP12(serverCert);

		saveServerJKS(serverKeyPair.getPrivate(), serverCert);

		System.out.println("Done.");

	}

	private static void saveServerJKS(Key privateKey, Certificate certificate)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		ks.load(null, PASSWORD);
		ks.setCertificateEntry("serverCert", certificate);
		ks.setKeyEntry("serverKey", privateKey, PASSWORD, new Certificate[] { certificate });
		FileOutputStream fos = new FileOutputStream(new File("server.jks"));
		ks.store(fos, PASSWORD);
		fos.close();

	}

	private static void saveServerPEM(byte[] serverBytes, PrivateKey privateKey)
			throws FileNotFoundException, IOException {
		String serverCertPem = PEMUtils.getCertificatePEM(serverBytes);
		IOUtils.copy(new StringReader(serverCertPem), new FileOutputStream(new File("nodejstest/server_cert.pem")),
				Charset.forName("UTF-8"));

		String serverKeyPem = PEMUtils.getPrivateKeyPEM(privateKey);
		IOUtils.copy(new StringReader(serverKeyPem), new FileOutputStream(new File("nodejstest/server_key.pem")),
				Charset.forName("UTF-8"));
	}

	private static void saveAliceP12(PrivateKey privateKey, Certificate certificate)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		KeyStore alice = KeyStore.getInstance("PKCS12");
		alice.load(null, PASSWORD);
		alice.setKeyEntry("alice", privateKey, PASSWORD, new Certificate[] { certificate });
		alice.store(new FileOutputStream(new File("nodejstest/alice.p12")), PASSWORD);
	}
	
	private static void saveServerP12(Certificate certificate)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		KeyStore server = KeyStore.getInstance("PKCS12");
		server.load(null, PASSWORD);
		server.setCertificateEntry("server", certificate);
		server.store(new FileOutputStream(new File("nodejstest/server.p12")), PASSWORD);
	}

	private static byte[] buildCACertificate(PublicKey publicKey, PrivateKey privateKey)
			throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {

		byte[] keyIdentifier = KeyIdentifierUtils.getKeyIdentifier(publicKey);

		X509Builder b = new X509Builder();
		b.setSubjectKeyIdentifier(keyIdentifier);
		b.setAuthorityKeyIdentifier(keyIdentifier);
		b.setEncodedPublicKey(publicKey.getEncoded());
		Common.bigInteger(Common.bytes("a3f2e2d38c74f478"));
		b.setSerial(BigInteger.valueOf(rnd.nextLong()));
		b.setIssuerName("CN=localhost, O=Client Certificate Demo");
		b.setSubjectName("CN=localhost, O=Client Certificate Demo");
		b.setNotBefore(date(2018, Calendar.JULY, 25, 07, 24, 01));
		b.setNotAfter(date(2019, Calendar.JULY, 25, 07, 24, 01));
		b.setCA(true);
		return b.encode(privateKey);
	}

	private static byte[] buildAliceCertificate(PublicKey alicePublicKey, PublicKey caPulicKey, PrivateKey caPrivateKey)
			throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {

		byte[] aliceKeyIdentifier = KeyIdentifierUtils.getKeyIdentifier(alicePublicKey);
		byte[] caKeyIdentifier = KeyIdentifierUtils.getKeyIdentifier(caPulicKey);

		X509Builder b = new X509Builder();
		b.setSubjectKeyIdentifier(aliceKeyIdentifier);
		b.setAuthorityKeyIdentifier(caKeyIdentifier);
		b.setEncodedPublicKey(alicePublicKey.getEncoded());
		b.setSerial(BigInteger.valueOf(rnd.nextLong()));
		b.setIssuerName("CN=localhost, O=Client Certificate Demo");
		b.setSubjectName("CN=Alice");
		b.setNotBefore(date(2018, Calendar.JULY, 25, 07, 24, 01));
		b.setNotAfter(date(2019, Calendar.JULY, 25, 07, 24, 01));
		b.setCA(true);
		return b.encode(caPrivateKey);
	}

	private static Date date(int year, int month, int day, int hour, int minute, int second) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(0);
		cal.set(year, month, day, hour, minute, second);
		return cal.getTime();
	}

}
