package kr.ac.kopo.cjj.myapp.repository;

import java.util.Optional;
import kr.ac.kopo.cjj.myapp.domain.FeedbackTheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackThemeRepository extends JpaRepository<FeedbackTheme, Long> {
    boolean existsByNameIgnoreCase(String name);
    Optional<FeedbackTheme> findByNameIgnoreCase(String name);
}
