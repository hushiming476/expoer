package cn.itcast.dao.stat;

import cn.itcast.dao.cargo.FactoryDao;
import cn.itcast.domain.cargo.Factory;
import cn.itcast.domain.cargo.FactoryExample;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext-dao.xml")
public class StatDaoTest {
    // 注入dao
    @Autowired
    private StatDao statDao;

    @Test
    public void factoryData(){
        //[{name=会龙, value=26201.40}, {name=光华, value=254035.00}]
        System.out.println(statDao.getFactoryData("1"));
    }
}















