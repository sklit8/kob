package com.kob.backend.service.impl.record;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.Record;
import com.kob.backend.pojo.User;
import com.kob.backend.service.record.GetRecordListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class GetRecordListServiceImpl implements GetRecordListService {

    @Autowired
    private RecordMapper recordMapper;
    @Autowired
    private UserMapper userMapper;
    @Override
    public JSONObject getList(Integer page) {
        IPage<Record> recordIPage = new Page<>(page,10);
        QueryWrapper<Record> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        List<Record> records = recordMapper.selectPage(recordIPage,queryWrapper).getRecords();
        JSONObject resp = new JSONObject();
        List<JSONObject> items = new LinkedList<>();
        for(Record record : records) {
            User userA = userMapper.selectById(record.getBId());
            User userB = userMapper.selectById(record.getAId());

            JSONObject item = new JSONObject();
            item.put("a_username", userA.getUsername());
            item.put("b_username", userB.getUsername());
            item.put("a_photo", userA.getPhoto());
            item.put("b_photo", userB.getPhoto());
            String loser = "平局";
            if("A".equals(record.getLoser())) loser = "B胜";
            else if("B".equals(record.getLoser())) loser = "A胜";
            item.put("result",loser);
            item.put("createTime",record.getCreatetime());
            item.put("record", record);
            items.add(item);
        }
        resp.put("records",items);
        resp.put("records_count",recordMapper.selectCount(null));
        return resp;
    }
}
