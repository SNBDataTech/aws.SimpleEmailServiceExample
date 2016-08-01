package com.snbdatatech.aws.ses.service.AwsSesService.impl;

import com.amazonaws.services.simpleemail.*;
import com.amazonaws.services.simpleemail.model.*;
import com.amazonaws.regions.*;
import com.snbdatatech.aws.ses.service.AwsConfigurationService.AwsConfigurationService;
import com.snbdatatech.aws.ses.service.AwsSesService.AwsSesService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by sblindt on 7/29/2016.
 */
@Service
public class AwsSesServiceImpl implements AwsSesService {

    @Value("${aws.ses.defaultFromAddress}")
    private String defaultFromAddress;

    @Value("${aws.ses.region}")
    private String region;

    @Autowired
    private AwsConfigurationService configurationService;

    // Create logger
    private final Logger logger = LogManager.getLogger(AwsSesServiceImpl.class);

    /**
     * Send email using AWS SES service
     *
     * @param fromAddress String of the address the email is coming from
     * @param toAddresses String array of addresses to send to
     * @param ccAddresses String array of cc addresses to send to
     * @param bccAddresses String array of bcc addresses to send to
     * @param subject the subject of the message
     * @param content the content of the message
     * @return true if the email was sent successfully
     */
    public boolean sendEmail(String fromAddress, String[] toAddresses, String[] ccAddresses, String[] bccAddresses, String subject, String content) {

        // Verify the from address is set
        if ((fromAddress == null) || (fromAddress.equals(""))) {
            fromAddress = this.defaultFromAddress;
        }

        // Create the destination
        Destination destination = this.createEmailDestination(toAddresses, ccAddresses, bccAddresses);

        // Create the message
        Message message = this.createEmailMessage(subject, content);

        // Build the Email request
        SendEmailRequest request = new SendEmailRequest().withSource(fromAddress).withDestination(destination).withMessage(message);

        try
        {
            // Create an AWS SES client
            AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient(this.configurationService.getAwsCredentials());

            // Create and set the needed AWS region
            Region REGION = Region.getRegion(Regions.valueOf(this.region));
            client.setRegion(REGION);

            // Send the email and get the result.
            SendEmailResult result = client.sendEmail(request);

            // Log the Send Result - MessageId
            logger.error(result);

            // Email request was successful
            return true;
        }
        catch (Exception ex)
        {
            // Log request exception
            this.logger.error(ex.getMessage());

            // Failed to send email request
            return false;
        }
    }

    /**
     * Create a destination for the email
     *
     * @param toAddresses String array of addresses to send to
     * @param ccAddresses String array of cc addresses to send to
     * @param bccAddresses String array of bcc addresses to send to
     * @return the destination of the email
     */
    private Destination createEmailDestination(String[] toAddresses, String[] ccAddresses, String[] bccAddresses) {

        // Create destination for recipients
        Destination destination = new Destination().withToAddresses(toAddresses);

        // Determine if cc addresses need to be added
        if ((ccAddresses != null) && (ccAddresses.length > 0)) {

            // Add cc addresses to destination
            destination.withCcAddresses(ccAddresses);
        }

        // Determine if bcc addresses need to be added
        if ((bccAddresses != null) && (bccAddresses.length > 0)) {

            // Add bcc addresses to destination
            destination.withBccAddresses(bccAddresses);
        }

        // Return the message destination
        return destination;
    }

    /**
     * Create a message for the email
     *
     * @param subject the subject of the email
     * @param content the content of the email
     * @return the message of the email
     */
    private Message createEmailMessage(String subject, String content) {

        // Create the subject of the message
        Content subjectContent = new Content().withData(subject);

        // Create the body of the message
        Content bodyContent = new Content().withData(content);
        Body body = new Body().withText(bodyContent);

        // Create a message using the subject and body.
        return new Message().withSubject(subjectContent).withBody(body);
    }
}
