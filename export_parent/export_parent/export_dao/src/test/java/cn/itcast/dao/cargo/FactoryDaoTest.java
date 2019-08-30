package cn.itcast.dao.cargo;

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
public class FactoryDaoTest {
    // 注入dao
    @Autowired
    private FactoryDao factoryDao;

    /**
     * 测试保存（1）普通保存
     * insert into co_factory (id, ctype, full_name, factory_name, contacts,
     * phone, mobile, fax, address, inspector, remark, order_no, state,
     * create_by, create_dept, create_time, update_by, update_time )
     * values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )
     */
    @Test
    public void save(){
        Factory factory = new Factory();
        factory.setId(UUID.randomUUID().toString());
        factory.setFactoryName("测试-工厂");
        factory.setCreateTime(new Date());
        factory.setUpdateTime(new Date());
        //factoryDao.insert(factory);
    }

    /**
     * 测试保存（2）动态保存
     * insert into co_factory ( id, factory_name, create_time, update_time )
     * values ( ?, ?, ?, ? )
     */
    @Test
    public void saveSelective(){
        Factory factory = new Factory();
        factory.setId(UUID.randomUUID().toString());
        factory.setFactoryName("测试-工厂");
        factory.setCreateTime(new Date());
        factory.setUpdateTime(new Date());
        factoryDao.insertSelective(factory);
    }

    /**
     * 动态条件查询
     */
    @Test
    public void selectByExample(){
        FactoryExample example = new FactoryExample();
        // 构造条件：工厂名称 factory_name=民鑫
        FactoryExample.Criteria criteria = example.createCriteria();
        criteria.andFactoryNameEqualTo("民鑫");
        criteria.andFullNameEqualTo("");
        List<Factory> list = factoryDao.selectByExample(example);
        System.out.println(list);
    }
}















