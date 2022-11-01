package com.example.demo.Parser;

import com.example.demo.dao.HospitalDao;
import com.example.demo.domain.Hospital;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HospitalParserTest{
    String line1 = "\"1\",\"의원\",\"01_01_02_P\",\"3620000\",\"PHMA119993620020041100004\",\"19990612\",\"\",\"01\",\"영업/정상\",\"13\",\"영업중\",\"\",\"\",\"\",\"\",\"062-515-2875\",\"\",\"500881\",\"광주광역시 북구 풍향동 565번지 4호 3층\",\"광주광역시 북구 동문대로 24, 3층 (풍향동)\",\"61205\",\"효치과의원\",\"20211115113642\",\"U\",\"2021-11-17 02:40:00.0\",\"치과의원\",\"192630.735112\",\"185314.617632\",\"치과의원\",\"1\",\"0\",\"0\",\"52.29\",\"401\",\"치과\",\"\",\"\",\"\",\"0\",\"0\",\"\",\"\",\"0\",\"\",";

    @Autowired
    ApplicationContext context;
    ReadLineContext<Hospital> hospitalReadLineContext;

    @Autowired
    HospitalDao hospitalDao;

    @BeforeEach
    void setUp(){
        this.hospitalReadLineContext = context.getBean("hospitalReadLineContext", ReadLineContext.class);
    }

    @Test
    @DisplayName("Hospital이 insert가 잘 되는지")
    void add(){
        HospitalParser hp = new HospitalParser();
        Hospital hospital = hp.parse(line1);
        hospitalDao.add(hospital);
    }

//    @Test
//    @DisplayName("Hospital이 select가 잘 되는지")
//    void findById(){
//        HospitalParser hp = new HospitalParser();
//        Hospital hospital = hp.parse(line1);
//        hospitalDao.deleteAll();
//        hospitalDao.add(hospital);
//
//        Hospital selectHospital = hospitalDao.findById(1);
//        Assertions.assertEquals("효치과의원",selectHospital.getHospitalName());
//
//    }

    @Test
    @DisplayName("getCount가 잘 되는지")
    void getCount(){
        HospitalParser hp = new HospitalParser();
        Hospital hospital = hp.parse(line1);

        hospitalDao.deleteAll();
        int count1 = hospitalDao.getCount();
        Assertions.assertEquals(0,count1);

        hospitalDao.add(hospital);
        int count2 = hospitalDao.getCount();
        Assertions.assertEquals(1,count2);
    }

    @Test
    @DisplayName("10만건 이상 데이터가 파싱 되는지")
    void oneHudreadThousandDatas() throws IOException {
        String filename = "D:\\backendSchool\\git\\fulldata_01_01_02_P_의원.csv";
        List<Hospital> hospitalList = hospitalReadLineContext.readByLine(filename);
        assertTrue(hospitalList.size() > 10000);
        assertTrue(hospitalList.size() > 100000);
        for(int i=0; i<10; i++){
            System.out.println(hospitalList.get(i).getHospitalName());
        }
    }
    @Test
    @DisplayName("csv 1줄을 hospital구현체롤 잘 만드는지")
    void testconvert(){
        HospitalParser hp = new HospitalParser();
        Hospital hospital = hp.parse(line1);

        assertEquals(1, hospital.getId());
        assertEquals("의원", hospital.getOpenServiceName());
        assertEquals(3620000,hospital.getOpenLocalGovernmentCode());
        assertEquals("PHMA119993620020041100004",hospital.getManagementNumber());
        assertEquals(LocalDateTime.of(1999, 6, 12, 0, 0, 0), hospital.getLicenseDate()); //19990612
        assertEquals(1, hospital.getBusinessStatus());
        assertEquals(13, hospital.getBusinessStatusCode());
        assertEquals("062-515-2875", hospital.getPhone());
        assertEquals("광주광역시 북구 풍향동 565번지 4호 3층", hospital.getFullAddress());
        assertEquals("광주광역시 북구 동문대로 24, 3층 (풍향동)", hospital.getRoadNameAddress());
        assertEquals("효치과의원", hospital.getHospitalName());
        assertEquals("치과의원", hospital.getBusinessTypeName());
        assertEquals(1, hospital.getHealthcareProviderCount());
        assertEquals(0, hospital.getPatientRoomCount());
        assertEquals(0, hospital.getTotalNumberOfBeds());
        assertEquals(52.29f, hospital.getTotalAreaSize());
    }


}