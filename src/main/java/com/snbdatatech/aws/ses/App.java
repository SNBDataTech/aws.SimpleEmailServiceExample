package com.snbdatatech.aws.ses;

import com.snbdatatech.aws.ses.controller.AwsSesController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by sblindt on 7/29/2016.
 */
public class App {

    // Create an application context
    private static ApplicationContext context;

    // Create logger
    private static final Logger LOGGER = LogManager.getLogger(App.class);

    public static void main(String[] args) {

        // Create the context to load Bean configuration file
        context = new ClassPathXmlApplicationContext("beans.xml");

        // Create controller to process AWS SES request
        AwsSesController controller = (AwsSesController) context.getBean("appController");

        // Determine if email sent correctly
        if (controller.processSendEmailRequest()) {
            LOGGER.info("EMAIL SUCCESSFULLY SENT EMAIL VIA AWS SES");
        } else {
            LOGGER.info("EMAIL WAS NOT SUCCESSFULLY SENT EMAIL VIA AWS SES");
        }

        // Create Hook to destroy all active Beans
        ((AbstractApplicationContext) context).registerShutdownHook();

        // Close the context
        ((ClassPathXmlApplicationContext)context).close();
    }
}
