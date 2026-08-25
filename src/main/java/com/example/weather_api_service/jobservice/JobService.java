package com.example.weather_api_service.jobservice;

import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;


@Service
public class JobService {


    private final StringRedisTemplate redisTemplate;


    public JobService(StringRedisTemplate redisTemplate){
        this.redisTemplate=redisTemplate;
    }



    public String publishJob(String payLoad){

        String jobId= UUID.randomUUID().toString();

        MapRecord<String,String,String> record=MapRecord.create(
                "jobs",
                Map.of(
                        "jobId",jobId,
                        "payLoad",payLoad
                )
        );
        redisTemplate.opsForStream().add(record);
        return jobId;




    }
}
