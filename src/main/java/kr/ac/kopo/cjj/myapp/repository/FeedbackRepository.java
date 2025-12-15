package kr.ac.kopo.cjj.myapp.repository;

import java.util.List;
import kr.ac.kopo.cjj.myapp.domain.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByPortfolioIdOrderByCreatedAtDesc(Long portfolioId);
}
