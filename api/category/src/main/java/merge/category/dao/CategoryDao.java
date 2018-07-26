package merge.category.dao;

import java.util.List;

import merge.category.domain.CategoryBean;

public interface CategoryDao {
	CategoryBean getParentCategory(String userid);

	CategoryBean getCategoryById(int cid);
	
	List<CategoryBean> getCategoryByParentId(int cid);
}
