package com.example.weather_api_service.jobcontroller;


import com.example.weather_api_service.jobrequest.JobRequest;
import com.example.weather_api_service.jobservice.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/job")
public class JobController {


    private final JobService jobService;


    public JobController(JobService jobService){
        this.jobService=jobService;
    }

    @PostMapping
    public ResponseEntity<Map<String,String>> createJob(@RequestBody JobRequest jobRequest){
       String jobId= jobService.publishJob(jobRequest.getPayLoad());
        return ResponseEntity.ok().body(Map.of("jobId",jobId));
    }


}
