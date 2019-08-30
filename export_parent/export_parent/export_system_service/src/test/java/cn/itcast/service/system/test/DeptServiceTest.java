package cn.itcast.service.system.test;

import cn.itcast.domain.system.Dept;
import cn.itcast.service.system.DeptService;
import com.github.pagehelper.PageInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:spring/applicationContext-*.xml")
public class DeptServiceTest {

    // 注入dao
    @Autowired
    private DeptService deptService;

    @Test
    public void find(){
        PageInfo<Dept> pageInfo = deptService.findByPage("1", 1, 10);
        System.out.println(pageInfo);
    }
}
