package eu.zdenet.jms;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Created by Zdeno on 11/13/2016.
 */
@Path("jms")
@Stateless
public class RestEndPOint {
    @Inject
    Producer producer;

    @GET
    public void start(){
        producer.addMessage();
    }
}
