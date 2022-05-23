package com.qrly.app.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import net.glxn.qrgen.javase.QRCode;


public class QRService {
	public static BufferedImage generateQRCodeImage(String barcodeText) throws Exception {
	    ByteArrayOutputStream stream = QRCode
	      .from(barcodeText)
	      .withSize(250, 250)
	      .stream();
	    ByteArrayInputStream bis = new ByteArrayInputStream(stream.toByteArray());

	    return ImageIO.read(bis);
	}
}
