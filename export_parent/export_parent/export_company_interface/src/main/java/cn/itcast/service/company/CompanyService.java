package cn.itcast.service.company;

import cn.itcast.domain.company.Company;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface CompanyService {
    /**
     * 查询所有企业
     */
    List<Company> findAll();

    /**
     * 添加
     * @param company
     */
    void save(Company company);

    /**
     * 修改
     * @param company
     */
    void update(Company company);

    /**
     * 主键查询
     * @param id 主键
     * @return 返回用户对象
     */
    Company findById(String id);

    /**
     * 删除
     * @param id
     */
    void delete(String id);

    /**
     * 分页查询
     * @param pageNum   当前页
     * @param pageSize  页大小
     */
    PageInfo<Company> findByPage(int pageNum, int pageSize);

}
