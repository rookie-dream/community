package com.rookie.community.service;

import java.util.Date;

public interface DataService {

    //将指定的IP计入UV
    void recordUV(String ip);
    //统计指定日期内的UV
    long calculateUV(Date start,Date end);

    //将指定的用户计入UV
    void recordDAU(int userId);
    //统计指定日期内的DAU
    long calculateDAU(Date start,Date end);
}
