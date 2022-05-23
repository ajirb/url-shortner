package com.qrly.app.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qrly.app.common.URLValidator;
import com.qrly.app.service.QRService;
import com.qrly.app.service.URLConverterService;


@RestController
public class URLController {
    private static final Logger LOGGER = LoggerFactory.getLogger(URLController.class);
    private final URLConverterService urlConverterService;

    public URLController(URLConverterService urlConverterService) {
        this.urlConverterService = urlConverterService;
    }

    @CrossOrigin
    @RequestMapping(value = "/shortener", method=RequestMethod.POST, consumes = {"application/json"})
    public String shortenUrl(@RequestBody @Valid final UserRequest shortenRequest, HttpServletRequest request) throws Exception {
        LOGGER.info("Received url to shorten: " + shortenRequest.getUrl());
        String longUrl = shortenRequest.getUrl();
        if (URLValidator.INSTANCE.validateURL(longUrl)) {
            String localURL = request.getRequestURL().toString();
            String shortenedUrl = urlConverterService.shortenURL(localURL, shortenRequest.getUrl());
            LOGGER.info("Shortened url to: " + shortenedUrl);
            return shortenedUrl;
        }
        throw new Exception("Please enter a valid URL");
    }
    
    @CrossOrigin
	@PostMapping(value = "/QR", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<BufferedImage> barbecueEAN13Barcode(@RequestBody @Valid final UserRequest qRRequest, HttpServletRequest request)
    throws Exception {
		LOGGER.info("Received url to change to barcode : " + qRRequest);
        return new ResponseEntity<BufferedImage>(QRService.generateQRCodeImage(qRRequest.getUrl()), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method=RequestMethod.GET)
    public RedirectView redirectUrl(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) throws IOException, URISyntaxException, Exception {
        LOGGER.info("Received shortened url to redirect: " + id);
        String redirectUrlString = urlConverterService.getLongURLFromID(id);
        LOGGER.info("Original URL: " + redirectUrlString);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("http://" + redirectUrlString);
        return redirectView;
    }
}

class UserRequest{
    private String url;

    @JsonCreator
    public UserRequest() {

    }

    @JsonCreator
    public UserRequest(@JsonProperty("url") String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}


