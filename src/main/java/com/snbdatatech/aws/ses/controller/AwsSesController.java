package com.snbdatatech.aws.ses.controller;

import com.snbdatatech.aws.ses.service.AwsSesService.AwsSesService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by sblindt on 7/29/2016.
 */
public class AwsSesController {

    @Autowired
    AwsSesService awsSesService;

    // Create logger
    private final Logger logger = LogManager.getLogger(AwsSesController.class);

    public boolean processSendEmailRequest() {

        return this.awsSesService.sendEmail("from@example.com", new String[]{"to@example.com"},
                new String[]{"cc@example.com"}, new String[]{"bcc@example.com"}, "Test From JAVA SDK", "This is a test using the Java AWS SDK!!!");
    }
}
