package eu.diacron.crawlservice.rest;

import eu.diacron.crawlservice.scheduler.CrawlStatusJobListener;
import eu.diacron.crawlservice.scheduler.CrawlStatusJob;
import eu.diacron.crawlservice.app.Util;
import eu.diacron.crawlservice.config.ConfigController;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.matchers.KeyMatcher;

//http://localhost:8080/Diacrawl/rest/post
@Path("/crawl")
public class DiacrawlRestService {

    @POST
    @Path("/getid")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response getcrawlid(String pageToCrawl) {

        String crawlid = null;

        try {

            ConfigController configController = ConfigController.getInstance();
            configController.readProperties();
            // STEP 1: create new crawl process for a specific url 
            System.out.println("--- " + pageToCrawl);
            crawlid = Util.getCrawlid(new URL(pageToCrawl));

            System.out.println("Crawl page: " + pageToCrawl + " with ID: " + crawlid);

        } catch (MalformedURLException ex) {
            Logger.getLogger(DiacrawlRestService.class.getName()).log(Level.SEVERE, null, ex);

            return Response.status(400).entity("Page to Crawl has malformed url").build();
        }

        return Response.status(201).entity(crawlid).build();

    }

    @POST
    @Path("/initcrawl")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response initcrawl(String crawlid) {

        try {
            ConfigController configController = ConfigController.getInstance();
            configController.readProperties();
            JobDetail job = JobBuilder.newJob(CrawlStatusJob.class).withIdentity(crawlid, "checkCrawlStatus").build();
            job.getJobDataMap().put("CRAWL_ID", crawlid);
            job.getJobDataMap().put("status", "");

            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(crawlid, "checkCrawlStatus").withSchedule(
                    SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInSeconds(120).repeatForever())
                    .build();
            //withIntervalInSeconds(5)
            // schedule it
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();

            //Listener attached to jobKey
            scheduler.getListenerManager().addJobListener(
                    new CrawlStatusJobListener(), KeyMatcher.keyEquals(job.getKey())
            );

            scheduler.start();
            scheduler.scheduleJob(job, trigger);

        } catch (SchedulerException ex) {
            Logger.getLogger(DiacrawlRestService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Response.status(201).entity(crawlid).build();

    }

    @GET
    @Path("/getjobs")
    public Response getcrawljobs() {
        ConfigController configController = ConfigController.getInstance();
        configController.readProperties();

        String result = "";

        try {
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();

            for (String groupName : scheduler.getJobGroupNames()) {

                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {

                    String jobName = jobKey.getName();
                    String jobGroup = jobKey.getGroup();

                    //get job's trigger
                    List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
                    Date nextFireTime = triggers.get(0).getNextFireTime();

                    System.out.println("[jobName] : " + jobName + " [groupName] : "
                            + jobGroup + " - " + nextFireTime);

                    result += "[jobName] : " + jobName + " [groupName] : " + jobGroup + " - " + nextFireTime;

                }

            }

        } catch (SchedulerException ex) {
            Logger.getLogger(DiacrawlRestService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Response.status(200).entity(result).build();

    }

}
