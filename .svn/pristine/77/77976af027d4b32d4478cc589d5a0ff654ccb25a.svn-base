package merge.category.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import merge.category.dao.CategoryDao;
import merge.category.domain.CategoryBean;

@Service
public class CategorySerivce {

	@Resource
	private CategoryDao categoryDao;

	public CategoryBean getCategoryById(Integer cid) {
		return categoryDao.getCategoryById(cid);
	}
	
	public List<CategoryBean> getCategoryByParentId(Integer cid) {
		return categoryDao.getCategoryByParentId(cid);
	}
}
