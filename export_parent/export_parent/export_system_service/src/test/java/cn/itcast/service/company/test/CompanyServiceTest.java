package cn.itcast.service.company.test;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration({"classpath:spring/applicationContext-tx.xml",
//        "classpath:spring/applicationContext-dao.xml"})
@ContextConfiguration("classpath*:spring/applicationContext-*.xml")
public class CompanyServiceTest {

   //// 注入dao
   //@Autowired
   //private CompanyService companyService;

   //@Test
   //public void find(){
   //    PageInfo<Company> pageInfo = companyService.findByPage(1, 2);
   //    System.out.println(pageInfo);
   //}
}
