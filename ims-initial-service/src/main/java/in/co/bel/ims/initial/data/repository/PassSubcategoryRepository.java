package in.co.bel.ims.initial.data.repository;

import java.util.List;

import in.co.bel.ims.initial.entity.PassSubcategory;
import in.co.bel.ims.initial.service.fwk.ImsJPATemplate;

public interface PassSubcategoryRepository extends ImsJPATemplate<PassSubcategory> {

	List<PassSubcategory> findByPassCategoryIdAndDeleted(int passCategoryId, boolean b);

	PassSubcategory findByIdAndDeleted(int passSubcategoryId, boolean b);
   
}

