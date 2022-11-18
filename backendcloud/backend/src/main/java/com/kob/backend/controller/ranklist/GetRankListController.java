package com.kob.backend.controller.ranklist;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.service.ranklist.GetRankListService;
import com.kob.backend.service.record.GetRecordListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetRankListController {
    @Autowired
    private GetRankListService getRankListService;

    @GetMapping("/api/ranklist/getList")
    public JSONObject getList(@RequestParam MultiValueMap<String,String> data){
        Integer page = Integer.parseInt(data.getFirst("page"));
        return getRankListService.getList(page);
    }
}
