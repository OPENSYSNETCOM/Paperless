package com.opensysnet.paperless.scheduler;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.opensysnet.paperless.mapper.PaperlessMapper;
import com.opensysnet.paperless.model.KakaoGeoRes;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import com.mashape.unirest.http.JsonNode;


@Slf4j
@Service
public class StoreCheckScheduler {

    @Autowired
    private PaperlessMapper paperlessMapper;

    @Async
    @Scheduled(cron = "0 0 0 * * *")
    public void StoreCheckScheduler() throws  Exception {
        log.debug("************************ StoreCheckScheduler service start ************************");
        try {
            HashMap<String, Object> param = new HashMap<>();

            Integer last_seq = paperlessMapper.selectLastStoreSeq();
            log.debug("COMPANY_POSITION LAST SEQ : " + last_seq);
            if (last_seq == null) last_seq = 0;
            param.put("seq", last_seq);
            List<LinkedHashMap<String, Object>> store_list = paperlessMapper.selectStoreInfoAll(param);
            int insert_count = 0;
            for (LinkedHashMap<String, Object> store_info : store_list) {

                if ( !store_info.containsKey("address") || !store_info.containsKey("organ")) continue;

                String address = URLEncoder.encode((String)store_info.get("address"), "UTF-8");
                String req_url = "https://maps.googleapis.com/maps/api/geocode/json?address=" +
                        address + "&region=kr&key=AIzaSyCxqH24pReNBJCii5VREqu5I3yc_bf6Fbc";

                HttpResponse<JsonNode> response = Unirest.get(req_url).asJson();

                if ( response.getStatusText().equals("OK") && response.getStatus() == 200) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

                    JSONObject jsonObj = new JSONObject(response.getBody().toString());
                    JSONArray json_arr = jsonObj.getJSONArray("results");
                    int i;
                    log.debug("json_arr.length :" + json_arr.length());
                    for (i = 0; i < json_arr.length(); i++) {
                        JSONObject json = json_arr.getJSONObject(i);
                        JSONObject geoMetryObject = json.getJSONObject("geometry");
                        String location_type = geoMetryObject.getString("location_type");
                        if (json_arr.length() > 1 && !location_type.equals("ROOFTOP")) continue;
                        JSONObject locations = geoMetryObject.getJSONObject("location");
                        String lat = locations.getString("lat");
                        String lon = locations.getString("lng");

                        HashMap<String, Object> insert_param = new HashMap<>();
                        insert_param.put("seq", store_info.get("seq"));
                        insert_param.put("organ", store_info.get("organ"));
                        insert_param.put("code", store_info.get("code"));
                        insert_param.put("address", store_info.get("address"));
                        insert_param.put("lat", lat );
                        insert_param.put("lon", lon);

                        log.debug("lat : " + lat + ", lot : " + lon);

                        paperlessMapper.insertStorePositionInfo(insert_param);
                        insert_count++;
                        break;

                    }
                }
                if (insert_count > 30) break;
                Thread.sleep(500);
            }
        }  catch (Exception e) {
            e.printStackTrace();
        }
        log.debug("************************ StoreCheckScheduler service end ************************");

    }
}
