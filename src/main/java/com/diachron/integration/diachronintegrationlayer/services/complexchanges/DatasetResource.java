/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.diachron.integration.diachronintegrationlayer.services.complexchanges;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.athena.imis.diachron.archive.api.QueryLib;
import org.diachron.detection.exploit.ChangesExploiter;
import org.diachron.detection.exploit.DetChange;

/**
 * REST Web Service
 *
 * @author Jim
 */
@Path("dataset")
public class DatasetResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of DatasetResource
     */
    public DatasetResource() {
    }

    @GET
    @Path("/")
    public Response getDataset() {
        QueryLib querylib = new QueryLib();
        try {
            String response = querylib.listDiachronicDatasets();
            return Response.status(Response.Status.OK).entity(response).build();
        } catch (Exception ex) {
            Logger.getLogger(DatasetResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error fetching the dataset").build();
        }
    }
    
    @GET
    @Path("{id}")
    public Response getDatasetById(@PathParam("id") String id) {
        QueryLib querylib = new QueryLib();
        String response = querylib.getDatasetVersionById(id);
        return Response.status(Response.Status.OK).entity(response).build();
    }
    
    @GET
    @Path("{id}/changes")
    public Response getChangesBetweeenVersions(@PathParam("id") String id,
            @QueryParam("from") String from,
            @QueryParam("to") String to,
            @QueryParam("id") String _id) {
        Properties prop = new Properties();
        InputStream inputStream;
        try {
            inputStream = new FileInputStream("/tmp/config.properties");
            prop.load(inputStream);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DatasetResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DatasetResource.class.getName()).log(Level.SEVERE, null, ex);
        }

        String json="";
        try {        
            ChangesExploiter exploiter = new ChangesExploiter(prop, _id, true);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Set<DetChange> changeSet = exploiter.fetchChangesBetweenVersions(from, to, null, null, 1000);
            json = gson.toJson(changeSet);
        } catch (Exception ex) {
            Logger.getLogger(DatasetResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.BAD_REQUEST).entity("Bad Request").build();
        }
        return Response.status(Response.Status.OK).entity(json).build();
    }

    /**
     * PUT method for updating or creating an instance of DatasetResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/xml")
    public void putXml(String content) {
    }
}
