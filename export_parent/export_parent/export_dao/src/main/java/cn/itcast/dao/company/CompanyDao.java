package cn.itcast.dao.company;

import cn.itcast.domain.company.Company;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CompanyDao {
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
     * @param id
     * @return
     */
    Company findById(String id);

    /**
     * 删除
     * @param id
     */
    void delete(String id);
}
