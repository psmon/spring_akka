package com.psmon.cachedb.data.primary;

import org.springframework.data.jpa.repository.JpaRepository;
import com.psmon.cachedb.data.primary.ItemBuyLog;
import java.util.List;

public interface ItemBuyLogRepository extends JpaRepository<ItemBuyLog, Long>{
	
	List<ItemBuyLog> findByUserinfoAgeBetween(int minage,int maxage);

}
