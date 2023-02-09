package in.co.bel.ims.initial.service.fwk;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

@NoRepositoryBean
public interface ImsJPATemplate<T> extends PagingAndSortingRepository<T, Integer>, JpaRepository<T, Integer> {
	
	public List<T> findAllByDeleted(Sort sort, boolean isDeleted);
	public List<T> findAllByDeleted(boolean isDeleted);
	
}