package kr.ac.kopo.cjj.myapp.repository;

import java.util.Optional;
import kr.ac.kopo.cjj.myapp.domain.TechStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TechStackRepository extends JpaRepository<TechStack, Long> {
    boolean existsByNameIgnoreCase(String name);
    Optional<TechStack> findByNameIgnoreCase(String name);
}
