package com.example.demo.Parser;

import com.example.demo.dao.HospitalDao;
import com.example.demo.domain.Hospital;
import com.example.demo.service.HospitalService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class HospitalParserTest{
    String line1 = "\"1\",\"의원\",\"01_01_02_P\",\"3620000\",\"PHMA119993620020041100004\",\"19990612\",\"\",\"01\",\"영업/정상\",\"13\",\"영업중\",\"\",\"\",\"\",\"\",\"062-515-2875\",\"\",\"500881\",\"광주광역시 북구 풍향동 565번지 4호 3층\",\"광주광역시 북구 동문대로 24, 3층 (풍향동)\",\"61205\",\"효치과의원\",\"20211115113642\",\"U\",\"2021-11-17 02:40:00.0\",\"치과의원\",\"192630.735112\",\"185314.617632\",\"치과의원\",\"1\",\"0\",\"0\",\"52.29\",\"401\",\"치과\",\"\",\"\",\"\",\"0\",\"0\",\"\",\"\",\"0\",\"\",";
    String line2 = "\"2\",\"의원\",\"01_01_02_P\",\"3620000\",\"PHMA119993620020041100005\",\"19990707\",\"\",\"01\",\"영업/정상\",\"13\",\"영업중\",\"\",\"\",\"\",\"\",\"062-574-2802\",\"\",\"500867\",\"광주광역시 북구 일곡동 821번지 1호 2층\",\"광주광역시 북구 설죽로 518, 2층 (일곡동)\",\"61041\",\"일곡부부치과의원\",\"20170905183213\",\"I\",\"2018-08-31 23:59:59.0\",\"치과의원\",\"190646.777107\",\"189589.427851\",\"치과의원\",\"2\",\"0\",\"0\",\"200\",\"401\",\"치과\",\"\",\"\",\"\",\"0\",\"0\",\"\",\"\",\"0\",\"\",";
    String line3 = "\"3\",\"의원\",\"01_01_02_P\",\"3620000\",\"PHMA119993620020041100006\",\"19990713\",\"\",\"01\",\"영업/정상\",\"13\",\"영업중\",\"\",\"\",\"\",\"\",\"062-575-2875\",\"\",\"\",\"광주광역시 북구 일곡동 841번지 6호\",\"광주광역시 북구 설죽로 495, 3층 (일곡동)\",\"61040\",\"사랑이가득한치과의원\",\"20190730134859\",\"U\",\"2019-08-01 02:40:00.0\",\"치과의원\",\"190645.299575\",\"189353.47816\",\"치과의원\",\"1\",\"0\",\"0\",\"128\",\"401\",\"치과\",\"\",\"\",\"\",\"0\",\"0\",\"\",\"\",\"0\",\"\",";

    private HospitalParser hp = new HospitalParser();
    @Autowired
    ApplicationContext context;
    @Autowired
    ReadLineContext<Hospital> hospitalReadLineContext;

    @Autowired
    HospitalDao hospitalDao;
    @Autowired
    HospitalService hospitalService;

    @BeforeEach
    void setUp(){
        this.hospitalReadLineContext = context.getBean("hospitalReadLineContext", ReadLineContext.class);
    }

    @Test
    @DisplayName("Hospital이 insert가 잘 되는지")
    void add(){
        Hospital hospital = hp.parse(line1);
        hospitalDao.add(hospital);
    }

    @Test
    @DisplayName("Hospital이 select가 잘 되는지")
    void findById(){
        Hospital hospital = hp.parse(line1);
        hospitalDao.deleteAll();
        hospitalDao.add(hospital);

        Hospital selectHospital = hospitalDao.findById(1);
        assertEquals(hospital.getId(),selectHospital.getId());
        assertEquals(hospital.getHospitalName(),selectHospital.getHospitalName());
        assertEquals(hospital.getFullAddress(),selectHospital.getFullAddress());
        assertEquals(hospital.getLicenseDate(), hospital.getLicenseDate());

    }

    @Test
    @DisplayName("getCount가 잘 되는지")
    void getCount(){
        Hospital hospital = hp.parse(line1);

        //삭제되었을때 총 개수는 0개이여야함
        hospitalDao.deleteAll();
        int count1 = hospitalDao.getCount();
        assertEquals(0,count1);

        //하나가 추가되었을 대 총 개수는 1개이여야함
        hospitalDao.add(hospital);
        int count2 = hospitalDao.getCount();
        assertEquals(1,count2);
    }

    @Test
    @DisplayName("getAll로 모든 데이터가 불러와지는지 확인")
    void getAll(){
        hospitalDao.deleteAll();
        Hospital hospital1 = hp.parse(line1);
        Hospital hospital2 = hp.parse(line2);
        Hospital hospital3 = hp.parse(line3);

        hospitalDao.add(hospital1);
        hospitalDao.add(hospital2);
        hospitalDao.add(hospital3);

        List<Hospital> all = hospitalDao.getAll();
        assertEquals(3, all.size());
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



    @Test
    @DisplayName("대용량 데이터에도 잘 파싱이 되는지 Test")
    void oneHundradThousandRowsTest() throws IOException {
        hospitalDao.deleteAll();
        String fileName = "D:\\backendSchool\\git\\fulldata_01_01_02_P_의원.csv";
        int dataCnt = this.hospitalService.insertLargeVolumeHospitalData(fileName);
        assertTrue(dataCnt > 1000);
        assertTrue(dataCnt > 10000);

        System.out.printf("파싱된 데이터의 수: %d", dataCnt);
    }

}