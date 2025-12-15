package kr.ac.kopo.cjj.myapp.repository;

import java.util.List;
import kr.ac.kopo.cjj.myapp.domain.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    List<Portfolio> findByOwnerUsername(String username);
    List<Portfolio> findByVisibleTrue();
}
